package com.example.assets_management.dashboard.repository;

import com.example.assets_management.dashboard.dto.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of DashboardRepository with manual query execution.
 * This allows us to execute native queries and map results to DTOs manually.
 */
@Repository
public class DashboardRepositoryImpl {

    @PersistenceContext
    private EntityManager entityManager;

    // ==================== ASSET SUMMARY ====================

    public DashboardSummaryDto getAssetSummary() {
        String query = """
            SELECT COUNT(*) as totalAssets,
                   COALESCE(SUM(CASE WHEN current_value IS NOT NULL THEN current_value ELSE 0 END), 0) as totalAssetValue,
                   SUM(CASE WHEN status = 'ASSIGNED' THEN 1 ELSE 0 END) as assignedAssets,
                   SUM(CASE WHEN status = 'AVAILABLE' THEN 1 ELSE 0 END) as availableAssets,
                   SUM(CASE WHEN status = 'IN_REPAIR' THEN 1 ELSE 0 END) as maintenanceAssets,
                   SUM(CASE WHEN status = 'LOST' THEN 1 ELSE 0 END) as damagedAssets,
                   SUM(CASE WHEN status = 'CLOSED' THEN 1 ELSE 0 END) as disposedAssets
            FROM assets
            WHERE deleted_at IS NULL
            """;

        jakarta.persistence.Query nativeQuery = entityManager.createNativeQuery(query);
        Object[] result = (Object[]) nativeQuery.getSingleResult();

        return DashboardSummaryDto.builder()
                .totalAssets(((Number) result[0]).longValue())
                .totalAssetValue((BigDecimal) result[1])
                .assignedAssets(((Number) result[2]).longValue())
                .availableAssets(((Number) result[3]).longValue())
                .maintenanceAssets(((Number) result[4]).longValue())
                .damagedAssets(((Number) result[5]).longValue())
                .disposedAssets(((Number) result[6]).longValue())
                .build();
    }

    // ==================== ASSET STATUS DISTRIBUTION ====================

    public List<AssetStatusDto> getAssetStatusDistribution() {
        String query = """
            SELECT status as status,
                   COUNT(*) as count,
                   (COUNT(*) * 100.0 / (SELECT COUNT(*) FROM assets WHERE deleted_at IS NULL)) as percentage
            FROM assets
            WHERE deleted_at IS NULL
            GROUP BY status
            """;

        jakarta.persistence.Query nativeQuery = entityManager.createNativeQuery(query);
        List<Object[]> results = nativeQuery.getResultList();

        return results.stream().map(result -> AssetStatusDto.builder()
                .status((String) result[0])
                .count(((Number) result[1]).longValue())
                .percentage(((Number) result[2]).doubleValue())
                .build()).toList();
    }

    // ==================== CATEGORY DISTRIBUTION ====================

    public List<CategoryDistributionDto> getCategoryDistribution() {
        String query = """
            SELECT c.id as categoryId,
                   c.asset_category as categoryName,
                   COUNT(a.id) as assetCount,
                   COALESCE(SUM(CASE WHEN a.current_value IS NOT NULL THEN a.current_value ELSE 0 END), 0) as assetValue
            FROM asset_categories c
            LEFT JOIN assets a ON c.id = a.category_id AND a.deleted_at IS NULL
            GROUP BY c.id, c.asset_category
            ORDER BY assetCount DESC
            """;

        jakarta.persistence.Query nativeQuery = entityManager.createNativeQuery(query);
        List<Object[]> results = nativeQuery.getResultList();

        return results.stream().map(result -> CategoryDistributionDto.builder()
                .categoryId(result[0] != null ? UUID.fromString(result[0].toString()) : null)
                .categoryName((String) result[1])
                .assetCount(((Number) result[2]).longValue())
                .assetValue((BigDecimal) result[3])
                .build()).toList();
    }

    // ==================== BRANCH DISTRIBUTION ====================

