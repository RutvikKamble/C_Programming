package com.example.assets_management.dashboard.service;

import com.example.assets_management.dashboard.dto.*;
import com.example.assets_management.dashboard.repository.DashboardRepositoryImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of DashboardService.
 * Aggregates data from Phase I and Phase II modules into a single admin dashboard view.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardServiceImpl implements DashboardService {

    private final DashboardRepositoryImpl dashboardRepository;

    @Override
    public AdminDashboardResponse getAdminDashboard() {
        log.info("Fetching admin dashboard data");

        try {
            // Build the complete dashboard response
            return AdminDashboardResponse.builder()
                    // Main KPI Summary
                    .summary(getDashboardSummary())
                    
                    // Items requiring Admin attention
                    .attentionRequired(getAttentionRequired())
                    
                    // Asset Status Distribution
                    .assetStatus(getAssetStatusDistribution())
                    
                    // Asset Distribution by Category
                    .categoryDistribution(getCategoryDistribution())
                    
                    // Asset Distribution by Branch
                    .branchDistribution(getBranchDistribution())
                    
                    // Asset Distribution by Department
                    .departmentDistribution(getDepartmentDistribution())
                    
                    // Allocation Summary
                    .allocation(getAllocationSummary())
                    
                    // Maintenance Summary
                    .maintenance(getMaintenanceSummary())
                    
                    // Maintenance Status Distribution
                    .maintenanceStatus(getMaintenanceStatusDistribution())
                    
                    // Upcoming/Overdue Maintenance
                    .upcomingMaintenance(getUpcomingMaintenance())
                    
                    // Warranty Summary
                    .warranty(getWarrantySummary())
                    
                    // Warranty Alerts
                    .warrantyAlerts(getWarrantyAlerts())
                    
                    // Request Summary
                    .requests(getRequestSummary())
                    
                    // Pending Requests
                    .pendingRequests(getPendingRequests())
                    
                    // Audit Summary
                    .audit(getAuditSummary())
                    
                    // Active Audit (if any)
                    .activeAudit(getActiveAudit())
                    
                    // Recent Assets
                    .recentAssets(getRecentAssets())
                    
                    // Recent Activity
                    .recentActivity(getRecentActivity())
                    .build();

        } catch (Exception e) {
            log.error("Error fetching admin dashboard data", e);
            // Return empty response rather than failing
            return createEmptyResponse();
        }
    }

    private DashboardSummaryDto getDashboardSummary() {
        try {
            DashboardSummaryDto summary = dashboardRepository.getAssetSummary();
            if (summary == null) {
                return createEmptySummary();
            }
            return summary;
        } catch (Exception e) {
            log.error("Error fetching dashboard summary", e);
            return createEmptySummary();
        }
    }

    private AttentionRequiredDto getAttentionRequired() {
        try {
            AttentionRequiredDto attention = dashboardRepository.getAttentionRequired();
            if (attention == null) {
                return createEmptyAttentionRequired();
            }
            return attention;
        } catch (Exception e) {
            log.error("Error fetching attention required data", e);
            return createEmptyAttentionRequired();
        }
    }

    private List<AssetStatusDto> getAssetStatusDistribution() {
        try {
            List<AssetStatusDto> distribution = dashboardRepository.getAssetStatusDistribution();
            return distribution != null ? distribution : List.of();
        } catch (Exception e) {
            log.error("Error fetching asset status distribution", e);
            return List.of();
        }
    }

    private List<CategoryDistributionDto> getCategoryDistribution() {
        try {
            List<CategoryDistributionDto> distribution = dashboardRepository.getCategoryDistribution();
            return distribution != null ? distribution : List.of();
        } catch (Exception e) {
            log.error("Error fetching category distribution", e);
            return List.of();
        }
    }

    private List<BranchDistributionDto> getBranchDistribution() {
        try {
            List<BranchDistributionDto> distribution = dashboardRepository.getBranchDistribution();
            return distribution != null ? distribution : List.of();
        } catch (Exception e) {
            log.error("Error fetching branch distribution", e);
            return List.of();
        }
    }

    private List<DepartmentDistributionDto> getDepartmentDistribution() {
        try {
            List<DepartmentDistributionDto> distribution = dashboardRepository.getDepartmentDistribution();
            return distribution != null ? distribution : List.of();
        } catch (Exception e) {
            log.error("Error fetching department distribution", e);
            return List.of();
        }
    }

    private AllocationSummaryDto getAllocationSummary() {
        try {
            AllocationSummaryDto summary = dashboardRepository.getAllocationSummary();
            if (summary == null) {
                return createEmptyAllocationSummary();
            }
            return summary;
        } catch (Exception e) {
            log.error("Error fetching allocation summary", e);
            return createEmptyAllocationSummary();
        }
    }

    private MaintenanceSummaryDto getMaintenanceSummary() {
        try {
            MaintenanceSummaryDto summary = dashboardRepository.getMaintenanceSummary();
            if (summary == null) {
                return createEmptyMaintenanceSummary();
            }
            return summary;
        } catch (Exception e) {
            log.error("Error fetching maintenance summary", e);
            return createEmptyMaintenanceSummary();
        }
    }

    private List<MaintenanceStatusDto> getMaintenanceStatusDistribution() {
        try {
            List<MaintenanceStatusDto> distribution = dashboardRepository.getMaintenanceStatusDistribution();
            return distribution != null ? distribution : List.of();
        } catch (Exception e) {
            log.error("Error fetching maintenance status distribution", e);
            return List.of();
        }
    }

    private List<PreventiveMaintenanceDto> getUpcomingMaintenance() {
        try {
            List<PreventiveMaintenanceDto> maintenance = dashboardRepository.getUpcomingMaintenance();
            return maintenance != null ? maintenance : List.of();
        } catch (Exception e) {
            log.error("Error fetching upcoming maintenance", e);
            return List.of();
        }
    }

    private WarrantySummaryDto getWarrantySummary() {
        try {
            WarrantySummaryDto summary = dashboardRepository.getWarrantySummary();
            if (summary == null) {
                return createEmptyWarrantySummary();
            }
            return summary;
        } catch (Exception e) {
            log.error("Error fetching warranty summary", e);
            return createEmptyWarrantySummary();
        }
    }

    private List<WarrantyAlertDto> getWarrantyAlerts() {
        try {
            List<WarrantyAlertDto> alerts = dashboardRepository.getWarrantyAlerts();
            return alerts != null ? alerts : List.of();
        } catch (Exception e) {
            log.error("Error fetching warranty alerts", e);
            return List.of();
        }
    }

    private RequestSummaryDto getRequestSummary() {
        try {
            RequestSummaryDto summary = dashboardRepository.getRequestSummary();
            if (summary == null) {
                return createEmptyRequestSummary();
            }
            return summary;
        } catch (Exception e) {
            log.error("Error fetching request summary", e);
            return createEmptyRequestSummary();
        }
    }

    private List<PendingRequestDto> getPendingRequests() {
        try {
            List<PendingRequestDto> requests = dashboardRepository.getPendingRequests();
            return requests != null ? requests : List.of();
        } catch (Exception e) {
            log.error("Error fetching pending requests", e);
            return List.of();
        }
    }

    private AuditSummaryDto getAuditSummary() {
        try {
            AuditSummaryDto summary = dashboardRepository.getAuditSummary();
            if (summary == null) {
                return createEmptyAuditSummary();
            }
            return summary;
        } catch (Exception e) {
            log.error("Error fetching audit summary", e);
            return createEmptyAuditSummary();
        }
    }

    private ActiveAuditDto getActiveAudit() {
        try {
            ActiveAuditDto activeAudit = dashboardRepository.getActiveAudit();
            // Return null if no active audit exists
            return activeAudit;
        } catch (Exception e) {
            log.error("Error fetching active audit", e);
            return null;
        }
    }

    private List<RecentAssetDto> getRecentAssets() {
        try {
            List<RecentAssetDto> assets = dashboardRepository.getRecentAssets();
            return assets != null ? assets : List.of();
        } catch (Exception e) {
            log.error("Error fetching recent assets", e);
            return List.of();
        }
    }

    private List<RecentActivityDto> getRecentActivity() {
        try {
            List<RecentActivityDto> activity = dashboardRepository.getRecentActivity();
            return activity != null ? activity : List.of();
        } catch (Exception e) {
            log.error("Error fetching recent activity", e);
            return List.of();
        }
    }

    // ==================== EMPTY DATA HELPERS ====================

    private AdminDashboardResponse createEmptyResponse() {
        return AdminDashboardResponse.builder()
                .summary(createEmptySummary())
                .attentionRequired(createEmptyAttentionRequired())
                .assetStatus(List.of())
                .categoryDistribution(List.of())
                .branchDistribution(List.of())
                .departmentDistribution(List.of())
                .allocation(createEmptyAllocationSummary())
                .maintenance(createEmptyMaintenanceSummary())
                .maintenanceStatus(List.of())
                .upcomingMaintenance(List.of())
                .warranty(createEmptyWarrantySummary())
                .warrantyAlerts(List.of())
                .requests(createEmptyRequestSummary())
                .pendingRequests(List.of())
                .audit(createEmptyAuditSummary())
                .activeAudit(null)
                .recentAssets(List.of())
                .recentActivity(List.of())
                .build();
    }

    private DashboardSummaryDto createEmptySummary() {
        return DashboardSummaryDto.builder()
                .totalAssets(0L)
                .totalAssetValue(java.math.BigDecimal.ZERO)
                .assignedAssets(0L)
                .availableAssets(0L)
                .maintenanceAssets(0L)
                .damagedAssets(0L)
                .disposedAssets(0L)
                .build();
    }

    private AttentionRequiredDto createEmptyAttentionRequired() {
        return AttentionRequiredDto.builder()
                .maintenanceDue(0L)
                .overdueMaintenance(0L)
                .warrantyExpiring(0L)
                .pendingRequests(0L)
                .auditDiscrepancies(0L)
                .damagedAssets(0L)
                .build();
    }

    private AllocationSummaryDto createEmptyAllocationSummary() {
        return AllocationSummaryDto.builder()
                .totalAllocations(0L)
                .activeAllocations(0L)
                .returnedAllocations(0L)
                .goodReturns(0L)
                .damagedReturns(0L)
                .lostReturns(0L)
                .build();
    }

    private MaintenanceSummaryDto createEmptyMaintenanceSummary() {
        return MaintenanceSummaryDto.builder()
                .totalWorkOrders(0L)
                .openWorkOrders(0L)
                .created(0L)
                .assigned(0L)
                .inProgress(0L)
                .completed(0L)
                .closed(0L)
                .totalMaintenanceCost(java.math.BigDecimal.ZERO)
                .build();
    }

    private WarrantySummaryDto createEmptyWarrantySummary() {
        return WarrantySummaryDto.builder()
                .expired(0L)
                .expiring30Days(0L)
                .expiring60Days(0L)
                .expiring90Days(0L)
                .build();
    }

    private RequestSummaryDto createEmptyRequestSummary() {
        return RequestSummaryDto.builder()
                .pendingDeptHead(0L)
                .pendingAssetManager(0L)
                .approved(0L)
                .rejected(0L)
                .fulfilled(0L)
                .totalPending(0L)
                .build();
    }

    private AuditSummaryDto createEmptyAuditSummary() {
        return AuditSummaryDto.builder()
                .totalAudits(0L)
                .inProgress(0L)
                .completed(0L)
                .totalExpectedAssets(0L)
                .totalFoundAssets(0L)
                .totalMissingAssets(0L)
                .totalDiscrepancies(0L)
                .build();
    }
}
