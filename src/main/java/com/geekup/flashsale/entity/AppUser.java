package com.geekup.flashsale.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * User entity used for authentication and booking ownership.
 */
@Entity
@Table(name = "app_user")
@Data
@NoArgsConstructor
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String userName;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String role;

    @OneToMany(mappedBy = "user")
    private List<Booking> bookings = new ArrayList<>();

    /**
     * Links booking to this user and maintains both sides of relation.
     *
     * @param booking booking entity
     */
    public void addBooking(Booking booking) {
        bookings.add(booking);
        booking.setUser(this);
    }

    /**
     * Unlinks booking from this user and maintains both sides of relation.
     *
     * @param booking booking entity
     */
    public void removeBooking(Booking booking) {
        this.bookings.remove(booking);
        booking.setUser(null);
    }
}
