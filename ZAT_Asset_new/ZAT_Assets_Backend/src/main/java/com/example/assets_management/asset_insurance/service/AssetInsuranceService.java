package com.example.assets_management.asset_insurance.service;

import com.example.assets_management.asset_insurance.dto.request.InsuranceCreateRequest;
import com.example.assets_management.asset_insurance.dto.request.InsuranceUpdateRequest;
import com.example.assets_management.asset_insurance.dto.response.InsuranceResponse;
import com.example.assets_management.asset_insurance.enums.InsuranceStatus;

import java.util.List;
import java.util.UUID;

public interface AssetInsuranceService {
    InsuranceResponse create(InsuranceCreateRequest request);
    InsuranceResponse update(UUID id, InsuranceUpdateRequest request);
    void delete(UUID id);
    InsuranceResponse findById(UUID id);
    List<InsuranceResponse> findAll(InsuranceStatus status);
    List<InsuranceResponse> findByAssetId(UUID assetId);
    List<InsuranceResponse> findExpiringSoon(int days);
}