    public List<BranchDistributionDto> getBranchDistribution() {
        String query = """
            SELECT b.id as branchId,
                   b.branch_name as branchName,
                   COUNT(a.id) as assetCount,
                   COALESCE(SUM(CASE WHEN a.current_value IS NOT NULL THEN a.current_value ELSE 0 END), 0) as assetValue
            FROM branches b
            LEFT JOIN assets a ON b.id = a.branch_id AND a.deleted_at IS NULL
            GROUP BY b.id, b.branch_name
            ORDER BY assetCount DESC
            """;

        jakarta.persistence.Query nativeQuery = entityManager.createNativeQuery(query);
        List<Object[]> results = nativeQuery.getResultList();

        return results.stream().map(result -> BranchDistributionDto.builder()
                .branchId(result[0] != null ? UUID.fromString(result[0].toString()) : null)
                .branchName((String) result[1])
                .assetCount(((Number) result[2]).longValue())
                .assetValue((BigDecimal) result[3])
                .build()).toList();
    }

    // ==================== DEPARTMENT DISTRIBUTION ====================

    public List<DepartmentDistributionDto> getDepartmentDistribution() {
        String query = """
            SELECT d.id as departmentId,
                   d.department_name as departmentName,
                   COUNT(a.id) as assetCount,
                   COALESCE(SUM(CASE WHEN a.current_value IS NOT NULL THEN a.current_value ELSE 0 END), 0) as assetValue,
                   SUM(CASE WHEN a.status = 'ASSIGNED' THEN 1 ELSE 0 END) as assignedAssetCount
            FROM departments d
            LEFT JOIN assets a ON d.id = a.department_id AND a.deleted_at IS NULL
            GROUP BY d.id, d.department_name
            ORDER BY assetCount DESC
            """;

        jakarta.persistence.Query nativeQuery = entityManager.createNativeQuery(query);
        List<Object[]> results = nativeQuery.getResultList();

        return results.stream().map(result -> DepartmentDistributionDto.builder()
                .departmentId(result[0] != null ? UUID.fromString(result[0].toString()) : null)
                .departmentName((String) result[1])
                .assetCount(((Number) result[2]).longValue())
                .assetValue((BigDecimal) result[3])
                .assignedAssetCount(((Number) result[4]).longValue())
                .build()).toList();
    }

    // ==================== ALLOCATION SUMMARY ====================

    public AllocationSummaryDto getAllocationSummary() {
        String query = """
            SELECT COUNT(*) as totalAllocations,
                   SUM(CASE WHEN returned_on IS NULL THEN 1 ELSE 0 END) as activeAllocations,
                   SUM(CASE WHEN returned_on IS NOT NULL THEN 1 ELSE 0 END) as returnedAllocations,
                   0 as goodReturns,
                   0 as damagedReturns,
                   0 as lostReturns
            FROM asset_allocations
            """;

        jakarta.persistence.Query nativeQuery = entityManager.createNativeQuery(query);
        Object[] result = (Object[]) nativeQuery.getSingleResult();

        return AllocationSummaryDto.builder()
                .totalAllocations(((Number) result[0]).longValue())
                .activeAllocations(((Number) result[1]).longValue())
                .returnedAllocations(((Number) result[2]).longValue())
                .goodReturns(((Number) result[3]).longValue())
                .damagedReturns(((Number) result[4]).longValue())
                .lostReturns(((Number) result[5]).longValue())
                .build();
    }

    // ==================== MAINTENANCE SUMMARY ====================

    public MaintenanceSummaryDto getMaintenanceSummary() {
        String query = """
            SELECT COUNT(*) as totalWorkOrders,
                   SUM(CASE WHEN status IN ('CREATED', 'ASSIGNED', 'IN_PROGRESS') THEN 1 ELSE 0 END) as openWorkOrders,
                   SUM(CASE WHEN status = 'CREATED' THEN 1 ELSE 0 END) as created,
                   SUM(CASE WHEN status = 'ASSIGNED' THEN 1 ELSE 0 END) as assigned,
                   SUM(CASE WHEN status = 'IN_PROGRESS' THEN 1 ELSE 0 END) as inProgress,
                   SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) as completed,
                   SUM(CASE WHEN status = 'CLOSED' THEN 1 ELSE 0 END) as closed,
                   COALESCE(SUM(CASE WHEN cost IS NOT NULL THEN cost ELSE 0 END), 0) as totalMaintenanceCost
            FROM asset_maintenance
            """;

        jakarta.persistence.Query nativeQuery = entityManager.createNativeQuery(query);
        Object[] result = (Object[]) nativeQuery.getSingleResult();

        return MaintenanceSummaryDto.builder()
                .totalWorkOrders(((Number) result[0]).longValue())
                .openWorkOrders(((Number) result[1]).longValue())
                .created(((Number) result[2]).longValue())
                .assigned(((Number) result[3]).longValue())
                .inProgress(((Number) result[4]).longValue())
                .completed(((Number) result[5]).longValue())
                .closed(((Number) result[6]).longValue())
                .totalMaintenanceCost((BigDecimal) result[7])
                .build();
    }

