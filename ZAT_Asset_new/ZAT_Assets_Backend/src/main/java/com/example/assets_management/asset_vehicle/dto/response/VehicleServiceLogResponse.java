package com.example.assets_management.asset_vehicle.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record VehicleServiceLogResponse(
        UUID id,
        UUID vehicleId,
        UUID assetId,
        String assetCode,
        LocalDate serviceDate,
        BigDecimal odometerReading,
        BigDecimal cost,
        UUID vendorId,
        String vendorName,
        String remarks,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
