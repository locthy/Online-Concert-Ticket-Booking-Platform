package com.geekup.flashsale.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private String status; // PENDING, PAID, CANCELLED

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // Added CascadeType.ALL so saving Booking saves items automatically
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

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    // Helper method to link items safely
    public void addItem(BookingItem item) {
        items.add(item);
        item.setBooking(this);
    }

    public void removeItem(BookingItem item) {
        this.items.remove(item);
        item.setBooking(null);
    }
}