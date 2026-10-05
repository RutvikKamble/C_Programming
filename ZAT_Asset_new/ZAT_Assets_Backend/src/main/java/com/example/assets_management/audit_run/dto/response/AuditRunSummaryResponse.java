package com.example.assets_management.audit_run.dto.response;

public record AuditRunSummaryResponse(
        Long auditRunId,
        String status,
        int totalExpectedAssets,
        int totalVerifiedAssets,
        int totalMissingAssets,
        int totalDamagedAssets,
        int totalDiscrepancies,
        int totalScannedAssets,
        double progressPercentage,
        boolean passed
) {}
