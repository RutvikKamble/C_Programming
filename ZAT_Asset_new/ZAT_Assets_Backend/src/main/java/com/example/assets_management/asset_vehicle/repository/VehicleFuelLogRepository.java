package com.example.assets_management.asset_vehicle.repository;

import com.example.assets_management.asset_vehicle.entity.VehicleFuelLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VehicleFuelLogRepository extends JpaRepository<VehicleFuelLog, UUID> {
    List<VehicleFuelLog> findByVehicleDetails_IdOrderByDateDesc(UUID vehicleId);
}
