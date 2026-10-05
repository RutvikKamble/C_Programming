package com.example.assets_management.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Asset request summary statistics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestSummaryDto {

    private Long pendingDeptHead;
    private Long pendingAssetManager;
    private Long approved;
    private Long rejected;
    private Long fulfilled;
    private Long totalPending;
}
