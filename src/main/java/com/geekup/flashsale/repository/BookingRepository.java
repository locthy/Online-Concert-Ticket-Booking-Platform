package com.geekup.flashsale.repository;

import com.geekup.flashsale.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data access for booking aggregates.
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Finds bookings in a given status created before a threshold.
     *
     * @param status booking status
     * @param time creation time threshold
     * @return matching bookings
     */
    List<Booking> findAllByStatusAndCreatedAtBefore(String status, LocalDateTime time);
}
