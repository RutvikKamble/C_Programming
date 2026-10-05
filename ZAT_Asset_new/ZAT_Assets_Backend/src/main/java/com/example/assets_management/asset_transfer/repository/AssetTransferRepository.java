package com.example.assets_management.asset_transfer.repository;

import com.example.assets_management.asset_transfer.entity.AssetTransfer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AssetTransferRepository extends JpaRepository<AssetTransfer, UUID> {

    List<AssetTransfer> findByAssetIdOrderByTransferDateDescCreatedAtDesc(UUID assetId);
}
