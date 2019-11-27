package com.coronation.upload.repo;

import com.coronation.upload.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
	Payment findByTransactionId(String referenceCode);
	Payment findTopByOrderByTransactionIdDesc();
}
