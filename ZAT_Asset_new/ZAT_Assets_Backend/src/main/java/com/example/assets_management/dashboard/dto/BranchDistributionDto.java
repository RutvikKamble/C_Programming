package com.example.assets_management.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Asset distribution by branch.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchDistributionDto {

    private UUID branchId;
    private String branchName;
    private Long assetCount;
    private BigDecimal assetValue;
}
