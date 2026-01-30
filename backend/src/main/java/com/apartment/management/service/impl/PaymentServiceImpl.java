package com.apartment.management.service.impl;

import com.apartment.management.model.Payment;
import com.apartment.management.repository.PaymentRepository;
import com.apartment.management.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Override
    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }

    @Override
    public List<Payment> getPaymentsByTenantId(Long tenantId) {
        return paymentRepository.findByTenant_Id(tenantId);
    }

    @Override
    public List<Payment> getPaymentsByFlatId(Long flatId) {
        return paymentRepository.findByFlat_Id(flatId);
    }

    @Override
    public List<Payment> getPaymentsByStatus(Payment.Status status) {
        return paymentRepository.findByStatus(status);
    }

    @Override
    public List<Payment> getPaymentsByType(Payment.PaymentType type) {
        return paymentRepository.findByType(type);
    }

    @Override
    public List<Payment> getPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return paymentRepository.findByPaymentDateBetween(startDate, endDate);
    }

    @Override
    public List<Payment> getPaymentsByTenantAndDateRange(Long tenantId, LocalDateTime startDate, LocalDateTime endDate) {
        return paymentRepository.findByTenant_IdAndPaymentDateBetween(tenantId, startDate, endDate);
    }

    @Override
    public List<Payment> getOverduePayments() {
        return paymentRepository.findByDueDateBeforeAndStatus(LocalDateTime.now(), Payment.Status.PENDING);
    }

    @Override
    public Payment createPayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    @Override
    public Payment updatePayment(Long id, Payment payment) {
        if (paymentRepository.existsById(id)) {
            payment.setId(id);
            return paymentRepository.save(payment);
        }
        throw new RuntimeException("Payment not found with id: " + id);
    }

    @Override
    public Payment updatePaymentStatus(Long id, Payment.Status status) {
        return paymentRepository.findById(id)
                .map(payment -> {
                    payment.setStatus(status);
                    if (status == Payment.Status.COMPLETED) {
                        payment.setPaymentDate(LocalDateTime.now());
                    }
                    return paymentRepository.save(payment);
                })
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));
    }

    @Override
    public void deletePayment(Long id) {
        if (paymentRepository.existsById(id)) {
            paymentRepository.deleteById(id);
        } else {
            throw new RuntimeException("Payment not found with id: " + id);
        }
    }

    @Override
    public List<Payment> getPendingPayments() {
        return paymentRepository.findByStatus(Payment.Status.PENDING);
    }

    @Override
    public List<Payment> getCompletedPayments() {
        return paymentRepository.findByStatus(Payment.Status.COMPLETED);
    }
} 