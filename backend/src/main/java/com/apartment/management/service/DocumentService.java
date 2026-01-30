package com.apartment.management.service;

import com.apartment.management.model.Document;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DocumentService {
    List<Document> getAllDocuments();
    Optional<Document> getDocumentById(Long id);
    List<Document> getDocumentsByTenantId(Long tenantId);
    List<Document> getDocumentsByFlatId(Long flatId);
    List<Document> getDocumentsByType(Document.DocumentType type);
    List<Document> getDocumentsByUploader(Long userId);
    List<Document> getDocumentsByVerifier(Long userId);
    List<Document> getVerifiedDocuments();
    List<Document> getUnverifiedDocuments();
    List<Document> getExpiringDocuments(LocalDateTime beforeDate);
    Document uploadDocument(MultipartFile file, Document document);
    Document updateDocument(Long id, Document document);
    Document verifyDocument(Long id, Long verifiedBy);
    void deleteDocument(Long id);
    byte[] downloadDocument(Long id);
    List<Document> getDocumentsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
} 