package com.example.assets_management.asset_documents.repository;

import com.example.assets_management.asset_documents.entity.AssetDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AssetDocumentRepository extends JpaRepository<AssetDocument, Long> {

    List<AssetDocument> findByAsset_Id(UUID assetId);
}
