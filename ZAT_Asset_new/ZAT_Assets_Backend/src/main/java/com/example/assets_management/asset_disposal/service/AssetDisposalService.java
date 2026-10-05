package com.example.assets_management.asset_disposal.service;

import com.example.assets_management.asset_disposal.dto.request.DisposalFinalizeRequest;
import com.example.assets_management.asset_disposal.dto.request.DisposalReviewRequest;
import com.example.assets_management.asset_disposal.dto.response.DisposalResponse;
import com.example.assets_management.asset_disposal.enums.DisposalStatus;

import java.util.List;
import java.util.UUID;

public interface AssetDisposalService {

    DisposalResponse createReview(UUID assetId, DisposalReviewRequest request);

    DisposalResponse approve(UUID disposalId, UUID approvedById, String remarks);

    DisposalResponse reject(UUID disposalId, UUID rejectedById, String rejectionReason);

    DisposalResponse finalizeDisposal(UUID disposalId, DisposalFinalizeRequest request);

    DisposalResponse findById(UUID disposalId);

    List<DisposalResponse> findAll(DisposalStatus status);

    List<DisposalResponse> findByAssetId(UUID assetId);
}
