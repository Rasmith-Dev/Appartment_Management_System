package com.apartment.management.service;

import com.apartment.management.model.Flat;
import com.apartment.management.repository.ComplaintRepository;
import com.apartment.management.repository.DocumentRepository;
import com.apartment.management.repository.FlatRepository;
import com.apartment.management.repository.PaymentRepository;
import com.apartment.management.repository.TenantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FlatService {
    private final FlatRepository flatRepository;
    private final TenantRepository tenantRepository;
    private final PaymentRepository paymentRepository;
    private final ComplaintRepository complaintRepository;
    private final DocumentRepository documentRepository;

    public FlatService(
            FlatRepository flatRepository,
            TenantRepository tenantRepository,
            PaymentRepository paymentRepository,
            ComplaintRepository complaintRepository,
            DocumentRepository documentRepository
    ) {
        this.flatRepository = flatRepository;
        this.tenantRepository = tenantRepository;
        this.paymentRepository = paymentRepository;
        this.complaintRepository = complaintRepository;
        this.documentRepository = documentRepository;
    }

    public List<Flat> getAllFlats() {
        return flatRepository.findAll();
    }

    public Flat getFlatById(Long id) {
        return flatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flat not found with id: " + id));
    }

    public Flat createFlat(Flat flat) {
        return flatRepository.save(flat);
    }

    public Flat updateFlat(Long id, Flat flat) {
        Flat existingFlat = getFlatById(id);
        existingFlat.setRent(flat.getRent());
        existingFlat.setStatus(flat.getStatus());
        return flatRepository.save(existingFlat);
    }

    public void deleteFlat(Long id) {
        if (!flatRepository.existsById(id)) {
            throw new RuntimeException("Flat not found with id: " + id);
        }
        if (tenantRepository.existsByFlat_Id(id)
                || paymentRepository.existsByFlat_Id(id)
                || complaintRepository.existsByFlat_Id(id)
                || documentRepository.existsByFlat_Id(id)) {
            throw new IllegalStateException("Flat is referenced by other records");
        }
        flatRepository.deleteById(id);
    }
} 