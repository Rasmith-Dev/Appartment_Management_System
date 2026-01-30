package com.apartment.management.repository;

import com.apartment.management.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface TenantRepository extends JpaRepository<Tenant, Long> {
    List<Tenant> findByFlat_Id(Long flatId);
    boolean existsByFlat_Id(Long flatId);
    boolean existsByUser_Id(Long userId);
    List<Tenant> findByLeaseEndAfter(LocalDate date);
    List<Tenant> findByLeaseEndBetween(LocalDate startDate, LocalDate endDate);
} 