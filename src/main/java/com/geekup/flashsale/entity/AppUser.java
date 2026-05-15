package com.geekup.flashsale.entity;

import jakarta.persistence.*;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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

    private String passwordHash;

    @Column(nullable = false)
    private String role;

    @OneToMany(mappedBy = "user")
    private List<Booking> bookings = new ArrayList<>();

    public void addBooking(Booking booking) {
        bookings.add(booking);
        booking.setUser(this);
    }

    public void removebooking(Booking booking) {
        this.bookings.remove(booking);
        booking.setUser(null);
    }
}
