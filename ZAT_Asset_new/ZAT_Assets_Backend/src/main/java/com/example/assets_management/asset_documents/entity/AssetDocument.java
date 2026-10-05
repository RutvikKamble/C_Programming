package com.example.assets_management.asset_documents.entity;

import com.example.assets_management.asset_documents.enums.AssetDocumentType;
import com.example.assets_management.asset_master.entity.Asset;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "asset_document")
@Getter
@Setter
@NoArgsConstructor
public class AssetDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nullable on purpose: a document can be uploaded before it's linked to
    // a specific asset, or attached generically (e.g. a policy document).
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "asset_id")
    private Asset asset;

    // INVOICE / WARRANTY-CARD / INSURANCE / MANUAL / AMC-CONTRACT / OTHER
    @NotBlank(message = "documentType is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssetDocumentType documentType;

    @NotBlank(message = "documentName is required")
    private String documentName;

    @NotBlank(message = "filePath is required")
    private String filePath;

    private String uploadedBy;

    private LocalDateTime uploadedDate;

    // Optional: useful for insurance/AMC documents that expire
    private LocalDate expiryDate;
}
