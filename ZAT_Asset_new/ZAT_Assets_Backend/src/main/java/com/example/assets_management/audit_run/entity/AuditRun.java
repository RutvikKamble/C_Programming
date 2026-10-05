package com.example.assets_management.audit_run.entity;

import com.example.assets_management.asset_category.entity.AssetCategory;
import com.example.assets_management.branch.entity.Branch;
import com.example.assets_management.company.entity.Company;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_run")
@Getter
@Setter
@NoArgsConstructor
public class AuditRun {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private AssetCategory category;

    @Column(name = "audited_by", length = 150)
    private String auditedBy;

    @Column(name = "audit_date")
    private LocalDate auditDate;

    @Column(nullable = false, length = 30)
    private String status = "OPEN";

    private LocalDateTime startedAt;
    private LocalDateTime closedAt;

    private Integer totalExpectedAssets = 0;
    private Integer totalVerifiedAssets = 0;
    private Integer totalMissingAssets = 0;
    private Integer totalDamagedAssets = 0;
    private Integer totalDiscrepancies = 0;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @PrePersist
    void onCreate() {
        if (startedAt == null) startedAt = LocalDateTime.now();
        if (auditDate == null) auditDate = LocalDate.now();
    }
}
