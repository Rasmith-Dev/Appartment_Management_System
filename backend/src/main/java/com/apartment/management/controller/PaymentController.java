package com.apartment.management.controller;

import com.apartment.management.dto.PaymentRequest;
import com.apartment.management.model.Flat;
import com.apartment.management.model.Payment;
import com.apartment.management.model.Tenant;
import com.apartment.management.repository.FlatRepository;
import com.apartment.management.repository.TenantRepository;
import com.apartment.management.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private FlatRepository flatRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<Payment> getAllPayments() {
        return paymentService.getAllPayments();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TENANT')")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        return paymentService.getPaymentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tenant/{tenantId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @securityService.isCurrentUserTenant(#tenantId)")
    public List<Payment> getPaymentsByTenantId(@PathVariable Long tenantId) {
        return paymentService.getPaymentsByTenantId(tenantId);
    }

    @GetMapping("/flat/{flatId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<Payment> getPaymentsByFlatId(@PathVariable Long flatId) {
        return paymentService.getPaymentsByFlatId(flatId);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<Payment> getPaymentsByStatus(@PathVariable Payment.Status status) {
        return paymentService.getPaymentsByStatus(status);
    }

    @GetMapping("/type/{type}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<Payment> getPaymentsByType(@PathVariable Payment.PaymentType type) {
        return paymentService.getPaymentsByType(type);
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<Payment> getPaymentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return paymentService.getPaymentsByDateRange(startDate, endDate);
    }

    @GetMapping("/tenant/{tenantId}/date-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @securityService.isCurrentUserTenant(#tenantId)")
    public List<Payment> getPaymentsByTenantAndDateRange(
            @PathVariable Long tenantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return paymentService.getPaymentsByTenantAndDateRange(tenantId, startDate, endDate);
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<Payment> getOverduePayments() {
        return paymentService.getOverduePayments();
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<Payment> getPendingPayments() {
        return paymentService.getPendingPayments();
    }

    @GetMapping("/completed")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<Payment> getCompletedPayments() {
        return paymentService.getCompletedPayments();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Payment> createPayment(@RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.createPayment(toPayment(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Payment> updatePayment(@PathVariable Long id, @RequestBody PaymentRequest request) {
        try {
            Payment payment = toPayment(request);
            return ResponseEntity.ok(paymentService.updatePayment(id, payment));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Payment> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam Payment.Status status) {
        try {
            return ResponseEntity.ok(paymentService.updatePaymentStatus(id, status));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/mark-paid")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Payment> markPaymentAsPaid(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(paymentService.updatePaymentStatus(id, Payment.Status.COMPLETED));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        try {
            paymentService.deletePayment(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private Payment toPayment(PaymentRequest request) {
        Tenant tenant = tenantRepository.findById(request.getTenantId())
                .orElseThrow(() -> new RuntimeException("Tenant not found with id: " + request.getTenantId()));
        Flat flat = null;
        if (request.getFlatId() != null) {
            flat = flatRepository.findById(request.getFlatId())
                    .orElseThrow(() -> new RuntimeException("Flat not found with id: " + request.getFlatId()));
        } else if (tenant.getFlat() != null) {
            flat = tenant.getFlat();
        }

        Payment payment = new Payment();
        payment.setTenant(tenant);
        if (flat != null) {
            payment.setFlat(flat);
        }
        if (request.getAmount() != null) {
            payment.setAmount(java.math.BigDecimal.valueOf(request.getAmount()));
        }
        if (request.getType() != null) {
            payment.setType(Payment.PaymentType.valueOf(request.getType()));
        }
        if (request.getStatus() != null) {
            payment.setStatus(Payment.Status.valueOf(request.getStatus()));
        }
        if (request.getDueDate() != null) {
            payment.setDueDate(parseDateTime(request.getDueDate()));
        }
        payment.setDescription(request.getDescription());
        return payment;
    }

    private LocalDateTime parseDateTime(String input) {
        try {
            return LocalDateTime.parse(input, DateTimeFormatter.ISO_DATE_TIME);
        } catch (DateTimeParseException e) {
            LocalDate date = LocalDate.parse(input, DateTimeFormatter.ISO_DATE);
            return date.atStartOfDay();
        }
    }
} 