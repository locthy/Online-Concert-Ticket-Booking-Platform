package com.geekup.flashsale.worker;

import com.geekup.flashsale.dto.payload.ReservationItem;
import com.geekup.flashsale.dto.payload.ReservationPayload;
import com.geekup.flashsale.entity.Booking;
import com.geekup.flashsale.entity.TicketCategory;
import com.geekup.flashsale.repository.BookingRepository;
import com.geekup.flashsale.repository.TicketCategoryRepository;
import com.geekup.flashsale.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

/**
 * Kafka consumer that persists booking transactions and prepares payment links.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BookingWorker {

    private final StringRedisTemplate redisTemplate;
    private final BookingRepository bookingRepository;
    private final PaymentService paymentService;
    private final TicketCategoryRepository ticketCategoryRepository;

    /**
     * Consumes reservation events, computes total amount, creates booking record,
     * and stores payment link in Redis.
     *
     * @param payload reservation payload from Kafka
     */
    @Transactional
    @KafkaListener(topics = "booking_events", groupId = "flashsale-group")
    public void consumeBookingEvent(ReservationPayload payload) {
        log.info("[WORKER] Process product from Kafka with QueueID: {}", payload.getQueueTicketId());
        String qid = payload.getQueueTicketId();
        try {
            double totalAmount = 0.0;

            for (ReservationItem item : payload.getItems()) {
                TicketCategory category = ticketCategoryRepository.findById(item.getCategoryId())
                        .orElseThrow(() -> new IllegalArgumentException("Not found category ticket!"));
                totalAmount += (category.getPrice() * item.getQuantity());
            }

            Booking newBooking = new Booking(
                    payload.getQueueTicketId(),
                    payload.getIdempotencyKey(),
                    totalAmount,
                    "PENDING"
            );

            bookingRepository.save(newBooking);
            log.info("[DATABASE] Save to database. ID: {}", newBooking.getId());

            String paymentUrl = paymentService.createVirtualPaymentLink(qid, totalAmount);
            String redisKey = "payment_link:" + qid;
            redisTemplate.opsForValue().set(redisKey, paymentUrl, Duration.ofMinutes(15));
            log.info("[DONE] Payment link ready and stored in Redis for QID: {}", qid);
        } catch (IllegalArgumentException e) {
            // Invalid payload data should be logged and skipped to avoid poison retries.
            log.error("[WORKER ERROR] Invalid data for QueueID {}: {}", qid, e.getMessage());
        } catch (Exception e) {
            log.error("[ERROR] Save transaction: {}", e.getMessage());
            throw e;
        }
    }
}
