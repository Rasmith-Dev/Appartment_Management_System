package com.apartment.management.controller;

import com.apartment.management.dto.ComplaintRequest;
import com.apartment.management.model.Complaint;
import com.apartment.management.model.Flat;
import com.apartment.management.model.Tenant;
import com.apartment.management.repository.FlatRepository;
import com.apartment.management.repository.TenantRepository;
import com.apartment.management.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private FlatRepository flatRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<Complaint> getAllComplaints() {
        return complaintService.getAllComplaints();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TENANT')")
    public ResponseEntity<Complaint> getComplaintById(@PathVariable Long id) {
        return complaintService.getComplaintById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tenant/{tenantId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @securityService.isCurrentUserTenant(#tenantId)")
    public List<Complaint> getComplaintsByTenantId(@PathVariable Long tenantId) {
        return complaintService.getComplaintsByTenantId(tenantId);
    }

    @GetMapping("/flat/{flatId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<Complaint> getComplaintsByFlatId(@PathVariable Long flatId) {
        return complaintService.getComplaintsByFlatId(flatId);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<Complaint> getComplaintsByStatus(@PathVariable Complaint.Status status) {
        return complaintService.getComplaintsByStatus(status);
    }

    @GetMapping("/priority/{priority}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<Complaint> getComplaintsByPriority(@PathVariable Complaint.Priority priority) {
        return complaintService.getComplaintsByPriority(priority);
    }

    @GetMapping("/assigned/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<Complaint> getComplaintsByAssignedTo(@PathVariable Long userId) {
        return complaintService.getComplaintsByAssignedTo(userId);
    }

    @GetMapping("/open")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<Complaint> getOpenComplaints() {
        return complaintService.getOpenComplaints();
    }

    @GetMapping("/urgent")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<Complaint> getUrgentComplaints() {
        return complaintService.getUrgentComplaints();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TENANT')")
    public ResponseEntity<Complaint> createComplaint(@RequestBody ComplaintRequest request) {
        return ResponseEntity.ok(complaintService.createComplaint(toComplaint(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Complaint> updateComplaint(@PathVariable Long id, @RequestBody ComplaintRequest request) {
        try {
            Complaint complaint = toComplaint(request);
            return ResponseEntity.ok(complaintService.updateComplaint(id, complaint));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Complaint> updateComplaintStatus(
            @PathVariable Long id,
            @RequestParam Complaint.Status status,
            @RequestParam(required = false) String resolution) {
        try {
            return ResponseEntity.ok(complaintService.updateComplaintStatus(id, status, resolution));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteComplaint(@PathVariable Long id) {
        try {
            complaintService.deleteComplaint(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private Complaint toComplaint(ComplaintRequest request) {
        Tenant tenant = tenantRepository.findById(request.getTenantId())
                .orElseThrow(() -> new RuntimeException("Tenant not found with id: " + request.getTenantId()));

        Flat flat = null;
        if (request.getFlatId() != null) {
            flat = flatRepository.findById(request.getFlatId())
                    .orElseThrow(() -> new RuntimeException("Flat not found with id: " + request.getFlatId()));
        } else if (tenant.getFlat() != null) {
            flat = tenant.getFlat();
        }

        Complaint complaint = new Complaint();
        complaint.setTenant(tenant);
        if (flat != null) {
            complaint.setFlat(flat);
        }
        complaint.setTitle(request.getTitle());
        complaint.setDescription(request.getDescription());
        if (request.getStatus() != null) {
            complaint.setStatus(Complaint.Status.valueOf(request.getStatus()));
        }
        if (request.getPriority() != null) {
            complaint.setPriority(Complaint.Priority.valueOf(request.getPriority()));
        }
        return complaint;
    }
} 