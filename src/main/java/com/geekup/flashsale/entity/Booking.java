package com.geekup.flashsale.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Booking aggregate root representing a queued or completed order.
 */
@Entity
@Table(name = "booking")
@Data
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String queueTicketId;

    @Column(unique = true, nullable = false)
    private String idempotencyKey;

    @Column(nullable = false)
    private Double totalAmount;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    private List<BookingItem> items = new ArrayList<>();

    @OneToOne(mappedBy = "bookingId", cascade = CascadeType.ALL)
    private PaymentTransaction paymentTransaction = null;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id")
    private Voucher voucher = null;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_user_id")
    private AppUser user;

    public Booking(String queueTicketId, String idempotencyKey, Double totalAmount, String status) {
        this.queueTicketId = queueTicketId;
        this.idempotencyKey = idempotencyKey;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    /**
     * Initializes creation timestamp when inserting a new record.
     */
    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    /**
     * Adds an item and updates the inverse relation.
     *
     * @param item booking item
     */
    public void addItem(BookingItem item) {
        items.add(item);
        item.setBooking(this);
    }

    /**
     * Removes an item and updates the inverse relation.
     *
     * @param item booking item
     */
    public void removeItem(BookingItem item) {
        this.items.remove(item);
        item.setBooking(null);
    }
}
