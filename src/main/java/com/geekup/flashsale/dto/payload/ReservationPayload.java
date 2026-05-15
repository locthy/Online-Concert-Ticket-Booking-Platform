package com.geekup.flashsale.dto.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Reservation message persisted in Redis and published to Kafka.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationPayload {
    private String queueTicketId;
    private List<ReservationItem> items;
    private String status;
    private String idempotencyKey;
    private LocalDateTime createdAt;
}
