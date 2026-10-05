package com.example.assets_management.asset_vehicle.dto.request;

import com.example.assets_management.asset_vehicle.enums.FuelType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record VehicleUpdateRequest(
        @NotBlank(message = "registrationNumber is required")
        @Size(max = 30, message = "registrationNumber must be at most 30 characters")
        String registrationNumber,

        UUID insurancePolicyId,

        @NotNull(message = "fuelType is required")
        FuelType fuelType,

        @Size(max = 150, message = "changedBy must be at most 150 characters")
        String changedBy
) {}
