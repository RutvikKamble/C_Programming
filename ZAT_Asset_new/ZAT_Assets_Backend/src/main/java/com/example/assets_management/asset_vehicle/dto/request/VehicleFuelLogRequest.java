package com.example.assets_management.asset_vehicle.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record VehicleFuelLogRequest(
        @NotNull(message = "date is required")
        LocalDate date,

        @NotNull(message = "amount is required")
        @DecimalMin(value = "0.01", message = "amount must be greater than zero")
        BigDecimal amount,

        @NotNull(message = "cost is required")
        @DecimalMin(value = "0.00", message = "cost must be zero or greater")
        BigDecimal cost,

        @Size(max = 5000, message = "remarks must be at most 5000 characters")
        String remarks,

        @Size(max = 150, message = "changedBy must be at most 150 characters")
        String changedBy
) {}
