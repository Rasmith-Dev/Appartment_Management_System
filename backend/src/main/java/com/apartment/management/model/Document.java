package com.apartment.management.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "documents")
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileType;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentType type;

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    @ManyToOne
    @JoinColumn(name = "flat_id")
    private Flat flat;

    @Column(length = 1000)
    private String description;

    @Column(name = "uploaded_by", nullable = false)
    private Long uploadedBy;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "is_verified", nullable = false)
    private boolean verified;

    @Column(name = "verified_by")
    private Long verifiedBy;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @JsonProperty("tenantId")
    public Long getTenantId() {
        return tenant != null ? tenant.getId() : null;
    }

    @JsonProperty("flatId")
    public Long getFlatId() {
        return flat != null ? flat.getId() : null;
    }

    @JsonProperty("fileUrl")
    public String getFileUrl() {
        return id == null ? null : "/api/documents/" + id + "/download";
    }

    public enum DocumentType {
        LEASE,
        INVOICE,
        LEASE_AGREEMENT,
        ID_PROOF,
        ADDRESS_PROOF,
        INCOME_PROOF,
        MAINTENANCE_REPORT,
        COMPLAINT_REPORT,
        PAYMENT_RECEIPT,
        OTHER
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        uploadedAt = LocalDateTime.now();
        verified = false;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 