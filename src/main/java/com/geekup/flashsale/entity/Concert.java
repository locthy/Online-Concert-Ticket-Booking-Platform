package com.geekup.flashsale.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name="concert")
@NoArgsConstructor
public class Concert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDateTime eventDate;

    @Column(nullable = false)
    private String status;

    @OneToMany(mappedBy = "concert", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TicketCategory> ticketCategories = new ArrayList<>();

    public void addTicketCategory(TicketCategory ticketCategory) {
        ticketCategories.add(ticketCategory);
        ticketCategory.setConcert(this);
    }

    public void ticketCategory(TicketCategory ticketCategory) {
        this.ticketCategories.remove(ticketCategory);
        ticketCategory.setConcert(null);
    }

}
