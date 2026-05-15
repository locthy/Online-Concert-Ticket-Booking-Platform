package com.geekup.flashsale.repository;

import com.geekup.flashsale.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {
}
