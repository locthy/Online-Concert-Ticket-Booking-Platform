package com.geekup.flashsale.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Produces payment links for queued bookings.
 * This implementation is a mock and can be replaced with a real gateway adapter.
 */
@Service
public class PaymentService {

    /**
     * Creates a mock payment URL for a queued booking request.
     *
     * @param queueTicketId booking queue id
     * @param amount total amount to be paid
     * @return mock payment URL
     */
    public String createVirtualPaymentLink(String queueTicketId, double amount) {
        String mockTransactionId = UUID.randomUUID().toString();
        return "https://mock-payment-gateway.com/pay/" + mockTransactionId + "?ref=" + queueTicketId;
    }
}
