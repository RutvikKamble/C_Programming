package com.example.assets_management.audit_run.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.example.assets_management.audit_run.entity.*;
import com.example.assets_management.audit_run.repository.*;
import com.example.assets_management.asset_master.entity.Asset;
import com.example.assets_management.asset_master.repository.AssetRepository;
import com.example.assets_management.exception.ResourceNotFoundException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/audit-runs")
public class AuditRunController {
    private final AuditRunRepository runs;
    private final AuditScanRepository scans;
    private final AssetRepository assets;

    public AuditRunController(AuditRunRepository runs, AuditScanRepository scans, AssetRepository assets) {
        this.runs = runs;
        this.scans = scans;
        this.assets = assets;
    }

    @PreAuthorize("hasAuthority(\'Asset.Asset_Audit.View\')")
    @GetMapping
    public List<AuditRun> all() {
        return runs.findAll();
    }

    @PreAuthorize("hasAuthority(\'Asset.Asset_Audit.View\')")
    @GetMapping("/{id}")
    public AuditRun one(@PathVariable Long id) {
        return runs.findById(id).orElseThrow(() -> new ResourceNotFoundException("Audit run not found"));
    }

    @PreAuthorize("hasAuthority(\'Asset.Asset_Audit.View\')")
    @GetMapping("/{id}/scans")
    public List<AuditScan> scans(@PathVariable Long id) {
        return scans.findByAuditRun(one(id));
    }

    @PreAuthorize("hasAuthority(\'Asset.Asset_Audit.Run\')")
    @PostMapping
    public ResponseEntity<AuditRun> start(@RequestBody AuditRun in) {
        AuditRun r = new AuditRun();
        r.setName(in.getName());
        r.setAuditedBy(in.getAuditedBy());
        r.setStatus("OPEN");
        r.setStartedAt(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CREATED).body(runs.save(r));
    }

    @PreAuthorize("hasAuthority(\'Asset.Asset_Audit.Scan\')")
    @PostMapping("/{id}/scan")
    public ResponseEntity<AuditScan> scan(@PathVariable Long id, @RequestBody Map<String, String> body) {
        AuditRun r = one(id);
        if (!"OPEN".equals(r.getStatus())) throw new IllegalStateException("Audit run is not open");
        String code = body.get("code");
        if (code == null || code.isBlank()) throw new IllegalArgumentException("code is required");
        Asset a = assets.findByAssetCode(code).orElseGet(() -> assets.findByQrCodeValue(code).orElse(null));
        if (a == null)
            a = assets.findByBarcodeValue(code).orElseThrow(() -> new ResourceNotFoundException("Asset not found for scan code"));
        if (scans.existsByAuditRunAndAsset(r, a))
            throw new IllegalStateException("Asset already scanned in this audit");
        AuditScan s = new AuditScan();
        s.setAuditRun(r);
        s.setAsset(a);
        s.setScannedCode(code);
        s.setExpectedLocation(a.getCurrentLocation());
        s.setFoundLocation(body.get("foundLocation"));
        s.setDiscrepancy(a.getCurrentLocation() != null && body.get("foundLocation") != null && !a.getCurrentLocation().equalsIgnoreCase(body.get("foundLocation")));
        return ResponseEntity.status(HttpStatus.CREATED).body(scans.save(s));
    }

    @PreAuthorize("hasAuthority(\'Asset.Asset_Audit.Reconcile\')")
    @PostMapping("/{id}/reconcile")
    public List<AuditScan> reconcile(@PathVariable Long id) {
        AuditRun r = one(id);
        if ("CLOSED".equals(r.getStatus())) throw new IllegalStateException("Audit run is closed");
        List<AuditScan> found = scans.findByAuditRun(r);
        Set<UUID> foundIds = new HashSet<>();
        for (AuditScan s : found) foundIds.add(s.getAsset().getId());
        for (Asset a : assets.findAllActive()) {
            if (!foundIds.contains(a.getId())) {
                if (!scans.existsByAuditRunAndAsset(r, a)) {
                    AuditScan s = new AuditScan();
                    s.setAuditRun(r);
                    s.setAsset(a);
                    s.setScannedCode("NOT_SCANNED");
                    s.setExpectedLocation(a.getCurrentLocation());
                    s.setFoundLocation(null);
                    s.setDiscrepancy(true);
                    scans.save(s);
                }
            }
        }
        return scans.findByAuditRun(r);
    }

    @PreAuthorize("hasAuthority(\'Asset.Asset_Audit.Close\')")
    @PostMapping("/{id}/close")
    public AuditRun close(@PathVariable Long id) {
        AuditRun r = one(id);
        r.setStatus("CLOSED");
        r.setClosedAt(LocalDateTime.now());
        return runs.save(r);
    }
}
