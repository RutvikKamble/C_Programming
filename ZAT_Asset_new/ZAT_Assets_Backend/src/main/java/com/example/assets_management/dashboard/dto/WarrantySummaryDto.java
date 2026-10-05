package com.example.assets_management.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Warranty summary statistics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarrantySummaryDto {

    private Long expired;
    private Long expiring30Days;
    private Long expiring60Days;
    private Long expiring90Days;
}
