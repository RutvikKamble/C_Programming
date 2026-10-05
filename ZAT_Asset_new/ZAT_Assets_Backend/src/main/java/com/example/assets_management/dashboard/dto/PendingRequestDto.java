package com.example.assets_management.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Pending asset request details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingRequestDto {

    private Long requestId;
    private UUID assetId;
    private String assetCode;
    private String assetName;
    private String requestType;
    private String employeeName;
    private String department;
    private LocalDate requestDate;
    private String status;
    private String priority;
}
