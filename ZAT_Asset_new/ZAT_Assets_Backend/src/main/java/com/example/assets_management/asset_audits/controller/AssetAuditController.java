package com.example.assets_management.asset_audits.controller;

import org.springframework.security.access.prepost.PreAuthorize;

import com.example.assets_management.asset_audits.service.AssetAuditService;
import com.example.assets_management.asset_audits.entity.AssetAudit;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/asset-audits")
public class AssetAuditController {

    private final AssetAuditService auditService;

    public AssetAuditController(AssetAuditService auditService) {
        this.auditService = auditService;
    }

    @PreAuthorize("hasAuthority(\'Asset.Asset_Audit.Create\')")
    @PostMapping
    public ResponseEntity<AssetAudit> createAudit(@Valid @RequestBody AssetAudit audit) {
        return new ResponseEntity<>(auditService.createAudit(audit), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority(\'Asset.Asset_Audit.Update\')")
    @PutMapping("/{id}")
    public ResponseEntity<AssetAudit> updateAudit(@PathVariable Long id,
                                                  @Valid @RequestBody AssetAudit audit) {
        return new ResponseEntity<>(auditService.updateAudit(id, audit), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority(\'Asset.Asset_Audit.Delete\')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAudit(@PathVariable Long id) {
        auditService.deleteAudit(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAuthority(\'Asset.Asset_Audit.View\')")
    @GetMapping("/{id}")
    public ResponseEntity<AssetAudit> getAuditById(@PathVariable Long id) {
        return new ResponseEntity<>(auditService.getAuditById(id), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority(\'Asset.Asset_Audit.View\')")
    @GetMapping
    public ResponseEntity<List<AssetAudit>> getAllAudits() {
        return new ResponseEntity<>(auditService.getAllAudits(), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority(\'Asset.Asset_Audit.View\')")
    @GetMapping("/asset/{assetId}")
    public ResponseEntity<List<AssetAudit>> getAuditsByAssetId(@PathVariable UUID assetId) {
        return new ResponseEntity<>(auditService.getAuditsByAssetId(assetId), HttpStatus.OK);
    }
}
