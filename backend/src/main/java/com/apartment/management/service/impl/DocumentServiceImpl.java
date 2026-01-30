package com.apartment.management.service.impl;

import com.apartment.management.model.Document;
import com.apartment.management.repository.DocumentRepository;
import com.apartment.management.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    @Override
    public Optional<Document> getDocumentById(Long id) {
        return documentRepository.findById(id);
    }

    @Override
    public List<Document> getDocumentsByTenantId(Long tenantId) {
        return documentRepository.findByTenant_Id(tenantId);
    }

    @Override
    public List<Document> getDocumentsByFlatId(Long flatId) {
        return documentRepository.findByFlat_Id(flatId);
    }

    @Override
    public List<Document> getDocumentsByType(Document.DocumentType type) {
        return documentRepository.findByType(type);
    }

    @Override
    public List<Document> getDocumentsByUploader(Long userId) {
        return documentRepository.findByUploadedBy(userId);
    }

    @Override
    public List<Document> getDocumentsByVerifier(Long userId) {
        return documentRepository.findByVerifiedBy(userId);
    }

    @Override
    public List<Document> getVerifiedDocuments() {
        return documentRepository.findByVerified(true);
    }

    @Override
    public List<Document> getUnverifiedDocuments() {
        return documentRepository.findByVerified(false);
    }

    @Override
    public List<Document> getExpiringDocuments(LocalDateTime beforeDate) {
        return documentRepository.findByExpiryDateBefore(beforeDate);
    }

    @Override
    public Document uploadDocument(MultipartFile file, Document document) {
        try {
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isBlank()) {
                originalFilename = "uploaded-file";
            }
            String fileExtension = "";
            int lastDot = originalFilename.lastIndexOf(".");
            if (lastDot >= 0) {
                fileExtension = originalFilename.substring(lastDot);
            }
            String newFilename = UUID.randomUUID().toString() + fileExtension;

            // Save file
            Path filePath = uploadPath.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath);

            // Set document properties
            document.setFileName(originalFilename);
            document.setFileType(file.getContentType());
            document.setFilePath(filePath.toString());
            document.setFileSize(file.getSize());

            return documentRepository.save(document);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file: " + e.getMessage());
        }
    }

    @Override
    public Document updateDocument(Long id, Document document) {
        if (documentRepository.existsById(id)) {
            document.setId(id);
            return documentRepository.save(document);
        }
        throw new RuntimeException("Document not found with id: " + id);
    }

    @Override
    public Document verifyDocument(Long id, Long verifiedBy) {
        return documentRepository.findById(id)
                .map(document -> {
                    document.setVerified(true);
                    document.setVerifiedBy(verifiedBy);
                    document.setVerifiedAt(LocalDateTime.now());
                    return documentRepository.save(document);
                })
                .orElseThrow(() -> new RuntimeException("Document not found with id: " + id));
    }

    @Override
    public void deleteDocument(Long id) {
        documentRepository.findById(id).ifPresent(document -> {
            try {
                // Delete file from storage
                Path filePath = Paths.get(document.getFilePath());
                Files.deleteIfExists(filePath);
                
                // Delete document record
                documentRepository.deleteById(id);
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete file: " + e.getMessage());
            }
        });
    }

    @Override
    public byte[] downloadDocument(Long id) {
        return documentRepository.findById(id)
                .map(document -> {
                    try {
                        Path filePath = Paths.get(document.getFilePath());
                        return Files.readAllBytes(filePath);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to download file: " + e.getMessage());
                    }
                })
                .orElseThrow(() -> new RuntimeException("Document not found with id: " + id));
    }

    @Override
    public List<Document> getDocumentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return documentRepository.findByUploadedAtBetween(startDate, endDate);
    }
} 