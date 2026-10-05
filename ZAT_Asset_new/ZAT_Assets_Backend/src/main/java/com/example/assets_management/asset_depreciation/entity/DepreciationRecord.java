package com.example.assets_management.asset_depreciation.entity;

import com.example.assets_management.asset_depreciation.enums.DepreciationStatus;
import com.example.assets_management.asset_category.enums.DepreciationMethod;
import com.example.assets_management.asset_master.entity.Asset;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "depreciation_records",
       uniqueConstraints = @UniqueConstraint(name = "uk_depreciation_asset_period", columnNames = {"asset_id", "period_month"}),
       indexes = @Index(name = "idx_depreciation_asset_period", columnList = "asset_id, period_month"))
@Getter
@Setter
@NoArgsConstructor
public class DepreciationRecord {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "asset_id", nullable = false, updatable = false)
    private Asset asset;

    @Column(name = "period_month", nullable = false, updatable = false)
    private LocalDate periodMonth;

    @Column(name = "opening_value", nullable = false, precision = 14, scale = 2, updatable = false)
    private BigDecimal openingValue;

    @Column(name = "depreciation_amount", nullable = false, precision = 14, scale = 2, updatable = false)
    private BigDecimal depreciationAmount;

    @Column(name = "accumulated_depreciation", nullable = false, precision = 14, scale = 2, updatable = false)
    private BigDecimal accumulatedDepreciation;

    @Column(name = "closing_value", nullable = false, precision = 14, scale = 2, updatable = false)
    private BigDecimal closingValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "depreciation_method", nullable = false, updatable = false)
    private DepreciationMethod depreciationMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "depreciation_status", nullable = false, updatable = false)
    private DepreciationStatus depreciationStatus;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
