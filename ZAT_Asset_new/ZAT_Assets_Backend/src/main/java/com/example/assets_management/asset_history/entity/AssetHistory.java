package com.example.assets_management.asset_history.entity;

import com.example.assets_management.asset_master.entity.Asset;
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
 * Represents a single change event recorded against an asset.
 *
 * <p>The Asset History module captures lifecycle events such as creation,
 * updates, status changes, allocations, returns and deletes. Each row stores
 * the asset, the action performed, the optional field that changed, the old
 * and new values, and who performed the change.</p>
 *
 * @author ZAT Asset Management
 */
@Entity
@Table(name = "asset_history")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AssetHistory {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private UUID id;

    /** The asset this history entry belongs to. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    /** High level action, e.g. CREATED, UPDATED, STATUS_CHANGED, ALLOCATED, RETURNED, DELETED. */
    @Column(name = "action", nullable = false, length = 50)
    private String action;

    /** The field that changed, e.g. status, currentLocation, assetCode. */
    @Column(name = "field_changed", length = 100)
    private String fieldChanged;

    /** Previous value of the field (if any). */
    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;

    /** New value of the field (if any). */
    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;

    /** Natural language description of the change. */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /** Reference to the user (id or email) who performed the change. */
    @Column(name = "changed_by", length = 150)
    private String changedBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** History is immutable: business code must append a new event instead of editing the past. */
    @PreUpdate
    void preventUpdate() {
        throw new IllegalStateException("Asset history is append-only and cannot be updated");
    }

    /** History is immutable: entries cannot be deleted through JPA. */
    @PreRemove
    void preventDelete() {
        throw new IllegalStateException("Asset history is append-only and cannot be deleted");
    }
}
