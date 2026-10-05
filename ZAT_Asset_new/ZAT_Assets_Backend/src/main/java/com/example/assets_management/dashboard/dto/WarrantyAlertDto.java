package com.example.assets_management.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Warranty expiry alert for a specific asset.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarrantyAlertDto {

    private UUID assetId;
    private String assetCode;
    private String assetName;
    private LocalDate warrantyEndDate;
    private String serviceProvider;
    private Long daysRemaining;
    private String status; // EXPIRED, EXPIRING_SOON, ACTIVE
}
