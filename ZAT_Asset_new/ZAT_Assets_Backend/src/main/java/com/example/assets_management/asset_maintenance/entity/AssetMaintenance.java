package com.example.assets_management.asset_maintenance.entity;

import com.example.assets_management.asset_maintenance.enums.MaintenanceType;
import com.example.assets_management.asset_master.entity.Asset;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "asset_maintenance")
@Getter
@Setter
@NoArgsConstructor
public class AssetMaintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "asset is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    // PREVENTIVE / CORRECTIVE / PREDICTIVE / SCHEDULED
    @NotNull(message = "maintenanceType is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaintenanceType maintenanceType;

    private LocalDate scheduledDate;

    private LocalDate completedDate;

    private String performedBy;

    private BigDecimal cost;

    private String status;

    private String remarks;

    private LocalDate nextDueDate;

    /** PM schedule that generated this maintenance record, when applicable. */
    @Column(name = "preventive_maintenance_schedule_id")
    private UUID preventiveMaintenanceScheduleId;
}
