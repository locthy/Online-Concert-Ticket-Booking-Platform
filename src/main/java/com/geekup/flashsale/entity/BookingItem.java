package com.geekup.flashsale.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "booking_items")
@Data
@NoArgsConstructor
public class BookingItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    private Long categoryId;

    private Integer quantity;

    public BookingItem(Booking booking, Long categoryId, Integer quantity) {
        this.booking = booking;
        this.categoryId = categoryId;
        this.quantity = quantity;
    }
}