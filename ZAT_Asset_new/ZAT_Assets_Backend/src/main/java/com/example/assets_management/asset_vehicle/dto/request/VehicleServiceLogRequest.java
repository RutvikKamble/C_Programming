package com.example.assets_management.asset_vehicle.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record VehicleServiceLogRequest(
        @NotNull(message = "serviceDate is required")
        LocalDate serviceDate,

        @NotNull(message = "odometerReading is required")
        @DecimalMin(value = "0.00", message = "odometerReading must be zero or greater")
        BigDecimal odometerReading,

        @NotNull(message = "cost is required")
        @DecimalMin(value = "0.00", message = "cost must be zero or greater")
        BigDecimal cost,

        UUID vendorId,

        @Size(max = 5000, message = "remarks must be at most 5000 characters")
        String remarks,

        @Size(max = 150, message = "changedBy must be at most 150 characters")
        String changedBy
) {}
