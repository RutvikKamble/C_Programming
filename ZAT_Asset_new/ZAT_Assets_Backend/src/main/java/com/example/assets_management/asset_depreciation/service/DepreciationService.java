package com.example.assets_management.asset_depreciation.service;

import com.example.assets_management.asset_depreciation.dto.request.DepreciationPreviewRequest;
import com.example.assets_management.asset_depreciation.dto.response.DepreciationPreviewResponse;
import com.example.assets_management.asset_depreciation.dto.response.DepreciationResponse;
import com.example.assets_management.asset_depreciation.dto.response.DepreciationSummaryResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface DepreciationService {
    List<DepreciationResponse> getHistory(UUID assetId);
    DepreciationSummaryResponse getSummary(UUID assetId);
    DepreciationPreviewResponse preview(DepreciationPreviewRequest request);
    int run(LocalDate periodMonth);
    List<DepreciationResponse> report(UUID companyId, UUID branchId, UUID departmentId,
                                      UUID categoryId, UUID assetId,
                                      String method, LocalDate periodMonth);
}
