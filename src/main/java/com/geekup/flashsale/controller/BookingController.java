package com.geekup.flashsale.controller;

import com.geekup.flashsale.dto.request.CheckoutRequest;
import com.geekup.flashsale.dto.response.CheckoutResponse;
import com.geekup.flashsale.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.redis.core.StringRedisTemplate;

@Slf4j
@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final StringRedisTemplate redisTemplate;
    private final BookingService bookingService;

    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponse> processCheckout(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody CheckoutRequest request) {

        log.info("Receive purchase ticket. Idempotency-Key: {}", idempotencyKey);

        CheckoutResponse response = bookingService.reserveTicket(idempotencyKey, request);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    // implement short polling in frontend (check every 2 mins)
    @GetMapping("/status/{queueTicketId}")
    public ResponseEntity<CheckoutResponse> getBookingStatus(@PathVariable String queueTicketId) {

        // Check if the link exists in Redis
        String paymentUrl = redisTemplate.opsForValue().get("payment_link:" + queueTicketId);

        if (paymentUrl != null) {
            return ResponseEntity.ok(new CheckoutResponse("Payment is ready", queueTicketId, "PAYMENT_READY", paymentUrl));
        }

        return ResponseEntity.ok(new CheckoutResponse("Still processing...", queueTicketId, "PROCESSING", null));
    }
}