package com.example.assets_management.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Main response DTO for the Admin Dashboard API.
 * Combines Phase I and Phase II data into a single consolidated view.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardResponse {

    // Main KPI Summary
    private DashboardSummaryDto summary;

    // Items requiring Admin attention
    private AttentionRequiredDto attentionRequired;

    // Asset Status Distribution
    private List<AssetStatusDto> assetStatus;

    // Asset Distribution by Category
    private List<CategoryDistributionDto> categoryDistribution;

    // Asset Distribution by Branch
    private List<BranchDistributionDto> branchDistribution;

    // Asset Distribution by Department
    private List<DepartmentDistributionDto> departmentDistribution;

    // Allocation Summary
    private AllocationSummaryDto allocation;

    // Maintenance Summary
    private MaintenanceSummaryDto maintenance;

    // Maintenance Status Distribution
    private List<MaintenanceStatusDto> maintenanceStatus;

    // Upcoming/Overdue Maintenance
    private List<PreventiveMaintenanceDto> upcomingMaintenance;

    // Warranty Summary
    private WarrantySummaryDto warranty;

    // Warranty Alerts
    private List<WarrantyAlertDto> warrantyAlerts;

    // Request Summary
    private RequestSummaryDto requests;

    // Pending Requests
    private List<PendingRequestDto> pendingRequests;

    // Audit Summary
    private AuditSummaryDto audit;

    // Active Audit (if any)
    private ActiveAuditDto activeAudit;

    // Recent Assets
    private List<RecentAssetDto> recentAssets;

    // Recent Activity
    private List<RecentActivityDto> recentActivity;
}
