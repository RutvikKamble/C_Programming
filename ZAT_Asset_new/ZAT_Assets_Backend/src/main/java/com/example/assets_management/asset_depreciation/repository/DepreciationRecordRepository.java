package com.example.assets_management.asset_depreciation.repository;

import com.example.assets_management.asset_depreciation.entity.DepreciationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DepreciationRecordRepository extends JpaRepository<DepreciationRecord, UUID> {
    List<DepreciationRecord> findByAssetIdOrderByPeriodMonthAsc(UUID assetId);
    Optional<DepreciationRecord> findByAssetIdAndPeriodMonth(UUID assetId, LocalDate periodMonth);
    Optional<DepreciationRecord> findTopByAssetIdOrderByPeriodMonthDesc(UUID assetId);
    boolean existsByAssetIdAndPeriodMonth(UUID assetId, LocalDate periodMonth);

    @Query("select d from DepreciationRecord d order by d.periodMonth desc, d.asset.assetCode asc")
    List<DepreciationRecord> findAllForReport();
}
