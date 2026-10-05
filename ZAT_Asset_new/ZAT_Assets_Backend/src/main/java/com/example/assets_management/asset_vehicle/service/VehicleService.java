package com.example.assets_management.asset_vehicle.service;

import com.example.assets_management.asset_vehicle.dto.request.VehicleCreateRequest;
import com.example.assets_management.asset_vehicle.dto.request.VehicleUpdateRequest;
import com.example.assets_management.asset_vehicle.dto.response.VehicleResponse;

import java.util.List;
import java.util.UUID;

public interface VehicleService {
    VehicleResponse create(VehicleCreateRequest request);
    VehicleResponse update(UUID vehicleId, VehicleUpdateRequest request);
    void delete(UUID vehicleId);
    VehicleResponse findById(UUID vehicleId);
    VehicleResponse findByAssetId(UUID assetId);
    List<VehicleResponse> findAll();
}
