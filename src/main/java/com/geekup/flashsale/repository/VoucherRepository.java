package com.geekup.flashsale.repository;

import com.geekup.flashsale.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Data access for voucher records.
 */
@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {
}
