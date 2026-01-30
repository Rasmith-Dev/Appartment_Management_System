package com.apartment.management.controller;

import com.apartment.management.dto.TenantRequest;
import com.apartment.management.model.Flat;
import com.apartment.management.model.Tenant;
import com.apartment.management.model.User;
import com.apartment.management.repository.FlatRepository;
import com.apartment.management.repository.TenantRepository;
import com.apartment.management.repository.UserRepository;
import com.apartment.management.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api/tenants")
@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
public class TenantController {

    @Autowired
    private TenantService tenantService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FlatRepository flatRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<Tenant> getAllTenants() {
        return tenantService.getAllTenants();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Tenant> getTenantById(@PathVariable Long id) {
        return tenantService.getTenantById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/flat/{flatId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<Tenant> getTenantsByFlatId(@PathVariable Long flatId) {
        return tenantService.getTenantsByFlatId(flatId);
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<Tenant> getActiveTenants() {
        return tenantService.getActiveTenants();
    }

    @GetMapping("/expiring")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<Tenant> getTenantsWithExpiringLeases(@RequestParam(defaultValue = "30") int daysThreshold) {
        return tenantService.getTenantsWithExpiringLeases(daysThreshold);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createTenant(@RequestBody TenantRequest request) {
        if (tenantRepository.existsByUser_Id(request.getUserId())) {
            return ResponseEntity.badRequest().body("User already assigned to a tenant");
        }
        return ResponseEntity.ok(tenantService.createTenant(toTenant(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Tenant> updateTenant(@PathVariable Long id, @RequestBody TenantRequest request) {
        try {
            Tenant tenant = toTenant(request);
            return ResponseEntity.ok(tenantService.updateTenant(id, tenant));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTenant(@PathVariable Long id) {
        try {
            tenantService.deleteTenant(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private Tenant toTenant(TenantRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));
        Flat flat = flatRepository.findById(request.getFlatId())
                .orElseThrow(() -> new RuntimeException("Flat not found with id: " + request.getFlatId()));

        Tenant tenant = new Tenant();
        tenant.setUser(user);
        tenant.setFlat(flat);
        if (request.getLeaseStart() != null) {
            tenant.setLeaseStart(parseDate(request.getLeaseStart()));
        }
        if (request.getLeaseEnd() != null) {
            tenant.setLeaseEnd(parseDate(request.getLeaseEnd()));
        }
        tenant.setPhone(request.getPhone());
        return tenant;
    }

    private LocalDate parseDate(String input) {
        try {
            return LocalDate.parse(input, DateTimeFormatter.ISO_DATE);
        } catch (DateTimeParseException e) {
            return LocalDate.parse(input);
        }
    }
} 