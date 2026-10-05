package com.example.assets_management.asset_warranty.entity;

import com.example.assets_management.asset_master.entity.Asset;
import com.example.assets_management.asset_warranty.enums.AssetWarrantyStatus;
import com.example.assets_management.asset_warranty.enums.WarrantyTypes;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "asset_warranty")
@Getter
@Setter
@NoArgsConstructor
public class AssetWarranty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "asset is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    // MANUFACTURER / EXTENDED / AMC
    @NotNull(message = "warrantyType is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WarrantyTypes warrantyType;

    private String provider;

    private LocalDate startDate;

    @Column(updatable = false)
    private LocalDate endDate;

    // Store multiple coverage details as a collection
    @ElementCollection
    @CollectionTable(
            name = "asset_warranty_coverage_details",
            joinColumns = @JoinColumn(name = "warranty_id")
    )
    @Column(name = "detail")
    private List<String> coverageDetails = new ArrayList<>();

    // ACTIVE / EXPIRED / EXTENDED
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssetWarrantyStatus status;
}


/*
package com.example.assets_management.asset_warranty;

import com.example.assets_management.asset_master.Asset;
import com.example.assets_management.asset_vendor.Vendor;
import com.example.assets_management.asset_warranty.Enums.AssetWarrantyStatus;
import com.example.assets_management.asset_warranty.Enums.WarrantyTypes;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "asset_warranty")
@Getter
@Setter
@NoArgsConstructor
public class AssetWarranty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "asset is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    // MANUFACTURER / EXTENDED / AMC
    @NotNull(message = "warrantyType is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WarrantyTypes warrantyType;

    @ManyToOne
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    private LocalDate startDate;

    @Column(updatable = false)
    private LocalDate endDate;

    // Store multiple coverage details as a collection
    @ElementCollection
    @CollectionTable(
            name = "asset_warranty_coverage_details",
            joinColumns = @JoinColumn(name = "warranty_id")
    )
    @Column(name = "detail")
    private List<String> coverageDetails = new ArrayList<>();

    // ACTIVE / EXPIRED / EXTENDED
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssetWarrantyStatus status;
}
*/