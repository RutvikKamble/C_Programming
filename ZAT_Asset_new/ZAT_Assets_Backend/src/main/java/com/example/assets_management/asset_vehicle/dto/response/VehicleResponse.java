package com.example.assets_management.asset_vehicle.dto.response;

import com.example.assets_management.asset_master.enums.AssetStatus;
import com.example.assets_management.asset_vehicle.enums.FuelType;

import java.time.LocalDateTime;
import java.util.UUID;

public record VehicleResponse(
        UUID id,
        UUID assetId,
        String assetCode,
        String assetName,
        AssetStatus assetStatus,
        String registrationNumber,
        UUID insurancePolicyId,
        String insurancePolicyNumber,
        FuelType fuelType,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
