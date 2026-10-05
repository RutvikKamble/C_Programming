package com.example.assets_management.asset_warranty.repository;

import com.example.assets_management.asset_warranty.entity.AssetWarranty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AssetWarrantyRepository extends JpaRepository<AssetWarranty, Long> {

    // An asset can have more than one warranty record over its lifetime
    List<AssetWarranty> findByAsset_Id(UUID assetId);

    // Find warranties with endDate between today and 30 days from today (expiring soon)
    List<AssetWarranty> findByEndDateBetween(LocalDate start, LocalDate end);
}
