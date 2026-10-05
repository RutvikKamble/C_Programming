package com.example.assets_management.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Recent asset activity from asset history.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentActivityDto {

    private UUID id;
    private UUID assetId;
    private String assetCode;
    private String assetName;
    private String eventType;
    private String eventDetails;
    private String performedBy;
    private LocalDateTime createdAt;
}
