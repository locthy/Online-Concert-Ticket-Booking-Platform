package com.geekup.flashsale.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Ticket category under a concert (for example VIP or Standard).
 */
@Entity
@Table(name = "ticket_category")
@Data
@NoArgsConstructor
public class TicketCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

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

    /**
     * Adds a voucher and binds relation.
     *
     * @param voucher voucher entity
     */
    public void addVoucher(Voucher voucher) {
        vouchers.add(voucher);
        voucher.setTicketCategory(this);
    }

    /**
     * Removes a voucher and clears relation.
     *
     * @param voucher voucher entity
     */
    public void removeVoucher(Voucher voucher) {
        this.vouchers.remove(voucher);
        voucher.setTicketCategory(null);
    }
}
