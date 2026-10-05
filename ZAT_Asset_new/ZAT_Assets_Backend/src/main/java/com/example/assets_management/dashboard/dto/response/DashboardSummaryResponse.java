package com.example.assets_management.dashboard.dto.response;

import java.math.BigDecimal;
import java.util.List;

/**
 * Summary payload for the dashboard.
 *
 * <p>Aggregates asset counts, value, status/category breakdowns, warranty
 * expiry buckets, and recent allocations to power the frontend dashboard
 * widgets.</p>
 *
 * @param totalAssets        total number of assets
 * @param assignedAssets     assets currently assigned (ALLOCATED)
 * @param availableAssets    assets with status {@code AVAILABLE}
 * @param maintenanceAssets  assets with status {@code IN_REPAIR}
 * @param damagedAssets      assets with status {@code LOST} or {@code CLOSED} (risk bucket)
 * @param totalValue         sum of asset current values
 * @param expiringWarranty30 warranties expiring in the next 30 days
 * @param expiringWarranty60 warranties expiring in the next 60 days
 * @param expiringWarranty90 warranties expiring in the next 90 days
 * @param byStatus           count of assets per status
 * @param byCategory         count of assets per category
 * @param byType             count of assets per asset type
 * @param totalAllocations   total allocation records
 * @param activeAllocations  allocations still {@code ALLOCATED}
 * @param recentAllocations  most recent allocations (id, asset, employee)
 */
public record DashboardSummaryResponse(
        long totalAssets,
        long assignedAssets,
        long availableAssets,
        long maintenanceAssets,
        long damagedAssets,
        BigDecimal totalValue,
        long expiringWarranty30,
        long expiringWarranty60,
        long expiringWarranty90,
        List<NameCount> byStatus,
        List<NameCount> byCategory,
        List<NameCount> byType,
        long totalAllocations,
        long activeAllocations,
        List<RecentAllocation> recentAllocations
) {

    /** A simple name/count pair used for chart breakdowns. */
    public record NameCount(String name, long count) {
    }

    /** A recent allocation summary entry. */
    public record RecentAllocation(
            String assetId,
            String assetCode,
            String assetName,
            String employeeId,
            String employeeName,
            String allocatedOn,
            String status
    ) {
    }
}
