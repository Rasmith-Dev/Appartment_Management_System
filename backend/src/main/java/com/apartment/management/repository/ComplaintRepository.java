package com.apartment.management.repository;

import com.apartment.management.model.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByTenant_Id(Long tenantId);
    List<Complaint> findByFlat_Id(Long flatId);
    boolean existsByFlat_Id(Long flatId);
    List<Complaint> findByStatus(Complaint.Status status);
    List<Complaint> findByPriority(Complaint.Priority priority);
    List<Complaint> findByAssignedTo_Id(Long userId);
    List<Complaint> findByTenant_IdAndStatus(Long tenantId, Complaint.Status status);
    List<Complaint> findByFlat_IdAndStatus(Long flatId, Complaint.Status status);
} 