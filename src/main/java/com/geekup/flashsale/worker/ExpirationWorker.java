package com.geekup.flashsale.worker;

import com.geekup.flashsale.dto.payload.ReservationItem;
import com.geekup.flashsale.dto.payload.ReservationPayload;
import com.geekup.flashsale.entity.Booking;
import com.geekup.flashsale.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExpirationWorker {

    private final BookingRepository bookingRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    // Run every 30 seconds
    @Scheduled(fixedRate = 30000)
    @Transactional
    public void cleanupExpiredBookings() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(1);

        // Find product PENDING over 5 mins
        List<Booking> expiredBookings = bookingRepository.findAllByStatusAndCreatedAtBefore("PENDING", threshold);

        if (expiredBookings.isEmpty()) return;

        log.info("[EXPIRATION] Found {} expired bookings", expiredBookings.size());

        // Check every reservation
        for (Booking booking : expiredBookings) {
            String reservationKey =
                    "reservation:" + booking.getQueueTicketId();

            ReservationPayload reservation = (ReservationPayload) redisTemplate.opsForValue().get(reservationKey);

            if (reservation == null) {
                log.error("[CRITICAL] Not found any reservation payload for QID: {}.", booking.getQueueTicketId());
                booking.setStatus("CANCELLED_ERROR");
                bookingRepository.save(booking);
                continue;
            }
            // return all the fail tickets to Redis
            for (ReservationItem item : reservation.getItems()) {

                String stockKey =
                        "ticket_stock:" + item.getCategoryId();

                redisTemplate.opsForValue().increment(
                        stockKey,
                        item.getQuantity()
                );
                log.info("[REDIS] Return {} categoryId {} tickets for Concert ID: {} (Order: {})",
                        item.getQuantity(), item.getCategoryId(), item.getConcertId(), booking.getQueueTicketId());
            }
            // Set CANCELLED to the ticket status in database
            booking.setStatus("CANCELLED");

        }
    }
}