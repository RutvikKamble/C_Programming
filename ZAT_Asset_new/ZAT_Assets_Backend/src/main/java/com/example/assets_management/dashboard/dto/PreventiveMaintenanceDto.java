package com.example.assets_management.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Upcoming/overdue preventive maintenance item.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreventiveMaintenanceDto {

    private UUID assetId;
    private String assetCode;
    private String assetName;
    private LocalDate nextDueDate;
    private Long daysRemaining;
    private String status; // OVERDUE, DUE_TODAY, UPCOMING
}
