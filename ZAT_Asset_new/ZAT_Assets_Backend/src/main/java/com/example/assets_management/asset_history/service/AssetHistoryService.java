package com.example.assets_management.asset_history.service;

import com.example.assets_management.asset_history.dto.request.AssetHistoryRequest;
import com.example.assets_management.asset_history.dto.response.AssetHistoryResponse;
import com.example.assets_management.asset_history.enums.AssetHistoryEventType;
import com.example.assets_management.asset_master.entity.Asset;

import java.util.List;
import java.util.UUID;

/**
 * Central service for Asset History.
 *
 * Asset History is an append-only lifecycle trail for assets.
 *
 * Existing modules may continue using create(AssetHistoryRequest)
 * for backward compatibility, while newer integrations should use
 * recordEvent(...).
 */
public interface AssetHistoryService {

    /**
     * Fetch all history entries for a single asset, newest first.
     *
     * @param assetId asset UUID
     * @return asset history entries
     */
    List<AssetHistoryResponse> findHistoryByAssetId(UUID assetId);

    /**
     * Fetch all history entries, optionally filtered by action.
     *
     * @param action optional history action
     * @return asset history entries
     */
    List<AssetHistoryResponse> findAll(String action);

    /**
     * Legacy/backward-compatible history creation method.
     *
     * Existing modules such as depreciation, disposal, software asset,
     * vehicle and other modules may still call:
     *
     * historyService.create(new AssetHistoryRequest(...))
     *
     * This method delegates internally to the centralized recordEvent(...)
     * implementation.
     *
     * @param request history creation request
     * @return saved history response
     */
    AssetHistoryResponse create(AssetHistoryRequest request);

    /**
     * Record a standardized business event using an enum.
     *
     * Preferred method for new module integrations.
     */
    AssetHistoryResponse recordEvent(
            UUID assetId,
            AssetHistoryEventType eventType,
            String fieldChanged,
            String oldValue,
            String newValue,
            String description,
            String changedBy
    );

    /**
     * Record a custom/legacy business event using a String action.
     *
     * Useful for backward compatibility with existing modules.
     */
    AssetHistoryResponse recordEvent(
            UUID assetId,
            String action,
            String fieldChanged,
            String oldValue,
            String newValue,
            String description,
            String changedBy
    );

    /**
     * Convenience method when the Asset entity is already available.
     */
    AssetHistoryResponse recordEvent(
            Asset asset,
            AssetHistoryEventType eventType,
            String fieldChanged,
            String oldValue,
            String newValue,
            String description,
            String changedBy
    );
}