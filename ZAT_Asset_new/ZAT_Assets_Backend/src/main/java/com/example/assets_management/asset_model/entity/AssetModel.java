package com.example.assets_management.asset_model.entity;

import com.example.assets_management.asset_category.entity.AssetCategory;
import com.example.assets_management.asset_category.enums.DepreciationMethod;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a reusable asset model / template that can be referenced when
 * creating multiple assets of the same make &amp; specification.
 *
 * <p>An asset model belongs to a category and carries optional specification
 * data (stored as JSON), a default warranty period and a depreciation method.</p>
 *
 * @author ZAT Asset Management
 */
@Entity
@Table(name = "asset_models")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AssetModel {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private UUID id;

    /** The category this model belongs to. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private AssetCategory category;

    /** Human friendly model name, e.g. "Dell Latitude 5420". */
    @Column(name = "name", nullable = false, length = 150)
    private String name;

    /** Free-form specification sheet stored as JSON text. */
    @Column(name = "spec_sheet", columnDefinition = "TEXT")
    private String specSheet;

    /** Default warranty length in months for assets created from this model. */
    @Column(name = "default_warranty_months")
    private Integer defaultWarrantyMonths;

    /** Default depreciation method applied to assets from this model. */
    @Enumerated(EnumType.STRING)
    @Column(name = "depreciation_method", length = 40)
    private DepreciationMethod depreciationMethod;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
