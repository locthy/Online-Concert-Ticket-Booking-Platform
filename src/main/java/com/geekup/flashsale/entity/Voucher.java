package com.geekup.flashsale.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Voucher entity that can discount a specific ticket category.
 */
@Entity
@Data
@Table(name = "voucher")
@NoArgsConstructor
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private Double discountAmount;

    @Column(nullable = false)
    private Integer maxUsage;

    @Column(nullable = false)
    private Integer currentUsage;

    @Column(nullable = false)
    private LocalDateTime expiredDay;

    @Column(nullable = false)
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private TicketCategory ticketCategory;
}
