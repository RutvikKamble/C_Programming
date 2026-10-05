package com.example.assets_management.asset_audits.repository;

import com.example.assets_management.asset_audits.entity.AssetAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AssetAuditRepository extends JpaRepository<AssetAudit, Long> {

    List<AssetAudit> findByAsset_Id(UUID assetId);
}
