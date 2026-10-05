package com.example.assets_management.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Allocation/checkout summary statistics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllocationSummaryDto {

    private Long totalAllocations;
    private Long activeAllocations;
    private Long returnedAllocations;
    private Long goodReturns;
    private Long damagedReturns;
    private Long lostReturns;
}
