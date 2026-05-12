package com.geekup.flashsale.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long concertId;

    private String queueTicketId;

    @Column(unique = true, nullable = false)
    private String idempotencyKey;

    private Double totalAmount;

    private String status; // PENDING, PAID, CANCELLED

    private LocalDateTime createdAt;

    // Added CascadeType.ALL so saving Booking saves items automatically
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BookingItem> items = new ArrayList<>();

    public Booking(Long concertId, String queueTicketId, String idempotencyKey, Double totalAmount, String status) {
        this.concertId = concertId;
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
}