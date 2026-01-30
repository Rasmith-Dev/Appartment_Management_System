package com.apartment.management.service.impl;

import com.apartment.management.model.Tenant;
import com.apartment.management.repository.TenantRepository;
import com.apartment.management.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TenantServiceImpl implements TenantService {

    @Autowired
    private TenantRepository tenantRepository;

    @Override
    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }

    @Override
    public Optional<Tenant> getTenantById(Long id) {
        return tenantRepository.findById(id);
    }

    @Override
    public List<Tenant> getTenantsByFlatId(Long flatId) {
        return tenantRepository.findByFlat_Id(flatId);
    }

    @Override
    public Tenant createTenant(Tenant tenant) {
        return tenantRepository.save(tenant);
    }

    @Override
    public Tenant updateTenant(Long id, Tenant tenant) {
        if (tenantRepository.existsById(id)) {
            tenant.setId(id);
            return tenantRepository.save(tenant);
        }
        throw new RuntimeException("Tenant not found with id: " + id);
    }

    @Override
    public void deleteTenant(Long id) {
        if (tenantRepository.existsById(id)) {
            tenantRepository.deleteById(id);
        } else {
            throw new RuntimeException("Tenant not found with id: " + id);
        }
    }

    @Override
    public List<Tenant> getActiveTenants() {
        LocalDate today = LocalDate.now();
        return tenantRepository.findByLeaseEndAfter(today);
    }

    @Override
    public List<Tenant> getTenantsWithExpiringLeases(int daysThreshold) {
        LocalDate today = LocalDate.now();
        LocalDate thresholdDate = today.plusDays(daysThreshold);
        return tenantRepository.findByLeaseEndBetween(today, thresholdDate);
    }
} 