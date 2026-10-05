package com.example.assets_management.asset_software.service;

import com.example.assets_management.asset_software.dto.request.LicenseAllocationRequest;
import com.example.assets_management.asset_software.dto.request.LicenseAllocationUpdateRequest;
import com.example.assets_management.asset_software.dto.request.BulkLicenseAllocationRequest;
import com.example.assets_management.asset_software.dto.response.LicenseAllocationResponse;

import java.util.List;
import java.util.UUID;

public interface LicenseAllocationService {
    LicenseAllocationResponse allocate(UUID licenseId, LicenseAllocationRequest request);
    List<LicenseAllocationResponse> findByLicense(UUID licenseId);
    LicenseAllocationResponse findById(UUID licenseId, UUID allocationId);
    LicenseAllocationResponse update(UUID licenseId, UUID allocationId, LicenseAllocationUpdateRequest request);
    LicenseAllocationResponse release(UUID licenseId, UUID allocationId, String changedBy);
    List<LicenseAllocationResponse> bulkAllocate(UUID licenseId, BulkLicenseAllocationRequest request);
    int bulkRelease(UUID licenseId, String changedBy);
}
