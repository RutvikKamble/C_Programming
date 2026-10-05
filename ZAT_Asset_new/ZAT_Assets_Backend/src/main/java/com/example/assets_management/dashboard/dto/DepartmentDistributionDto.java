package com.example.assets_management.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Asset distribution by department.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDistributionDto {

    private UUID departmentId;
    private String departmentName;
    private Long assetCount;
    private BigDecimal assetValue;
    private Long assignedAssetCount;
}
