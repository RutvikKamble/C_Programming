package com.example.assets_management.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Recently created asset details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentAssetDto {

    private UUID assetId;
    private String assetCode;
    private String assetName;
    private String category;
    private String model;
    private String branch;
    private String status;
    private LocalDate purchaseDate;
    private BigDecimal currentValue;
    private LocalDateTime createdAt;
}
