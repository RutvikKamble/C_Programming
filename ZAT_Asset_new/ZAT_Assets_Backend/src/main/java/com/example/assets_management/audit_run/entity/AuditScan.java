package com.example.assets_management.audit_run.entity;

import com.example.assets_management.asset_master.entity.Asset;
import com.example.assets_management.employee.entity.Employee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_scan",
        uniqueConstraints = @UniqueConstraint(name = "uk_audit_scan_run_asset", columnNames = {"audit_run_id", "asset_id"}))
@Getter
@Setter
@NoArgsConstructor
public class AuditScan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "audit_run_id", nullable = false)
    private AuditRun auditRun;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @Column(name = "scanned_code", length = 100)
    private String scannedCode;

    @Column(name = "expected_location", length = 150)
    private String expectedLocation;

    @Column(name = "found_location", length = 150)
    private String foundLocation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "expected_owner_id")
    private Employee expectedOwner;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "actual_owner_id")
    private Employee actualOwner;

    @Column(name = "expected_status", length = 40)
    private String expectedStatus;

    @Column(name = "actual_condition", length = 50)
    private String actualCondition;

    @Column(name = "result", length = 40)
    private String result;

    @Column(nullable = false)
    private boolean discrepancy;

    @Column(name = "discrepancy_details", columnDefinition = "TEXT")
    private String discrepancyDetails;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;
}
