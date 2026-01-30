package com.apartment.management.repository;

import com.apartment.management.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByTenant_Id(Long tenantId);
    List<Payment> findByFlat_Id(Long flatId);
    boolean existsByFlat_Id(Long flatId);
    List<Payment> findByStatus(Payment.Status status);
    List<Payment> findByType(Payment.PaymentType type);
    List<Payment> findByTenant_IdAndStatus(Long tenantId, Payment.Status status);
    List<Payment> findByFlat_IdAndStatus(Long flatId, Payment.Status status);
    List<Payment> findByDueDateBeforeAndStatus(LocalDateTime date, Payment.Status status);
    List<Payment> findByPaymentDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Payment> findByTenant_IdAndPaymentDateBetween(Long tenantId, LocalDateTime startDate, LocalDateTime endDate);
} 