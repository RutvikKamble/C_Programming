package com.example.assets_management.asset_vehicle.repository;

import com.example.assets_management.asset_vehicle.entity.VehicleDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VehicleDetailsRepository extends JpaRepository<VehicleDetails, UUID> {
    Optional<VehicleDetails> findByAsset_Id(UUID assetId);
    List<VehicleDetails> findAllByOrderByCreatedAtDesc();
    boolean existsByAsset_Id(UUID assetId);
    boolean existsByRegistrationNumberIgnoreCase(String registrationNumber);
    boolean existsByRegistrationNumberIgnoreCaseAndIdNot(String registrationNumber, UUID id);
}
