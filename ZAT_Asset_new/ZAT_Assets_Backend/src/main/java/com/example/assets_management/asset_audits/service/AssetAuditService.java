package com.example.assets_management.asset_audits.service;

import com.example.assets_management.asset_audits.entity.AssetAudit;

import java.util.List;
import java.util.UUID;

public interface AssetAuditService {

    AssetAudit createAudit(AssetAudit audit);

    AssetAudit updateAudit(Long id, AssetAudit audit);

    void deleteAudit(Long id);

    AssetAudit getAuditById(Long id);

    List<AssetAudit> getAllAudits();

    List<AssetAudit> getAuditsByAssetId(UUID assetId);
}