    // ==================== MAINTENANCE STATUS DISTRIBUTION ====================

    public List<MaintenanceStatusDto> getMaintenanceStatusDistribution() {
        String query = """
            SELECT status as status, COUNT(*) as count
            FROM asset_maintenance
            GROUP BY status
            """;

        jakarta.persistence.Query nativeQuery = entityManager.createNativeQuery(query);
        List<Object[]> results = nativeQuery.getResultList();

        return results.stream().map(result -> MaintenanceStatusDto.builder()
                .status((String) result[0])
                .count(((Number) result[1]).longValue())
                .build()).toList();
    }

    // ==================== UPCOMING/OVERDUE MAINTENANCE ====================

    public List<PreventiveMaintenanceDto> getUpcomingMaintenance() {
        String query = """
            SELECT a.id as assetId,
                   a.asset_code as assetCode,
                   a.name as assetName,
                   am.next_due_date as nextDueDate,
                   DATEDIFF(am.next_due_date, CURRENT_DATE) as daysRemaining,
                   CASE
                       WHEN am.next_due_date < CURRENT_DATE THEN 'OVERDUE'
                       WHEN am.next_due_date = CURRENT_DATE THEN 'DUE_TODAY'
                       ELSE 'UPCOMING'
                   END as status
            FROM asset_maintenance am
            JOIN assets a ON am.asset_id = a.id
            WHERE am.next_due_date IS NOT NULL
            ORDER BY am.next_due_date ASC
            LIMIT 10
            """;

        jakarta.persistence.Query nativeQuery = entityManager.createNativeQuery(query);
        List<Object[]> results = nativeQuery.getResultList();

        return results.stream().map(result -> PreventiveMaintenanceDto.builder()
                .assetId(UUID.fromString(result[0].toString()))
                .assetCode((String) result[1])
                .assetName((String) result[2])
                .nextDueDate(result[3] != null ? ((java.sql.Date) result[3]).toLocalDate() : null)
                .daysRemaining(((Number) result[4]).longValue())
                .status((String) result[5])
                .build()).toList();
    }

    // ==================== WARRANTY SUMMARY ====================

    public WarrantySummaryDto getWarrantySummary() {
        String query = """
            SELECT SUM(CASE WHEN end_date < CURRENT_DATE THEN 1 ELSE 0 END) as expired,
                   SUM(CASE WHEN end_date >= CURRENT_DATE AND end_date <= DATE_ADD(CURRENT_DATE, INTERVAL 30 DAY) THEN 1 ELSE 0 END) as expiring30Days,
                   SUM(CASE WHEN end_date > DATE_ADD(CURRENT_DATE, INTERVAL 30 DAY) AND end_date <= DATE_ADD(CURRENT_DATE, INTERVAL 60 DAY) THEN 1 ELSE 0 END) as expiring60Days,
                   SUM(CASE WHEN end_date > DATE_ADD(CURRENT_DATE, INTERVAL 60 DAY) AND end_date <= DATE_ADD(CURRENT_DATE, INTERVAL 90 DAY) THEN 1 ELSE 0 END) as expiring90Days
            FROM asset_warranty
            """;

        jakarta.persistence.Query nativeQuery = entityManager.createNativeQuery(query);
        Object[] result = (Object[]) nativeQuery.getSingleResult();

        return WarrantySummaryDto.builder()
                .expired(result[0] != null ? ((Number) result[0]).longValue() : 0L)
                .expiring30Days(result[1] != null ? ((Number) result[1]).longValue() : 0L)
                .expiring60Days(result[2] != null ? ((Number) result[2]).longValue() : 0L)
                .expiring90Days(result[3] != null ? ((Number) result[3]).longValue() : 0L)
                .build();
    }

