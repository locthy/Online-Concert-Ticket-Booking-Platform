package com.geekup.flashsale.service;

import com.geekup.flashsale.dto.payload.BookingEventPayload;
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
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedisScript<Long> deductInventoryScript;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String KAFKA_TOPIC = "booking_events";

    public CheckoutResponse reserveTicket(String idempotencyKey, CheckoutRequest request) {
        log.info("Processing checkout with Idempotency Key: {}", idempotencyKey);

        // 1. Idempotency Check (Prevent duplicate charges)
        String idempotencyRedisKey = "idempotency:" + idempotencyKey;
        Boolean isFirstAttempt = redisTemplate.opsForValue()
                .setIfAbsent(idempotencyRedisKey, "PROCESSING", Duration.ofHours(24));

        if (Boolean.FALSE.equals(isFirstAttempt)) {
            log.warn("Duplicate request detected for key: {}", idempotencyKey);
            throw new IllegalStateException("Request is already being processed or completed.");
            // Note: In a real app, you would handle this gracefully with a GlobalExceptionHandler
        }

        // 2. Redis Inventory Check & Deduction (Atomic Operation Placeholder)
        boolean isTicketAvailable = checkAndDeductInventoryInRedis(request);
        if (!isTicketAvailable) {
            // Rollback the idempotency key so they can try again if it was a false alarm
            redisTemplate.delete(idempotencyRedisKey);
            throw new RuntimeException("Tickets are sold out!");
        }
        // 3. Generate Queue Ticket ID and Send to Kafka
        String queueTicketId = "qt_" + UUID.randomUUID();

        // 4. Create an internal event payload to send to the Kafka Topic
        ReservationPayload payload = new ReservationPayload(
                queueTicketId,
                request.getItems().stream()
                        .map(i -> new ReservationItem(
                                i.getCategoryId(),
                                i.getQuantity(),
                                i.getConcertId()
                        ))
                        .toList(),
                "PENDING",
                idempotencyKey,
                LocalDateTime.now()
        );
        // 5. Save to Redis
        redisTemplate.opsForValue().set(
                "reservation:" + queueTicketId,
                payload,
                Duration.ofMinutes(15) // If worker die, it automatically is deleted
        );
        log.info("Save to Redis success. Queue ID: {} " + payload, queueTicketId);

        // 6. Push to kafka
        kafkaTemplate.send(KAFKA_TOPIC, queueTicketId, payload);
        log.info("Successfully published booking event to Kafka. Queue ID: {}", queueTicketId);

        // 7. Return Immediate 202 Accepted Response
        return new CheckoutResponse(
                "Your booking request is queued and being processed.",
                queueTicketId,
                "PROCESSING",
                null
        );
    }

    // Helper method simulating the Lua Script execution
    private boolean checkAndDeductInventoryInRedis(CheckoutRequest request) {

        List<CheckoutRequest.BookingItemRequest> successfulDeductions = new ArrayList<>();

        for (CheckoutRequest.BookingItemRequest item : request.getItems()) {

            // 1. Prepare BOTH keys
            String stockKey = "ticket_stock:" + item.getCategoryId();
            String eventKey = "event:" + item.getConcertId(); // or "concert:" + item.getConcertId() depending on your naming convention

            // 2. Pass both keys to the Lua script
            Long result = (Long) stringRedisTemplate.execute(
                    deductInventoryScript,
                    Arrays.asList(stockKey, eventKey), // Passing KEYS[1] and KEYS[2]
                    String.valueOf(item.getQuantity()) // Passing ARGV[1]
            );

            // 3. Handle the specific return codes from Lua
            if (result == null || result != 1L) {

                // Log the exact reason for failure
                if (result != null && result == -2L) {
                    log.warn("Checkout failed: Event/Concert {} does not exist or is closed!", item.getConcertId());
                } else if (result != null && result == -1L) {
                    log.warn("Checkout failed: Ticket category {} not found in Redis!", item.getCategoryId());
                } else {
                    log.warn("Checkout failed: Not enough stock for category {}.", item.getCategoryId());
                }

                log.info("Starting ROLLBACK for previously deducted items...");

                // Start ROLLBACK: Return the deducted tickets of each category
                for (CheckoutRequest.BookingItemRequest successItem : successfulDeductions) {
                    String rollbackKey = "ticket_stock:" + successItem.getCategoryId();
                    stringRedisTemplate.opsForValue().increment(rollbackKey, successItem.getQuantity());
                    log.info("Rolled back {} tickets for category {}", successItem.getQuantity(), successItem.getCategoryId());
                }

                return false;
            }

            log.info("Deducted {} tickets from category {}", item.getQuantity(), item.getCategoryId());
            successfulDeductions.add(item);
        }

        return true;
    }

}