package com.example.assets_management.asset_requests.service;

import com.example.assets_management.asset_requests.entity.AssetRequest;

import java.util.List;
import java.util.UUID;

public interface AssetRequestService {
    AssetRequest createRequest(AssetRequest request);

    AssetRequest updateRequest(Long id, AssetRequest request);

    void deleteRequest(Long id);

    AssetRequest getRequestById(Long id);

    List<AssetRequest> getAllRequests();

    List<AssetRequest> getRequestsByAssetId(UUID assetId);

    List<AssetRequest> getRequestsByStatus(String status);

    // Existing employee workflow
    AssetRequest approveByDeptHead(Long id, String approvedBy);

    AssetRequest approveByAssetManager(Long id, String approvedBy);

    AssetRequest rejectRequest(Long id, String rejectedBy, String reason);

    AssetRequest fulfill(Long id, UUID assetId);

    // New controlled workflows
    AssetRequest send(Long id, String by);

    AssetRequest receive(Long id, String by);

    AssetRequest approve(Long id, String by);

    AssetRequest createAsset(Long id, String by);

    AssetRequest payBill(Long id, String by);

    AssetRequest transfer(Long id, String by);

    AssetRequest complete(Long id, String by);

    AssetRequest cancel(Long id, String by, String reason);
}
