package com.example.assets_management.asset_tracking.service;

import com.example.assets_management.asset_tracking.dto.request.AssetTrackingRequest;
import com.example.assets_management.asset_tracking.dto.response.AssetTrackingResponse;

import java.util.List;
import java.util.UUID;

/**
 * Service contract for the Asset Tracking module.
 */
public interface AssetTrackingService {

    /**
     * Fetch all tracking events for an asset, newest first.
     *
     * @param assetId the asset id
     * @return list of tracking responses
     */
    List<AssetTrackingResponse> findByAssetId(UUID assetId);

    /**
     * Fetch all tracking events, optionally filtered by type.
     *
     * @param type optional type filter
     * @return list of tracking responses
     */
    List<AssetTrackingResponse> findAll(String type);

    /**
     * Create a new tracking event.
     *
     * @param request creation payload
     * @return the persisted tracking response
     */
    AssetTrackingResponse create(AssetTrackingRequest request);

    /**
     * Delete a tracking event by id.
     *
     * @param id tracking event id
     */
    void delete(UUID id);
}
