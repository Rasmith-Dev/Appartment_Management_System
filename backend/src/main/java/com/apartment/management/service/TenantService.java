package com.apartment.management.service;

import com.apartment.management.model.Tenant;
import java.util.List;
import java.util.Optional;

public interface TenantService {
    List<Tenant> getAllTenants();
    Optional<Tenant> getTenantById(Long id);
    List<Tenant> getTenantsByFlatId(Long flatId);
    Tenant createTenant(Tenant tenant);
    Tenant updateTenant(Long id, Tenant tenant);
    void deleteTenant(Long id);
    List<Tenant> getActiveTenants();
    List<Tenant> getTenantsWithExpiringLeases(int daysThreshold);
} 