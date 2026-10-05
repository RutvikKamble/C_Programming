package com.example.assets_management.asset_requests.repository;

import com.example.assets_management.asset_requests.entity.AssetRequest;
import com.example.assets_management.asset_requests.enums.AssetRequestStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AssetRequestRepository extends JpaRepository<AssetRequest, Long> {
    @Query("select distinct r from AssetRequest r left join r.assets a where r.asset.id = :assetId or a.id = :assetId")
    List<AssetRequest> findByAnyAssetId(@Param("assetId") UUID assetId);
    List<AssetRequest> findByStatus(AssetRequestStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from AssetRequest r where r.id = :id")
    java.util.Optional<AssetRequest> findByIdForUpdate(@Param("id") Long id);
}

/*
package com.example.assets_management.asset_requests.repository;

import com.example.assets_management.asset_requests.entity.AssetRequest;
import com.example.assets_management.asset_requests.enums.AssetRequestStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AssetRequestRepository extends JpaRepository<AssetRequest, Long> {
    List<AssetRequest> findByAsset_Id(UUID assetId);
    List<AssetRequest> findByStatus(AssetRequestStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from AssetRequest r where r.id = :id")
    java.util.Optional<AssetRequest> findByIdForUpdate(@Param("id") Long id);
}
*/