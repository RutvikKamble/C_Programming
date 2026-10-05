package com.example.assets_management.asset_vehicle.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record VehicleFuelLogResponse(
        UUID id,
        UUID vehicleId,
        UUID assetId,
        String assetCode,
        LocalDate date,
        BigDecimal amount,
        BigDecimal cost,
        String remarks,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
