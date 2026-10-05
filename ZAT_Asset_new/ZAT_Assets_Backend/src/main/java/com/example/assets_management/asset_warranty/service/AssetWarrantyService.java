package com.example.assets_management.asset_warranty.service;

import com.example.assets_management.asset_warranty.entity.AssetWarranty;

import java.util.List;
import java.util.UUID;

public interface AssetWarrantyService {

    // Creates a new warranty record
    AssetWarranty createWarranty(AssetWarranty warranty);

    // Fully updates an existing warranty record (all fields replaced)
    AssetWarranty updateWarranty(Long id, AssetWarranty warranty);

    // Deletes a warranty record by ID
    void deleteWarranty(Long id);

    // Retrieves a warranty record by ID
    AssetWarranty getWarrantyById(Long id);

    // Retrieves all warranty records
    List<AssetWarranty> getAllWarranty();

    // Retrieves all warranties for a specific asset
    List<AssetWarranty> getWarrantyByAssetId(UUID assetId);

    // Retrieves all warranties expiring within the next 30 days
    List<AssetWarranty> getExpiringSoonWarranties();
}
