package com.example.assets_management.asset_vehicle.service;

import com.example.assets_management.asset_vehicle.dto.request.VehicleServiceLogRequest;
import com.example.assets_management.asset_vehicle.dto.response.VehicleServiceLogResponse;

import java.util.List;
import java.util.UUID;

public interface VehicleServiceLogService {
    VehicleServiceLogResponse create(UUID vehicleId, VehicleServiceLogRequest request);
    VehicleServiceLogResponse update(UUID vehicleId, UUID logId, VehicleServiceLogRequest request);
    void delete(UUID vehicleId, UUID logId);
    VehicleServiceLogResponse findById(UUID vehicleId, UUID logId);
    List<VehicleServiceLogResponse> findByVehicleId(UUID vehicleId);
}
