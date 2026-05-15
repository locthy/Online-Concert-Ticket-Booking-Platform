package com.geekup.flashsale.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ticket_category")
@Data
@NoArgsConstructor
public class TicketCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // e.g., "VIP", "Standard"

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer totalQuantity;

    @Column(nullable = false)
    private Integer availableQuantity = 0;

    @OneToMany(mappedBy = "ticketCategory")
    private List<Voucher> vouchers = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id", nullable = false)
    private Concert concert;

    public void addVoucher(Voucher voucher) {
        vouchers.add(voucher);
        voucher.setTicketCategory(this);
    }

    public void removeVoucher(Voucher voucher) {
        this.vouchers.remove(voucher);
        voucher.setTicketCategory(null);
    }
}