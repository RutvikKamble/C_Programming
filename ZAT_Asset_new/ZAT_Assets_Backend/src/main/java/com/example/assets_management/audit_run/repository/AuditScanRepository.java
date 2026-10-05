package com.example.assets_management.audit_run.repository;

import com.example.assets_management.asset_master.entity.Asset;
import com.example.assets_management.audit_run.entity.AuditRun;
import com.example.assets_management.audit_run.entity.AuditScan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuditScanRepository extends JpaRepository<AuditScan, Long> {
    List<AuditScan> findByAuditRun(AuditRun run);
    Optional<AuditScan> findByAuditRunAndAsset(AuditRun run, Asset asset);
    boolean existsByAuditRunAndAsset(AuditRun run, Asset asset);
    List<AuditScan> findByAuditRunAndDiscrepancyTrue(AuditRun run);
    default List<AuditScan> findExceptions(AuditRun run) { return findByAuditRunAndDiscrepancyTrue(run); }
}
