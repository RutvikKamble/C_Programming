package com.example.assets_management.asset_disposal.entity;

import com.example.assets_management.asset_disposal.enums.DisposalStatus;
import com.example.assets_management.asset_master.entity.Asset;
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

/**
 * Represents the controlled end-of-life/disposal workflow for an asset.
 *
 * <p>The asset itself is never deleted. Once the disposal is finalized,
 * the linked Asset is moved to CLOSED and remains available through its
 * normal history/audit trail.</p>
 */
@Entity
@Table(name = "disposal_records", indexes = {
        @Index(name = "idx_disposal_asset", columnList = "asset_id"),
        @Index(name = "idx_disposal_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AssetDisposal {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @Column(name = "reason", nullable = false, length = 30)
    private String reason;

    /** Asset Manager/person who raised the disposal review. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reviewed_by")
    private Employee reviewedBy;

    /** Admin/Finance approver. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "approved_by")
    private Employee approvedBy;

    /** Person who physically finalized/recorded the disposal. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "finalized_by")
    private Employee finalizedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private DisposalStatus status;

    @Column(name = "disposal_date")
    private LocalDate disposalDate;

    @Column(name = "disposal_value", precision = 14, scale = 2)
    private BigDecimal disposalValue;

    @Column(name = "disposal_method", length = 40)
    private String disposalMethod;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
