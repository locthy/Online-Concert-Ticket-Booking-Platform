package com.geekup.flashsale.dto.payload;

import com.geekup.flashsale.dto.request.CheckoutRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Legacy event payload wrapper for booking events.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingEventPayload {
    private String queueTicketId;
    private String idempotencyKey;
    private CheckoutRequest originalRequest;
}
