package com.example.assets_management.asset_requests.entity;

import com.example.assets_management.asset_category.entity.AssetCategory;
import com.example.assets_management.asset_master.entity.Asset;
import com.example.assets_management.asset_master.enums.AssetStatus;
import com.example.assets_management.asset_master.enums.AssetType;
import com.example.assets_management.asset_model.entity.AssetModel;
import com.example.assets_management.asset_requests.enums.AssetRequestPriority;
import com.example.assets_management.asset_requests.enums.AssetRequestStatus;
import com.example.assets_management.asset_requests.enums.AssetRequestType;
import com.example.assets_management.branch.entity.Branch;
import com.example.assets_management.company.entity.Company;
import com.example.assets_management.department.entity.Department;
import com.example.assets_management.employee.entity.Employee;
import com.example.assets_management.vendor.entity.Vendor;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "asset_request")
@Getter
@Setter
@NoArgsConstructor
public class AssetRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "requestType is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssetRequestType requestType;

    /**
     * Existing single-asset relationship retained for transfer and legacy employee requests.
     * For CREATE/PURCHASE requests this remains null until the Asset Master is actually created.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "asset_id")
    private Asset asset;

    /** All Asset Master records created from this request. */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "asset_request_assets",
            joinColumns = @JoinColumn(name = "asset_request_id"),
            inverseJoinColumns = @JoinColumn(name = "asset_id")
    )
    private List<Asset> assets = new ArrayList<>();

    private String requestedCategory;

    // Asset template fields used by CREATE/PURCHASE before an Asset Master exists.
    // They prevent a transient Asset entity from being persisted with the request.
    @Column(name = "asset_template_name", length = 150)
    private String assetName;

    @Column(name = "asset_template_code", length = 30)
    private String assetCode;

    @Column(name = "asset_template_serial_number", length = 100)
    private String assetSerialNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "asset_template_type", length = 50)
    private AssetType assetType;

    @Column(name = "asset_template_company_id")
    private UUID assetCompanyId;

    @Column(name = "asset_template_branch_id")
    private UUID assetBranchId;

    @Column(name = "asset_template_department_id")
    private UUID assetDepartmentId;

    @Column(name = "asset_template_category_id")
    private UUID assetCategoryId;

    @Column(name = "asset_template_model_id")
    private UUID assetModelId;

    @Column(name = "asset_template_current_value", precision = 14, scale = 2)
    private BigDecimal assetCurrentValue;

    @Column(name = "asset_template_purchase_date")
    private LocalDate assetPurchaseDate;

    @Column(name = "asset_template_warranty_expiry")
    private LocalDate assetWarrantyExpiry;

    @Enumerated(EnumType.STRING)
    @Column(name = "asset_template_status", length = 50)
    private AssetStatus assetStatus;

    @Column(name = "asset_template_location", length = 150)
    private String assetCurrentLocation;

    // Existing employee request field. Nullable for branch/head-office requests.
    private String employeeId;
    private String employeeName;
    private String department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by_id")
    private Employee requestedBy;

    private LocalDate requestDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssetRequestPriority priority = AssetRequestPriority.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssetRequestStatus status = AssetRequestStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_company_id")
    private Company sourceCompany;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_branch_id")
    private Branch sourceBranch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_company_id")
    private Company destinationCompany;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_branch_id")
    private Branch destinationBranch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    @Column(precision = 14, scale = 2)
    private BigDecimal purchaseCost;

    @Column(length = 100)
    private String invoiceNumber;

    @Column(precision = 14, scale = 2)
    private BigDecimal invoiceAmount;

    /** Number of Asset Master records to create. For PURCHASE, total invoice = purchaseCost * quantity. */
    private Integer quantity;

    @Column(name = "received_asset_id")
    private UUID receivedAssetId;

    private String approvedBy;
    private LocalDateTime approvedDate;
    private String receivedBy;
    private LocalDateTime receivedDate;
    private String paidBy;
    private LocalDateTime paidDate;

    private String departmentApprovedBy;
    private LocalDateTime departmentApprovedAt;
    private String managerApprovedBy;
    private LocalDateTime managerApprovedAt;
    private UUID fulfilledAllocationId;

    private String rejectionReason;
    private String remarks;

    @Version
    private Long version;
}

/*
package com.example.assets_management.asset_requests.entity;

import com.example.assets_management.asset_master.entity.Asset;
import com.example.assets_management.asset_requests.enums.AssetRequestPriority;
import com.example.assets_management.asset_requests.enums.AssetRequestStatus;
import com.example.assets_management.asset_requests.enums.AssetRequestType;
import com.example.assets_management.branch.entity.Branch;
import com.example.assets_management.company.entity.Company;
import com.example.assets_management.employee.entity.Employee;
import com.example.assets_management.vendor.entity.Vendor;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "asset_request")
@Getter
@Setter
@NoArgsConstructor
public class AssetRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "requestType is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssetRequestType requestType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "asset_id")
    private Asset asset;

    private String requestedCategory;

    // Existing employee request field. Nullable for branch/head-office requests.
    private String employeeId;
    private String employeeName;
    private String department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by_id")
    private Employee requestedBy;

    private LocalDate requestDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssetRequestPriority priority = AssetRequestPriority.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssetRequestStatus status = AssetRequestStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_company_id")
    private Company sourceCompany;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_branch_id")
    private Branch sourceBranch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_company_id")
    private Company destinationCompany;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_branch_id")
    private Branch destinationBranch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    @Column(precision = 14, scale = 2)
    private BigDecimal purchaseCost;

    @Column(length = 100)
    private String invoiceNumber;

    @Column(precision = 14, scale = 2)
    private BigDecimal invoiceAmount;

    private Integer quantity;

    @Column(name = "received_asset_id")
    private UUID receivedAssetId;

    private String approvedBy;
    private LocalDateTime approvedDate;
    private String receivedBy;
    private LocalDateTime receivedDate;
    private String paidBy;
    private LocalDateTime paidDate;

    private String departmentApprovedBy;
    private LocalDateTime departmentApprovedAt;
    private String managerApprovedBy;
    private LocalDateTime managerApprovedAt;
    private UUID fulfilledAllocationId;

    private String rejectionReason;
    private String remarks;

    @Version
    private Long version;
}
*/