package com.apartment.management.service;

import com.apartment.management.model.Complaint;
import java.util.List;
import java.util.Optional;

public interface ComplaintService {
    List<Complaint> getAllComplaints();
    Optional<Complaint> getComplaintById(Long id);
    List<Complaint> getComplaintsByTenantId(Long tenantId);
    List<Complaint> getComplaintsByFlatId(Long flatId);
    List<Complaint> getComplaintsByStatus(Complaint.Status status);
    List<Complaint> getComplaintsByPriority(Complaint.Priority priority);
    List<Complaint> getComplaintsByAssignedTo(Long userId);
    Complaint createComplaint(Complaint complaint);
    Complaint updateComplaint(Long id, Complaint complaint);
    Complaint updateComplaintStatus(Long id, Complaint.Status status, String resolution);
    void deleteComplaint(Long id);
    List<Complaint> getOpenComplaints();
    List<Complaint> getUrgentComplaints();
} 