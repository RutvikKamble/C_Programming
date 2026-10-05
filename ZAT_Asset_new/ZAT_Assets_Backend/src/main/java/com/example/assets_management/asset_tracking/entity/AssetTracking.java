package com.example.assets_management.asset_tracking.entity;

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
 * Represents a single tracking event for an asset.
 *
 * <p>The Asset Tracking module records whenever an asset moves, changes
 * location, is checked in/out, or changes state. Each event stores the
 * asset, the action performed (e.g. MOVED, CHECKED_OUT, LOCATION_CHANGED),
 * the responsible user, and the location at the time of the event.</p>
 *
 * @author ZAT Asset Management
 */
@Entity
@Table(name = "asset_tracking")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AssetTracking {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private UUID id;

    /** The asset being tracked. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    /** Action performed, e.g. MOVED, CHECKED_OUT, CHECKED_IN, LOCATION_CHANGED. */
    @Column(name = "action", nullable = false, length = 60)
    private String action;

    /** Who performed the action (id or email). */
    /*
    @Column(name = "user", length = 150)
    private String user;
    */
    @Column(name = "user_name")
    private String user;

    /** Location at the time of the event. */
    @Column(name = "location", length = 150)
    private String location;

    /** Event category, e.g. MOVEMENT, ASSIGNMENT, MAINTENANCE. */
    @Column(name = "type", length = 50)
    private String type;

    /** Optional free-form notes. */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "event_time")
    private LocalDateTime eventTime;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
