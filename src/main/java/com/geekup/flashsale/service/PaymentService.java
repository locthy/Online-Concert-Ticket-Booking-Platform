package com.geekup.flashsale.service;

import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class PaymentService {
    public String createVirtualPaymentLink(String queueTicketId, double amount) {
        // In reality, you would call Stripe/Momo/VNPAY API here
        String mockTransactionId = UUID.randomUUID().toString();
        return "https://mock-payment-gateway.com/pay/" + mockTransactionId + "?ref=" + queueTicketId;
    }
}