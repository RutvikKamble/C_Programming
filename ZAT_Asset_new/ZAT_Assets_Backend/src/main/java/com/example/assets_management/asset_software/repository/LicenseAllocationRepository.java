package com.example.assets_management.asset_software.repository;

import com.example.assets_management.asset_software.entity.LicenseAllocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LicenseAllocationRepository extends JpaRepository<LicenseAllocation, UUID> {
    List<LicenseAllocation> findBySoftwareLicense_IdOrderByAllocatedDateDesc(UUID softwareLicenseId);
    boolean existsBySoftwareLicense_IdAndEmployee_IdAndStatus(UUID softwareLicenseId, UUID employeeId, LicenseAllocation.AllocationStatus status);
}
