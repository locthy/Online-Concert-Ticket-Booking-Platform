package com.geekup.flashsale.repository;

import com.geekup.flashsale.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    // Spring Data JPA will automatically implement CRUD methods here
    List<Booking> findAllByStatusAndCreatedAtBefore(
            String status,
            LocalDateTime time
    );
}