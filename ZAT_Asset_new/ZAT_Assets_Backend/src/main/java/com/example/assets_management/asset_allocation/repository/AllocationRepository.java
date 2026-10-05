package com.example.assets_management.asset_allocation.repository;
import com.example.assets_management.asset_allocation.entity.Allocation;

import com.example.assets_management.asset_allocation.enums.AllocationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AllocationRepository extends JpaRepository<Allocation, UUID> {

    Optional<Allocation> findByAssetIdAndStatus(
            UUID assetId,
            AllocationStatus status
    );

    List<Allocation> findByAssetIdOrderByAllocatedOnDesc(UUID assetId);

    List<Allocation> findByEmployeeId(UUID employeeId);

    List<Allocation> findByAsset_Category_Id(UUID categoryId);

}