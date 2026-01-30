package com.apartment.management.repository;

import com.apartment.management.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByTenant_Id(Long tenantId);
    List<Document> findByFlat_Id(Long flatId);
    boolean existsByFlat_Id(Long flatId);
    List<Document> findByType(Document.DocumentType type);
    List<Document> findByUploadedBy(Long userId);
    List<Document> findByVerifiedBy(Long userId);
    List<Document> findByVerified(boolean verified);
    List<Document> findByExpiryDateBefore(LocalDateTime date);
    List<Document> findByTenant_IdAndType(Long tenantId, Document.DocumentType type);
    List<Document> findByFlat_IdAndType(Long flatId, Document.DocumentType type);
    List<Document> findByUploadedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
} 