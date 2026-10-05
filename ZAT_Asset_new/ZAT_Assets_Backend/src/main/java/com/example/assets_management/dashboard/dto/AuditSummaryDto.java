package com.example.assets_management.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Audit summary statistics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditSummaryDto {

    private Long totalAudits;
    private Long inProgress;
    private Long completed;
    private Long totalExpectedAssets;
    private Long totalFoundAssets;
    private Long totalMissingAssets;
    private Long totalDiscrepancies;
}