    // ==================== WARRANTY ALERTS ====================

    public List<WarrantyAlertDto> getWarrantyAlerts() {
        String query = """
            SELECT a.id as assetId,
                   a.asset_code as assetCode,
                   a.name as assetName,
                   aw.end_date as warrantyEndDate,
                   aw.provider as serviceProvider,
                   DATEDIFF(aw.end_date, CURRENT_DATE) as daysRemaining,
                   CASE
                       WHEN aw.end_date < CURRENT_DATE THEN 'EXPIRED'
                       WHEN aw.end_date <= DATE_ADD(CURRENT_DATE, INTERVAL 30 DAY) THEN 'EXPIRING_SOON'
                       ELSE 'ACTIVE'
                   END as status
            FROM asset_warranty aw
            JOIN assets a ON aw.asset_id = a.id
            WHERE aw.end_date <= DATE_ADD(CURRENT_DATE, INTERVAL 90 DAY)
            ORDER BY aw.end_date ASC
            LIMIT 10
            """;

        jakarta.persistence.Query nativeQuery = entityManager.createNativeQuery(query);
        List<Object[]> results = nativeQuery.getResultList();

        return results.stream().map(result -> WarrantyAlertDto.builder()
                .assetId(UUID.fromString(result[0].toString()))
                .assetCode((String) result[1])
                .assetName((String) result[2])
                .warrantyEndDate(result[3] != null ? ((java.sql.Date) result[3]).toLocalDate() : null)
                .serviceProvider((String) result[4])
                .daysRemaining(((Number) result[5]).longValue())
                .status((String) result[6])
                .build()).toList();
    }

    // ==================== REQUEST SUMMARY ====================

    public RequestSummaryDto getRequestSummary() {
        String query = """
            SELECT SUM(CASE WHEN status = 'SEND' THEN 1 ELSE 0 END) as pendingDeptHead,
                   SUM(CASE WHEN status = 'SEND' THEN 1 ELSE 0 END) as pendingAssetManager,
                   SUM(CASE WHEN status = 'ACCEPT' THEN 1 ELSE 0 END) as approved,
                   SUM(CASE WHEN status = 'REJECT' THEN 1 ELSE 0 END) as rejected,
                   0 as fulfilled,
                   SUM(CASE WHEN status = 'SEND' THEN 1 ELSE 0 END) as totalPending
            FROM asset_request
            """;

        jakarta.persistence.Query nativeQuery = entityManager.createNativeQuery(query);
        Object[] result = (Object[]) nativeQuery.getSingleResult();

        return RequestSummaryDto.builder()
                .pendingDeptHead(((Number) result[0]).longValue())
                .pendingAssetManager(((Number) result[1]).longValue())
                .approved(((Number) result[2]).longValue())
                .rejected(((Number) result[3]).longValue())
                .fulfilled(((Number) result[4]).longValue())
                .totalPending(((Number) result[5]).longValue())
                .build();
    }

    // ==================== PENDING REQUESTS ====================

    public List<PendingRequestDto> getPendingRequests() {
        String query = """
            SELECT ar.id as requestId,
                   a.id as assetId,
                   a.asset_code as assetCode,
                   a.name as assetName,
                   ar.request_type as requestType,
                   ar.employee_name as employeeName,
                   ar.department as department,
                   ar.request_date as requestDate,
                   ar.status as status,
                   ar.priority as priority
            FROM asset_request ar
            LEFT JOIN assets a ON ar.asset_id = a.id
            WHERE ar.status = 'SEND'
            ORDER BY ar.request_date DESC
            LIMIT 10
            """;

        jakarta.persistence.Query nativeQuery = entityManager.createNativeQuery(query);
        List<Object[]> results = nativeQuery.getResultList();

        return results.stream().map(result -> PendingRequestDto.builder()
                .requestId(((Number) result[0]).longValue())
                .assetId(result[1] != null ? UUID.fromString(result[1].toString()) : null)
                .assetCode((String) result[2])
                .assetName((String) result[3])
                .requestType((String) result[4])
                .employeeName((String) result[5])
                .department((String) result[6])
                .requestDate(result[7] != null ? ((java.sql.Date) result[7]).toLocalDate() : null)
                .status((String) result[8])
                .priority((String) result[9])
                .build()).toList();
    }

