package com.geekup.flashsale.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API response model for checkout submission and status polling.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutResponse {
    private String message;
    private String queueTicketId;
    private String status;
    private String paymentUrl;
}
