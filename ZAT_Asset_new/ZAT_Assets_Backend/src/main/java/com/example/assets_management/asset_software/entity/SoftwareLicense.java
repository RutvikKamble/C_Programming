package com.example.assets_management.asset_software.entity;

import com.example.assets_management.asset_master.entity.Asset;
import com.example.assets_management.asset_software.enums.SoftwareLicenseStatus;
import com.example.assets_management.vendor.entity.Vendor;
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
@Table(name = "software_licenses", uniqueConstraints = @UniqueConstraint(name = "uk_software_license_asset", columnNames = "asset_id"))
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SoftwareLicense {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(nullable = false, updatable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "asset_id", nullable = false, unique = true)
    private Asset asset;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 100)
    private String publisher;

    @Column(length = 100)
    private String manufacturer;

    @Column(name = "license_category", length = 100)
    private String licenseCategory;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "supplier_id")
    private Vendor supplier;

    @Column(name = "license_key", nullable = false, length = 1000)
    private String encryptedLicenseKey;

    @Column(name = "seat_count", nullable = false)
    private Integer seatCount;

    @Column(name = "seats_used", nullable = false)
    private Integer seatsUsed = 0;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column(name = "purchase_cost", nullable = false, precision = 12, scale = 2)
    private BigDecimal purchaseCost = BigDecimal.ZERO;

    @Column(name = "renewal_cost", nullable = false, precision = 12, scale = 2)
    private BigDecimal renewalCost = BigDecimal.ZERO;

    @Column(name = "order_number", length = 100)
    private String orderNumber;

    @Column(name = "purchase_order", length = 100)
    private String purchaseOrder;

    @Column(name = "license_contact_name", length = 150)
    private String licenseContactName;

    @Column(name = "license_contact_email", length = 150)
    private String licenseContactEmail;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false)
    private Boolean reassignable = true;

    @Column(nullable = false)
    private Boolean maintained = true;

    @Column(name = "termination_date")
    private LocalDate terminationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SoftwareLicenseStatus status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