    // ==================== AUDIT SUMMARY ====================

    public AuditSummaryDto getAuditSummary() {
        String query = """
            SELECT COUNT(*) as totalAudits,
                   COALESCE(SUM(CASE WHEN ar.status = 'IN_PROGRESS' THEN 1 ELSE 0 END), 0),
                   COALESCE(SUM(CASE WHEN ar.status IN ('COMPLETED', 'COMPLETED_WITH_EXCEPTIONS') THEN 1 ELSE 0 END), 0),
                   COALESCE(SUM(ar.total_expected_assets), 0),
                   COALESCE(SUM(ar.total_verified_assets), 0),
                   COALESCE(SUM(ar.total_missing_assets), 0),
                   COALESCE(SUM(ar.total_discrepancies), 0)
            FROM audit_run ar
            """;
        Object[] result = (Object[]) entityManager.createNativeQuery(query).getSingleResult();
        return AuditSummaryDto.builder()
                .totalAudits(((Number) result[0]).longValue())
                .inProgress(((Number) result[1]).longValue())
                .completed(((Number) result[2]).longValue())
                .totalExpectedAssets(((Number) result[3]).longValue())
                .totalFoundAssets(((Number) result[4]).longValue())
                .totalMissingAssets(((Number) result[5]).longValue())
                .totalDiscrepancies(((Number) result[6]).longValue())
                .build();
    }

    // ==================== ACTIVE AUDIT ====================

    public ActiveAuditDto getActiveAudit() {
        String query = """
            SELECT ar.id, b.id, b.branch_name, ar.audit_date,
                   ar.total_expected_assets,
                   (ar.total_expected_assets - COALESCE((
                       SELECT COUNT(*) FROM audit_scan s
                       WHERE s.audit_run_id = ar.id AND s.result = 'PENDING'
                   ), 0)),
                   (ar.total_expected_assets - COALESCE((
                       SELECT COUNT(*) FROM audit_scan s
                       WHERE s.audit_run_id = ar.id AND s.result = 'PENDING'
                   ), 0)),
                   ar.total_missing_assets,
                   CASE WHEN ar.total_expected_assets = 0 THEN 0.0
                        ELSE ROUND(((ar.total_expected_assets - COALESCE((
                            SELECT COUNT(*) FROM audit_scan s
                            WHERE s.audit_run_id = ar.id AND s.result = 'PENDING'
                        ), 0)) * 100.0) / ar.total_expected_assets, 2)
                   END
            FROM audit_run ar
            LEFT JOIN branches b ON ar.branch_id = b.id
            WHERE ar.status = 'IN_PROGRESS'
            ORDER BY ar.started_at DESC
            LIMIT 1
            """;
        List<Object[]> results = entityManager.createNativeQuery(query).getResultList();
        if (results.isEmpty()) return null;
        Object[] result = results.get(0);
        return ActiveAuditDto.builder()
                .auditId(((Number) result[0]).longValue())
                .branchId(result[1] != null ? UUID.fromString(result[1].toString()) : null)
                .branchName((String) result[2])
                .auditDate(result[3] != null ? ((java.sql.Date) result[3]).toLocalDate() : null)
                .systemCount(result[4] != null ? ((Number) result[4]).longValue() : 0L)
                .physicalCount(result[5] != null ? ((Number) result[5]).longValue() : 0L)
                .scannedCount(result[6] != null ? ((Number) result[6]).longValue() : 0L)
                .missingCount(result[7] != null ? ((Number) result[7]).longValue() : 0L)
                .progressPercentage(result[8] != null ? ((Number) result[8]).doubleValue() : 0.0)
                .build();
    }

    // ==================== RECENT ASSETS ====================

