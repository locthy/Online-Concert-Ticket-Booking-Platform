package com.geekup.flashsale.repository;

import com.geekup.flashsale.entity.TicketCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Data access for ticket categories.
 */
@Repository
public interface TicketCategoryRepository extends JpaRepository<TicketCategory, Long> {
}
