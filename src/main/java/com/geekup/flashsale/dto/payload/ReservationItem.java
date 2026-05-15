package com.geekup.flashsale.dto.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Reservation line item sent through asynchronous processing.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationItem {
    private Long categoryId;
    private Integer quantity;
    private Long concertId;
}
