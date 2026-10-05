package com.example.assets_management.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Main KPI summary for the dashboard.
 * Provides high-level asset counts and values.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryDto {

    private Long totalAssets;
    private BigDecimal totalAssetValue;
    private Long assignedAssets;
    private Long availableAssets;
    private Long maintenanceAssets;
    private Long damagedAssets;
    private Long disposedAssets;
}
