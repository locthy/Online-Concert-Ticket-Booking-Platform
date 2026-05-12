package com.geekup.flashsale.dto.payload;

import com.geekup.flashsale.dto.request.CheckoutRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingEventPayload {
    private String queueTicketId;
    private String idempotencyKey;
    private CheckoutRequest originalRequest;
}