    public List<RecentAssetDto> getRecentAssets() {
        String query = """
            SELECT a.id as assetId,
                   a.asset_code as assetCode,
                   a.name as assetName,
                   c.asset_category as category,
                   m.name as model,
                   b.branch_name as branch,
                   a.status as status,
                   a.purchase_date as purchaseDate,
                   a.current_value as currentValue,
                   a.created_at as createdAt
            FROM assets a
            LEFT JOIN asset_categories c ON a.category_id = c.id
            LEFT JOIN asset_models m ON a.model_id = m.id
            LEFT JOIN branches b ON a.branch_id = b.id
            WHERE a.deleted_at IS NULL
            ORDER BY a.created_at DESC
            LIMIT 10
            """;

        jakarta.persistence.Query nativeQuery = entityManager.createNativeQuery(query);
        List<Object[]> results = nativeQuery.getResultList();

        return results.stream().map(result -> RecentAssetDto.builder()
                .assetId(UUID.fromString(result[0].toString()))
                .assetCode((String) result[1])
                .assetName((String) result[2])
                .category((String) result[3])
                .model((String) result[4])
                .branch((String) result[5])
                .status((String) result[6])
                .purchaseDate(result[7] != null ? ((java.sql.Date) result[7]).toLocalDate() : null)
                .currentValue((BigDecimal) result[8])
                .createdAt(result[9] != null ? ((java.sql.Timestamp) result[9]).toLocalDateTime() : null)
                .build()).toList();
    }

    // ==================== RECENT ACTIVITY ====================

    public List<RecentActivityDto> getRecentActivity() {
        String query = """
            SELECT ah.id as id,
                   a.id as assetId,
                   a.asset_code as assetCode,
                   a.name as assetName,
                   ah.action as eventType,
                   ah.description as eventDetails,
                   ah.changed_by as performedBy,
                   ah.created_at as createdAt
            FROM asset_history ah
            JOIN assets a ON ah.asset_id = a.id
            ORDER BY ah.created_at DESC
            LIMIT 15
            """;

        jakarta.persistence.Query nativeQuery = entityManager.createNativeQuery(query);
        List<Object[]> results = nativeQuery.getResultList();

        return results.stream().map(result -> RecentActivityDto.builder()
                .id(UUID.fromString(result[0].toString()))
                .assetId(UUID.fromString(result[1].toString()))
                .assetCode((String) result[2])
                .assetName((String) result[3])
                .eventType((String) result[4])
                .eventDetails((String) result[5])
                .performedBy((String) result[6])
                .createdAt(result[7] != null ? ((java.sql.Timestamp) result[7]).toLocalDateTime() : null)
                .build()).toList();
    }

    // ==================== ATTENTION REQUIRED SUMMARY ====================

    public AttentionRequiredDto getAttentionRequired() {
        String query = """
            SELECT
                (SELECT COUNT(*) FROM asset_maintenance WHERE next_due_date <= DATE_ADD(CURRENT_DATE, INTERVAL 7 DAY) AND next_due_date >= CURRENT_DATE) as maintenanceDue,
                (SELECT COUNT(*) FROM asset_maintenance WHERE next_due_date < CURRENT_DATE) as overdueMaintenance,
                (SELECT COUNT(*) FROM asset_warranty WHERE end_date <= DATE_ADD(CURRENT_DATE, INTERVAL 30 DAY) AND end_date >= CURRENT_DATE) as warrantyExpiring,
                (SELECT COUNT(*) FROM asset_request WHERE status = 'SEND') as pendingRequests,
                (SELECT COUNT(*) FROM audit_run WHERE total_discrepancies > 0 AND status IN ('COMPLETED', 'COMPLETED_WITH_EXCEPTIONS', 'RECONCILIATION')) as auditDiscrepancies,
                (SELECT COUNT(*) FROM assets WHERE status = 'LOST' AND deleted_at IS NULL) as damagedAssets
            """;

        jakarta.persistence.Query nativeQuery = entityManager.createNativeQuery(query);
        Object[] result = (Object[]) nativeQuery.getSingleResult();

        return AttentionRequiredDto.builder()
                .maintenanceDue(result[0] != null ? ((Number) result[0]).longValue() : 0L)
                .overdueMaintenance(result[1] != null ? ((Number) result[1]).longValue() : 0L)
                .warrantyExpiring(result[2] != null ? ((Number) result[2]).longValue() : 0L)
                .pendingRequests(result[3] != null ? ((Number) result[3]).longValue() : 0L)
                .auditDiscrepancies(result[4] != null ? ((Number) result[4]).longValue() : 0L)
                .damagedAssets(result[5] != null ? ((Number) result[5]).longValue() : 0L)
                .build();
    }
}
