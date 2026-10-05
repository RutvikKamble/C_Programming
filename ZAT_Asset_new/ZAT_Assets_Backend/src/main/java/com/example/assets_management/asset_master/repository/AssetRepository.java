package com.example.assets_management.asset_master.repository;
import com.example.assets_management.asset_master.entity.Asset;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssetRepository extends JpaRepository<Asset, UUID> {

    @Query("SELECT a FROM Asset a WHERE a.deletedAt IS NULL AND a.assetCode = :assetCode")
    Optional<Asset> findByAssetCode(String assetCode);

    @Query("SELECT a FROM Asset a WHERE a.deletedAt IS NULL AND a.qrCodeValue = :qrCodeValue")
    Optional<Asset> findByQrCodeValue(String qrCodeValue);

    @Query("SELECT a FROM Asset a WHERE a.deletedAt IS NULL AND a.barcodeValue = :barcodeValue")
    Optional<Asset> findByBarcodeValue(String barcodeValue);

    @Query("SELECT a FROM Asset a WHERE a.deletedAt IS NULL")
    List<Asset> findAllActive();

    @Query("SELECT a FROM Asset a WHERE a.deletedAt IS NULL AND a.company.id = :companyId")
    List<Asset> findAllActiveByCompanyId(UUID companyId);

    @Query("SELECT a FROM Asset a WHERE a.id = :id AND a.deletedAt IS NULL AND a.company.id = :companyId")
    Optional<Asset> findActiveByIdAndCompany(UUID id, UUID companyId);

    @Query("SELECT a FROM Asset a WHERE a.id = :id AND a.deletedAt IS NULL")
    Optional<Asset> findActiveById(UUID id);

    @Query("SELECT a FROM Asset a WHERE a.deletedAt IS NOT NULL")
    List<Asset> findAllDeleted();

    @Query("SELECT a FROM Asset a WHERE a.id = :id AND a.deletedAt IS NOT NULL")
    Optional<Asset> findDeletedById(UUID id);

    @Modifying
    @Transactional
    @Query("UPDATE Asset a SET a.deletedAt = NULL WHERE a.id = :id")
    void restoreById(UUID id);

}
