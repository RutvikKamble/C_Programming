package com.example.assets_management.asset_master.entity;

import com.example.assets_management.asset_category.entity.AssetCategory;
import com.example.assets_management.asset_master.enums.AssetStatus;
import com.example.assets_management.asset_master.enums.AssetType;
import com.example.assets_management.asset_category.enums.DepreciationMethod;
import com.example.assets_management.asset_depreciation.enums.DepreciationStatus;
import com.example.assets_management.asset_model.entity.AssetModel;
import com.example.assets_management.vendor.entity.Vendor;
import com.example.assets_management.branch.entity.Branch;
import com.example.assets_management.company.entity.Company;
import com.example.assets_management.department.entity.Department;
import com.example.assets_management.employee.entity.Employee;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "assets")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@NoArgsConstructor
public class Asset {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(name = "asset_code", nullable = false, unique = true, length = 30)
    private String assetCode;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private AssetCategory category;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "model_id")
    private AssetModel model;

    @Enumerated(EnumType.STRING)
    @Column(name = "asset_type", nullable = false)
    private AssetType assetType;

    @Column(name = "serial_number", length = 100)
    private String serialNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column(name = "purchase_cost", precision = 14, scale = 2)
    private BigDecimal purchaseCost;

    @Column(name = "current_value", precision = 14, scale = 2)
    private BigDecimal currentValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "depreciation_method")
    private DepreciationMethod depreciationMethod;

    @Column(name = "useful_life_months")
    private Integer usefulLifeMonths;

    @Column(name = "salvage_value", precision = 14, scale = 2)
    private BigDecimal salvageValue;

    @Column(name = "depreciation_start_date")
    private LocalDate depreciationStartDate;

    @Column(name = "depreciation_end_date")
    private LocalDate depreciationEndDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "depreciation_status")
    private DepreciationStatus depreciationStatus;

    @Column(name = "warranty_expiry")
    private LocalDate warrantyExpiry;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssetStatus status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "current_owner_id")
    private Employee currentOwner;

    @Column(name = "current_location", length = 150)
    private String currentLocation;

    @Column(name = "qr_code_value", unique = true, length = 64, nullable = true)
    private String qrCodeValue;

    @Column(name = "barcode_value", unique = true, length = 64, nullable = true)
    private String barcodeValue;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
