package com.example.assets_management.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Maintenance status distribution.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceStatusDto {

    private String status;
    private Long count;
}
