package com.geekup.flashsale.repository;

import com.geekup.flashsale.entity.Concert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Data access for concerts.
 */
@Repository
public interface ConcertRepository extends JpaRepository<Concert, Long> {
}
