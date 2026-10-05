package com.example.assets_management.asset_vehicle.repository;

import com.example.assets_management.asset_vehicle.entity.VehicleServiceLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VehicleServiceLogRepository extends JpaRepository<VehicleServiceLog, UUID> {
    List<VehicleServiceLog> findByVehicleDetails_IdOrderByServiceDateDescOdometerReadingDesc(UUID vehicleId);
    Optional<VehicleServiceLog> findFirstByVehicleDetails_IdOrderByOdometerReadingDesc(UUID vehicleId);
    Optional<VehicleServiceLog> findFirstByVehicleDetails_IdOrderByServiceDateDesc(UUID vehicleId);
    boolean existsByVehicleDetails_Id(UUID vehicleId);
    boolean existsByVehicleDetails_IdAndOdometerReadingGreaterThan(UUID vehicleId, BigDecimal odometerReading);
}
