package com.geekup.flashsale.service;

import com.geekup.flashsale.dto.payload.ReservationItem;
import com.geekup.flashsale.dto.payload.ReservationPayload;
import com.geekup.flashsale.dto.request.CheckoutRequest;
import com.geekup.flashsale.dto.response.CheckoutResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Handles checkout orchestration across Redis, Kafka, and idempotency control.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedisScript<Long> deductInventoryScript;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String KAFKA_TOPIC = "booking_events";

    /**
     * Reserves tickets by enforcing idempotency, deducting stock in Redis, and publishing
     * an async booking event for background processing.
     *
     * @param idempotencyKey idempotency key from client
     * @param request checkout payload
     * @return immediate queue acknowledgement
     */
    public CheckoutResponse reserveTicket(String idempotencyKey, CheckoutRequest request) {
        log.info("Processing checkout with Idempotency Key: {}", idempotencyKey);

        String idempotencyRedisKey = "idempotency:" + idempotencyKey;
        Boolean isFirstAttempt = redisTemplate.opsForValue()
                .setIfAbsent(idempotencyRedisKey, "PROCESSING", Duration.ofHours(24));

        if (Boolean.FALSE.equals(isFirstAttempt)) {
            log.warn("Duplicate request detected for key: {}", idempotencyKey);
            throw new IllegalStateException("Request is already being processed or completed.");
        }

        boolean isTicketAvailable = checkAndDeductInventoryInRedis(request);
        if (!isTicketAvailable) {
            redisTemplate.delete(idempotencyRedisKey);
            throw new RuntimeException("Tickets are sold out!");
        }

        String queueTicketId = "qt_" + UUID.randomUUID();

        ReservationPayload payload = new ReservationPayload(
                queueTicketId,
                request.getItems().stream()
                        .map(i -> new ReservationItem(i.getCategoryId(), i.getQuantity(), i.getConcertId()))
                        .toList(),
                "PENDING",
                idempotencyKey,
                LocalDateTime.now()
        );

        redisTemplate.opsForValue().set(
                "reservation:" + queueTicketId,
                payload,
                Duration.ofMinutes(15)
        );
        log.info("Save to Redis success. Queue ID: {} {}", queueTicketId, payload);

        kafkaTemplate.send(KAFKA_TOPIC, queueTicketId, payload);
        log.info("Successfully published booking event to Kafka. Queue ID: {}", queueTicketId);

        return new CheckoutResponse(
                "Your booking request is queued and being processed.",
                queueTicketId,
                "PROCESSING",
                null
        );
    }

    /**
     * Performs atomic stock deduction for each item through Lua script execution.
     * Rolls back any successful deductions if one item fails.
     *
     * @param request checkout request
     * @return {@code true} if all deductions succeed, otherwise {@code false}
     */
    private boolean checkAndDeductInventoryInRedis(CheckoutRequest request) {
        List<CheckoutRequest.BookingItemRequest> successfulDeductions = new ArrayList<>();

        for (CheckoutRequest.BookingItemRequest item : request.getItems()) {
            String stockKey = "ticket_stock:" + item.getCategoryId();
            String eventKey = "event:" + item.getConcertId();

            Long result = stringRedisTemplate.execute(
                    deductInventoryScript,
                    Arrays.asList(stockKey, eventKey),
                    String.valueOf(item.getQuantity())
            );

            if (result == null || result != 1L) {
                if (result != null && result == -2L) {
                    log.warn("Checkout failed: Event/Concert {} does not exist or is closed!", item.getConcertId());
                } else if (result != null && result == -1L) {
                    log.warn("Checkout failed: Ticket category {} not found in Redis!", item.getCategoryId());
                } else {
                    log.warn("Checkout failed: Not enough stock for category {}.", item.getCategoryId());
                }

                log.info("Starting ROLLBACK for previously deducted items...");

                for (CheckoutRequest.BookingItemRequest successItem : successfulDeductions) {
                    String rollbackKey = "ticket_stock:" + successItem.getCategoryId();
                    stringRedisTemplate.opsForValue().increment(rollbackKey, successItem.getQuantity());
                    log.info("Rolled back {} tickets for category {}",
                            successItem.getQuantity(), successItem.getCategoryId());
                }
                return false;
            }

            log.info("Deducted {} tickets from category {}", item.getQuantity(), item.getCategoryId());
            successfulDeductions.add(item);
        }

        return true;
    }
}
