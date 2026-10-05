package com.example.assets_management.asset_maintenance.service;

import com.example.assets_management.asset_maintenance.entity.AssetMaintenance;

import java.util.List;
import java.util.UUID;

public interface AssetMaintenanceService {

    // Creates a new maintenance record
    AssetMaintenance createMaintenance(AssetMaintenance maintenance);

    // Fully updates an existing maintenance record (all fields replaced)
    AssetMaintenance updateMaintenance(Long id, AssetMaintenance maintenance);

    // Partially updates an existing maintenance record (only non‑null fields are updated)
    AssetMaintenance patchMaintenance(Long id, AssetMaintenance maintenance);

    // Bulk delete maintenance records by list of IDs
    void bulkDelete(List<Long> ids);

    // Bulk update: applies the same fields (non‑null) to all given IDs
    List<AssetMaintenance> bulkUpdate(List<Long> ids, AssetMaintenance updates);

    // Deletes a maintenance record by ID
    void deleteMaintenance(Long id);

    // Retrieves a maintenance record by ID
    AssetMaintenance getMaintenanceById(Long id);

    // Retrieves all maintenance records
    List<AssetMaintenance> getAllMaintenance();

    // Retrieves all maintenance records for a specific asset
    List<AssetMaintenance> getMaintenanceByAssetId(UUID assetId);

    // Retrieves the single soonest upcoming maintenance record for a specific asset
    AssetMaintenance getUpcomingByAssetId(UUID assetId);

    // Retrieves the single most recent completed maintenance record for a specific asset
    AssetMaintenance getCompletedByAssetId(UUID assetId);
}
