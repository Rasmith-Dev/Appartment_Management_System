package com.apartment.management.controller;

import com.apartment.management.model.Document;
import com.apartment.management.model.Flat;
import com.apartment.management.model.Tenant;
import com.apartment.management.repository.FlatRepository;
import com.apartment.management.repository.TenantRepository;
import com.apartment.management.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
@Tag(name = "Document Management", description = "APIs for managing documents")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private FlatRepository flatRepository;

    @Operation(summary = "Get all documents", description = "Retrieves a list of all documents")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all documents"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<Document> getAllDocuments() {
        return documentService.getAllDocuments();
    }

    @Operation(summary = "Get document by ID", description = "Retrieves a specific document by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the document"),
        @ApiResponse(responseCode = "404", description = "Document not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TENANT')")
    public ResponseEntity<Document> getDocumentById(
            @Parameter(description = "Document ID") @PathVariable Long id) {
        return documentService.getDocumentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get documents by tenant ID", description = "Retrieves all documents associated with a specific tenant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved tenant documents"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/tenant/{tenantId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @securityService.isCurrentUserTenant(#tenantId)")
    public List<Document> getDocumentsByTenantId(
            @Parameter(description = "Tenant ID") @PathVariable Long tenantId) {
        return documentService.getDocumentsByTenantId(tenantId);
    }

    @Operation(summary = "Get documents by flat ID", description = "Retrieves all documents associated with a specific flat")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved flat documents"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/flat/{flatId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<Document> getDocumentsByFlatId(
            @Parameter(description = "Flat ID") @PathVariable Long flatId) {
        return documentService.getDocumentsByFlatId(flatId);
    }

    @Operation(summary = "Get documents by type", description = "Retrieves all documents of a specific type")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved documents by type"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/type/{type}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<Document> getDocumentsByType(
            @Parameter(description = "Document type") @PathVariable Document.DocumentType type) {
        return documentService.getDocumentsByType(type);
    }

    @Operation(summary = "Upload document", description = "Uploads a new document with file")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully uploaded document"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TENANT')")
    public ResponseEntity<Document> uploadDocument(
            @Parameter(description = "Document file") @RequestParam("file") MultipartFile file,
            @Parameter(description = "Document title") @RequestParam("title") String title,
            @Parameter(description = "Document description") @RequestParam("description") String description,
            @Parameter(description = "Document type") @RequestParam("type") Document.DocumentType type,
            @Parameter(description = "Tenant ID") @RequestParam("tenantId") Long tenantId,
            @Parameter(description = "Flat ID") @RequestParam(value = "flatId", required = false) Long flatId) {
        Document document = buildDocument(title, description, type, tenantId, flatId);
        return ResponseEntity.ok(documentService.uploadDocument(file, document));
    }

    @Operation(summary = "Update document", description = "Updates an existing document's details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated document"),
        @ApiResponse(responseCode = "404", description = "Document not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Document> updateDocument(
            @Parameter(description = "Document ID") @PathVariable Long id,
            @Parameter(description = "Document file") @RequestParam(value = "file", required = false) MultipartFile file,
            @Parameter(description = "Document title") @RequestParam("title") String title,
            @Parameter(description = "Document description") @RequestParam("description") String description,
            @Parameter(description = "Document type") @RequestParam("type") Document.DocumentType type,
            @Parameter(description = "Tenant ID") @RequestParam("tenantId") Long tenantId,
            @Parameter(description = "Flat ID") @RequestParam(value = "flatId", required = false) Long flatId) {
        try {
            Document existing = documentService.getDocumentById(id)
                    .orElseThrow(() -> new RuntimeException("Document not found with id: " + id));
            applyDocumentUpdate(existing, title, description, type, tenantId, flatId);

            if (file != null && !file.isEmpty()) {
                return ResponseEntity.ok(documentService.uploadDocument(file, existing));
            }

            return ResponseEntity.ok(documentService.updateDocument(id, existing));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Verify document", description = "Marks a document as verified")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully verified document"),
        @ApiResponse(responseCode = "404", description = "Document not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PutMapping("/{id}/verify")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Document> verifyDocument(
            @Parameter(description = "Document ID") @PathVariable Long id,
            @Parameter(description = "User ID of verifier") @RequestParam Long verifiedBy) {
        try {
            return ResponseEntity.ok(documentService.verifyDocument(id, verifiedBy));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Download document", description = "Downloads a document file")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully downloaded document"),
        @ApiResponse(responseCode = "404", description = "Document not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/{id}/download")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TENANT')")
    public ResponseEntity<byte[]> downloadDocument(
            @Parameter(description = "Document ID") @PathVariable Long id) {
        Document document = documentService.getDocumentById(id)
                .orElseThrow(() -> new RuntimeException("Document not found with id: " + id));

        byte[] fileContent = documentService.downloadDocument(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(document.getFileType()))
                .body(fileContent);
    }

    @Operation(summary = "Delete document", description = "Deletes a document and its associated file")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully deleted document"),
        @ApiResponse(responseCode = "404", description = "Document not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDocument(
            @Parameter(description = "Document ID") @PathVariable Long id) {
        try {
            documentService.deleteDocument(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private Document buildDocument(String title, String description, Document.DocumentType type, Long tenantId, Long flatId) {
        Document document = new Document();
        applyDocumentUpdate(document, title, description, type, tenantId, flatId);
        return document;
    }

    private void applyDocumentUpdate(Document document, String title, String description, Document.DocumentType type, Long tenantId, Long flatId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found with id: " + tenantId));

        Flat flat = null;
        if (flatId != null) {
            flat = flatRepository.findById(flatId)
                    .orElseThrow(() -> new RuntimeException("Flat not found with id: " + flatId));
        } else if (tenant.getFlat() != null) {
            flat = tenant.getFlat();
        }

        document.setTenant(tenant);
        document.setFlat(flat);
        document.setTitle(title);
        document.setDescription(description);
        document.setType(type);
        if (document.getUploadedBy() == null) {
            Long uploadedBy = tenant.getUser() != null ? tenant.getUser().getId() : null;
            document.setUploadedBy(uploadedBy != null ? uploadedBy : 0L);
        }
    }
} 