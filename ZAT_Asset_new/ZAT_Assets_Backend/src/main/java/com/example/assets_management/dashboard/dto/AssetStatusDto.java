package com.example.assets_management.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Asset status distribution with count and percentage.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetStatusDto {

    private String status;
    private Long count;
    private Double percentage;
}
