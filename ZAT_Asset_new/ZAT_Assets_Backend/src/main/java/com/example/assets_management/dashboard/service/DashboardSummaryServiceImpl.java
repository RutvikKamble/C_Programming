package com.example.assets_management.dashboard.service;

import com.example.assets_management.asset_allocation.entity.Allocation;
import com.example.assets_management.asset_allocation.repository.AllocationRepository;
import com.example.assets_management.asset_allocation.enums.AllocationStatus;
import com.example.assets_management.asset_master.entity.Asset;
import com.example.assets_management.asset_master.repository.AssetRepository;
import com.example.assets_management.asset_master.enums.AssetStatus;
import com.example.assets_management.asset_master.enums.AssetType;
import com.example.assets_management.asset_category.entity.AssetCategory;
import com.example.assets_management.dashboard.dto.response.DashboardSummaryResponse;
import com.example.assets_management.dashboard.dto.response.DashboardSummaryResponse.NameCount;
import com.example.assets_management.dashboard.dto.response.DashboardSummaryResponse.RecentAllocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads aggregated statistics from the asset, category and allocation
 * repositories to build a single dashboard summary payload.
 */
@Service
public class DashboardSummaryServiceImpl implements DashboardSummaryService {

    private static final Logger log = LoggerFactory.getLogger(DashboardSummaryServiceImpl.class);

    private final AssetRepository assetRepository;
    private final AllocationRepository allocationRepository;

    public DashboardSummaryServiceImpl(AssetRepository assetRepository,
                                       AllocationRepository allocationRepository) {
        this.assetRepository = assetRepository;
        this.allocationRepository = allocationRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardSummaryResponse getSummary() {

        List<Asset> assets = assetRepository.findAll();
        List<Allocation> allocations = allocationRepository.findAll();

        long totalAssets = assets.size();
        long assignedAssets = countByStatus(assets, AssetStatus.ASSIGNED);
        long availableAssets = countByStatus(assets, AssetStatus.AVAILABLE);
        long maintenanceAssets = countByStatus(assets, AssetStatus.IN_REPAIR);
        long damagedAssets = countByStatus(assets, AssetStatus.LOST)
                + countByStatus(assets, AssetStatus.CLOSED);

        BigDecimal totalValue = assets.stream()
                .map(Asset::getCurrentValue)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LocalDate today = LocalDate.now();
        long expiringWarranty30 = countWarrantyExpiries(assets, today, 30);
        long expiringWarranty60 = countWarrantyExpiries(assets, today, 60);
        long expiringWarranty90 = countWarrantyExpiries(assets, today, 90);

        List<NameCount> byStatus = groupCounts(assets.stream()
                .map(a -> a.getStatus().name())
                .toList());

        List<NameCount> byCategory = groupCounts(assets.stream()
                .map(Asset::getCategory)
                .filter(java.util.Objects::nonNull)
                .map(AssetCategory::getAssetCategory)
                .toList());

        List<NameCount> byType = groupCounts(assets.stream()
                .map(Asset::getAssetType)
                .filter(java.util.Objects::nonNull)
                .map(AssetType::name)
                .toList());

        long totalAllocations = allocations.size();
        long activeAllocations = allocations.stream()
                .filter(a -> a.getStatus() == AllocationStatus.ALLOCATED)
                .count();

        List<RecentAllocation> recentAllocations = allocations.stream()
                .sorted((a, b) -> {
                    if (a.getAllocatedOn() == null && b.getAllocatedOn() == null) return 0;
                    if (a.getAllocatedOn() == null) return 1;
                    if (b.getAllocatedOn() == null) return -1;
                    return b.getAllocatedOn().compareTo(a.getAllocatedOn());
                })
                .limit(5)
                .map(a -> {
                    Asset asset = a.getAsset();
                    String employeeName = a.getEmployee() != null
                            ? a.getEmployee().getFullName() : null;
                    return new RecentAllocation(
                            asset != null && asset.getId() != null ? asset.getId().toString() : null,
                            asset != null ? asset.getAssetCode() : null,
                            asset != null ? asset.getName() : null,
                            a.getEmployee() != null && a.getEmployee().getId() != null
                                    ? a.getEmployee().getId().toString() : null,
                            employeeName,
                            a.getAllocatedOn() != null ? a.getAllocatedOn().toString() : null,
                            a.getStatus() != null ? a.getStatus().name() : null
                    );
                })
                .toList();

        DashboardSummaryResponse summary = new DashboardSummaryResponse(
                totalAssets,
                assignedAssets,
                availableAssets,
                maintenanceAssets,
                damagedAssets,
                totalValue,
                expiringWarranty30,
                expiringWarranty60,
                expiringWarranty90,
                byStatus,
                byCategory,
                byType,
                totalAllocations,
                activeAllocations,
                recentAllocations
        );

        log.info("Dashboard summary computed: totalAssets={}, totalAllocations={}",
                totalAssets, totalAllocations);
        return summary;
    }

    private long countByStatus(List<Asset> assets, AssetStatus status) {
        return assets.stream().filter(a -> a.getStatus() == status).count();
    }

    private long countWarrantyExpiries(List<Asset> assets, LocalDate today, long windowDays) {
        return assets.stream()
                .map(Asset::getWarrantyExpiry)
                .filter(java.util.Objects::nonNull)
                .filter(w -> w.isAfter(today) || w.isEqual(today))
                .filter(w -> ChronoUnit.DAYS.between(today, w) <= windowDays)
                .count();
    }

    private List<NameCount> groupCounts(List<String> values) {
        Map<String, Long> counts = new LinkedHashMap<>();
        for (String v : values) {
            counts.merge(v, 1L, Long::sum);
        }
        List<NameCount> result = new ArrayList<>();
        counts.forEach((k, v) -> result.add(new NameCount(k, v)));
        return result;
    }
}
