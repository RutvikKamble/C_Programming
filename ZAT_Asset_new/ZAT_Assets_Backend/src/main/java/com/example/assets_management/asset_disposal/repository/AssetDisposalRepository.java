package com.example.assets_management.asset_disposal.repository;

import com.example.assets_management.asset_disposal.entity.AssetDisposal;
import com.example.assets_management.asset_disposal.enums.DisposalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssetDisposalRepository extends JpaRepository<AssetDisposal, UUID> {

    List<AssetDisposal> findAllByOrderByCreatedAtDesc();

    List<AssetDisposal> findByStatusOrderByCreatedAtDesc(DisposalStatus status);

    List<AssetDisposal> findByAsset_IdOrderByCreatedAtDesc(UUID assetId);

    Optional<AssetDisposal> findFirstByAsset_IdAndStatusInOrderByCreatedAtDesc(
            UUID assetId,
            Collection<DisposalStatus> statuses
    );

    boolean existsByAsset_IdAndStatusIn(UUID assetId, Collection<DisposalStatus> statuses);
}
