package com.example.assets_management.asset_software.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record LicenseAllocationRequest(
        @NotNull(message = "employeeId is required") UUID employeeId,
        @Size(max = 150, message = "changedBy must be at most 150 characters") String changedBy
) {}
