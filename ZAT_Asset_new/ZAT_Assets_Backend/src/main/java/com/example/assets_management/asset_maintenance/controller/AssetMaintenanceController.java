package com.example.assets_management.asset_maintenance.controller;

import org.springframework.security.access.prepost.PreAuthorize;

import com.example.assets_management.asset_maintenance.service.AssetMaintenanceService;
import com.example.assets_management.asset_maintenance.dto.BulkUpdateRequest;
import com.example.assets_management.asset_maintenance.entity.AssetMaintenance;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/asset-maintenance")
public class AssetMaintenanceController {

    private final AssetMaintenanceService maintenanceService;

    public AssetMaintenanceController(AssetMaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    // Creates a new maintenance record
    @PreAuthorize("hasAuthority(\'Asset.Maintenance.Create\')")
    @PostMapping
    public ResponseEntity<AssetMaintenance> createMaintenance(@Valid @RequestBody AssetMaintenance maintenance) {
        return new ResponseEntity<>(maintenanceService.createMaintenance(maintenance), HttpStatus.CREATED);
    }

    // Fully updates an existing maintenance record (all fields replaced)
    @PreAuthorize("hasAuthority(\'Asset.Maintenance.Update\')")
    @PutMapping("/{id}")
    public ResponseEntity<AssetMaintenance> updateMaintenance(@PathVariable Long id,
                                                              @Valid @RequestBody AssetMaintenance maintenance) {
        return new ResponseEntity<>(maintenanceService.updateMaintenance(id, maintenance), HttpStatus.OK);
    }

    // Partially updates an existing maintenance record (only non‑null fields are updated)
    @PreAuthorize("hasAuthority(\'Asset.Maintenance.Update\')")
    @PatchMapping("/{id}")
    public ResponseEntity<AssetMaintenance> patchMaintenance(@PathVariable Long id,
                                                             @RequestBody AssetMaintenance maintenance) {
        return new ResponseEntity<>(maintenanceService.patchMaintenance(id, maintenance), HttpStatus.OK);
    }

    // Bulk delete maintenance records by list of IDs
    @PreAuthorize("hasAuthority(\'Asset.Maintenance.Delete\')")
    @PostMapping("/bulk-delete")
    public ResponseEntity<Void> bulkDelete(@RequestBody List<Long> ids) {
        maintenanceService.bulkDelete(ids);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Bulk update maintenance records: update the same fields for all given IDs
    @PreAuthorize("hasAuthority(\'Asset.Maintenance.Update\')")
    @PatchMapping("/bulk-update")
    public ResponseEntity<List<AssetMaintenance>> bulkUpdate(@RequestBody BulkUpdateRequest request) {
        return new ResponseEntity<>(maintenanceService.bulkUpdate(request.getIds(), request.getUpdates()), HttpStatus.OK);
    }

    // Deletes a maintenance record by ID
    @PreAuthorize("hasAuthority(\'Asset.Maintenance.Delete\')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMaintenance(@PathVariable Long id) {
        maintenanceService.deleteMaintenance(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Retrieves a single maintenance record by ID
    @PreAuthorize("hasAuthority(\'Asset.Maintenance.View\')")
    @GetMapping("/{id}")
    public ResponseEntity<AssetMaintenance> getMaintenanceById(@PathVariable Long id) {
        return new ResponseEntity<>(maintenanceService.getMaintenanceById(id), HttpStatus.OK);
    }

    // Retrieves all maintenance records
    @PreAuthorize("hasAuthority(\'Asset.Maintenance.View\')")
    @GetMapping
    public ResponseEntity<List<AssetMaintenance>> getAllMaintenance() {
        return new ResponseEntity<>(maintenanceService.getAllMaintenance(), HttpStatus.OK);
    }

    // Retrieves all maintenance records for a specific asset
    @PreAuthorize("hasAuthority(\'Asset.Maintenance.View\')")
    @GetMapping("/asset/{assetId}")
    public ResponseEntity<List<AssetMaintenance>> getMaintenanceByAssetId(@PathVariable UUID assetId) {
        return new ResponseEntity<>(maintenanceService.getMaintenanceByAssetId(assetId), HttpStatus.OK);
    }

    // Retrieves the single soonest upcoming maintenance record for a specific asset
    @PreAuthorize("hasAuthority(\'Asset.Maintenance.View\')")
    @GetMapping("/{assetId}/upcoming")
    public ResponseEntity<AssetMaintenance> getUpcomingByAssetId(@PathVariable UUID assetId) {
        return new ResponseEntity<>(maintenanceService.getUpcomingByAssetId(assetId), HttpStatus.OK);
    }

    // Retrieves the single most recent completed maintenance record for a specific asset
    @PreAuthorize("hasAuthority(\'Asset.Maintenance.View\')")
    @GetMapping("/{assetId}/completed")
    public ResponseEntity<AssetMaintenance> getCompletedByAssetId(@PathVariable UUID assetId) {
        return new ResponseEntity<>(maintenanceService.getCompletedByAssetId(assetId), HttpStatus.OK);
    }

}
