package com.example.assets_management.asset_vehicle.service;

import com.example.assets_management.asset_vehicle.dto.request.VehicleFuelLogRequest;
import com.example.assets_management.asset_vehicle.dto.response.VehicleFuelLogResponse;

import java.util.List;
import java.util.UUID;

public interface VehicleFuelLogService {
    VehicleFuelLogResponse create(UUID vehicleId, VehicleFuelLogRequest request);
    VehicleFuelLogResponse update(UUID vehicleId, UUID logId, VehicleFuelLogRequest request);
    void delete(UUID vehicleId, UUID logId);
    VehicleFuelLogResponse findById(UUID vehicleId, UUID logId);
    List<VehicleFuelLogResponse> findByVehicleId(UUID vehicleId);
}
