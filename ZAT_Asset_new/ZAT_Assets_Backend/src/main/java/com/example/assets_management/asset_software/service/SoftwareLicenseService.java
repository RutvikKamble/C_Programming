package com.example.assets_management.asset_software.service;

import com.example.assets_management.asset_software.dto.request.SoftwareLicenseCreateRequest;
import com.example.assets_management.asset_software.dto.request.SoftwareLicenseUpdateRequest;
import com.example.assets_management.asset_software.dto.response.SoftwareLicenseResponse;
import com.example.assets_management.asset_software.enums.SoftwareLicenseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface SoftwareLicenseService {
    SoftwareLicenseResponse create(SoftwareLicenseCreateRequest request);
    List<SoftwareLicenseResponse> findAll();
    Page<SoftwareLicenseResponse> search(String search, String publisher, String category, SoftwareLicenseStatus status, Boolean maintained, Boolean reassignable, Pageable pageable);
    SoftwareLicenseResponse findById(UUID id);
    SoftwareLicenseResponse findByAssetId(UUID assetId);
    SoftwareLicenseResponse update(UUID id, SoftwareLicenseUpdateRequest request);
    List<SoftwareLicenseResponse> findExpiringSoon(int days);
    byte[] exportCsv();
    int importCsv(byte[] csv, String changedBy);
}
