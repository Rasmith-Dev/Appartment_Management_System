package com.apartment.management.service.impl;

import com.apartment.management.model.Complaint;
import com.apartment.management.repository.ComplaintRepository;
import com.apartment.management.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ComplaintServiceImpl implements ComplaintService {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Override
    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    @Override
    public Optional<Complaint> getComplaintById(Long id) {
        return complaintRepository.findById(id);
    }

    @Override
    public List<Complaint> getComplaintsByTenantId(Long tenantId) {
        return complaintRepository.findByTenant_Id(tenantId);
    }

    @Override
    public List<Complaint> getComplaintsByFlatId(Long flatId) {
        return complaintRepository.findByFlat_Id(flatId);
    }

    @Override
    public List<Complaint> getComplaintsByStatus(Complaint.Status status) {
        return complaintRepository.findByStatus(status);
    }

    @Override
    public List<Complaint> getComplaintsByPriority(Complaint.Priority priority) {
        return complaintRepository.findByPriority(priority);
    }

    @Override
    public List<Complaint> getComplaintsByAssignedTo(Long userId) {
        return complaintRepository.findByAssignedTo_Id(userId);
    }

    @Override
    public Complaint createComplaint(Complaint complaint) {
        return complaintRepository.save(complaint);
    }

    @Override
    public Complaint updateComplaint(Long id, Complaint complaint) {
        Complaint existing = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found with id: " + id));

        if (complaint.getTitle() != null) {
            existing.setTitle(complaint.getTitle());
        }
        if (complaint.getDescription() != null) {
            existing.setDescription(complaint.getDescription());
        }
        if (complaint.getStatus() != null) {
            existing.setStatus(complaint.getStatus());
        }
        if (complaint.getPriority() != null) {
            existing.setPriority(complaint.getPriority());
        }
        if (complaint.getTenant() != null) {
            existing.setTenant(complaint.getTenant());
        }
        if (complaint.getFlat() != null) {
            existing.setFlat(complaint.getFlat());
        }
        if (complaint.getAssignedTo() != null) {
            existing.setAssignedTo(complaint.getAssignedTo());
        }

        return complaintRepository.save(existing);
    }

    @Override
    public Complaint updateComplaintStatus(Long id, Complaint.Status status, String resolution) {
        return complaintRepository.findById(id)
                .map(complaint -> {
                    complaint.setStatus(status);
                    if (status == Complaint.Status.RESOLVED) {
                        complaint.setResolution(resolution);
                    }
                    return complaintRepository.save(complaint);
                })
                .orElseThrow(() -> new RuntimeException("Complaint not found with id: " + id));
    }

    @Override
    public void deleteComplaint(Long id) {
        if (complaintRepository.existsById(id)) {
            complaintRepository.deleteById(id);
        } else {
            throw new RuntimeException("Complaint not found with id: " + id);
        }
    }

    @Override
    public List<Complaint> getOpenComplaints() {
        return complaintRepository.findByStatus(Complaint.Status.OPEN);
    }

    @Override
    public List<Complaint> getUrgentComplaints() {
        return complaintRepository.findByPriority(Complaint.Priority.URGENT);
    }
} 