package com.example.assets_management.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Asset distribution by category.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDistributionDto {

    private UUID categoryId;
    private String categoryName;
    private Long assetCount;
    private BigDecimal assetValue;
}
