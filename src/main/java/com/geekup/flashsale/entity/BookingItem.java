package com.geekup.flashsale.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Booking line item that binds a booking with a ticket category and quantity.
 */
@Entity
@Table(name = "booking_item")
@Data
@NoArgsConstructor
public class BookingItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private TicketCategory ticketCategory;

    @Column(nullable = false)
    private Integer quantity;

    public BookingItem(Booking booking, TicketCategory ticketCategory, Integer quantity) {
        this.booking = booking;
        this.ticketCategory = ticketCategory;
        this.quantity = quantity;
    }
}
