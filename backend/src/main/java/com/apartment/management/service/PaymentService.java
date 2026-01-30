package com.apartment.management.service;

import com.apartment.management.model.Payment;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentService {
    List<Payment> getAllPayments();
    Optional<Payment> getPaymentById(Long id);
    List<Payment> getPaymentsByTenantId(Long tenantId);
    List<Payment> getPaymentsByFlatId(Long flatId);
    List<Payment> getPaymentsByStatus(Payment.Status status);
    List<Payment> getPaymentsByType(Payment.PaymentType type);
    List<Payment> getPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    List<Payment> getPaymentsByTenantAndDateRange(Long tenantId, LocalDateTime startDate, LocalDateTime endDate);
    List<Payment> getOverduePayments();
    Payment createPayment(Payment payment);
    Payment updatePayment(Long id, Payment payment);
    Payment updatePaymentStatus(Long id, Payment.Status status);
    void deletePayment(Long id);
    List<Payment> getPendingPayments();
    List<Payment> getCompletedPayments();
} 