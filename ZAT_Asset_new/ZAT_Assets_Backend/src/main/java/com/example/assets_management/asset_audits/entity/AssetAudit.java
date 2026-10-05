package com.example.assets_management.asset_audits.entity;

import com.example.assets_management.asset_master.entity.Asset;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "asset_audit")
@Getter
@Setter
@NoArgsConstructor
public class AssetAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "asset is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    private LocalDate auditDate;

    private String auditedBy;

    private String locationVerified;

    // Good / Damaged / Missing / NeedsRepair
    private String condition;

    private String discrepancyNotes;

    // Verified / DiscrepancyFound
    private String status;
}
