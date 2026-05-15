package com.geekup.flashsale.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Client request model for checkout.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequest {

    @NotNull(message = "Items list cannot be null")
    @Valid
    private List<BookingItemRequest> items;

    /**
     * A single requested ticket category and quantity.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingItemRequest {
        @NotNull(message = "Category ID is required")
        private Long categoryId;

        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;

        @NotNull(message = "Concert ID is required")
        private Long concertId;
    }
}
