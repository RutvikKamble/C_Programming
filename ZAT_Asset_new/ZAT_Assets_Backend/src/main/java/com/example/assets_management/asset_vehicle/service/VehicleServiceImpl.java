package com.example.assets_management.asset_vehicle.service;

import com.example.assets_management.asset_history.dto.request.AssetHistoryRequest;
import com.example.assets_management.asset_history.service.AssetHistoryService;
import com.example.assets_management.asset_insurance.entity.AssetInsurance;
import com.example.assets_management.asset_insurance.repository.AssetInsuranceRepository;
import com.example.assets_management.asset_master.entity.Asset;
import com.example.assets_management.asset_master.enums.AssetStatus;
import com.example.assets_management.asset_master.enums.AssetType;
import com.example.assets_management.asset_master.repository.AssetRepository;
import com.example.assets_management.asset_vehicle.dto.request.VehicleCreateRequest;
import com.example.assets_management.asset_vehicle.dto.request.VehicleUpdateRequest;
import com.example.assets_management.asset_vehicle.dto.response.VehicleResponse;
import com.example.assets_management.asset_vehicle.entity.VehicleDetails;
import com.example.assets_management.asset_vehicle.repository.VehicleDetailsRepository;
import com.example.assets_management.asset_vehicle.repository.VehicleFuelLogRepository;
import com.example.assets_management.asset_vehicle.repository.VehicleServiceLogRepository;
import com.example.assets_management.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleDetailsRepository vehicleRepository;
    private final AssetRepository assetRepository;
    private final AssetInsuranceRepository insuranceRepository;
    private final AssetHistoryService assetHistoryService;
    private final VehicleServiceLogRepository vehicleServiceLogRepository;
    private final VehicleFuelLogRepository vehicleFuelLogRepository;

    @Override
    @Transactional
    public VehicleResponse create(VehicleCreateRequest request) {
        Asset asset = getActiveVehicleAsset(request.assetId());

        if (vehicleRepository.existsByAsset_Id(asset.getId())) {
            throw new IllegalStateException("Vehicle details already exist for asset: " + asset.getAssetCode());
        }

        String registrationNumber = normalizeRegistration(request.registrationNumber());
        if (vehicleRepository.existsByRegistrationNumberIgnoreCase(registrationNumber)) {
            throw new IllegalStateException("Vehicle registration number already exists: " + registrationNumber);
        }

        AssetInsurance insurance = resolveInsurance(asset, request.insurancePolicyId());

        VehicleDetails vehicle = new VehicleDetails();
        vehicle.setAsset(asset);
        vehicle.setRegistrationNumber(registrationNumber);
        vehicle.setInsurancePolicy(insurance);
        vehicle.setFuelType(request.fuelType());

        VehicleDetails saved = vehicleRepository.save(vehicle);

        recordHistory(asset, "VEHICLE_DETAILS_CREATED", null, null,
                registrationNumber,
                "Vehicle details created for asset " + asset.getAssetCode(), request.changedBy());

        return toResponse(saved);
    }

    @Override
    @Transactional
    public VehicleResponse update(UUID vehicleId, VehicleUpdateRequest request) {
        VehicleDetails vehicle = getEntity(vehicleId);
        Asset asset = requireMutableAsset(vehicle.getAsset());

        String registrationNumber = normalizeRegistration(request.registrationNumber());
        if (vehicleRepository.existsByRegistrationNumberIgnoreCaseAndIdNot(registrationNumber, vehicleId)) {
            throw new IllegalStateException("Vehicle registration number already exists: " + registrationNumber);
        }

        AssetInsurance insurance = resolveInsurance(asset, request.insurancePolicyId());
        String oldRegistration = vehicle.getRegistrationNumber();
        String oldFuelType = vehicle.getFuelType() != null ? vehicle.getFuelType().name() : null;
        String oldInsurance = vehicle.getInsurancePolicy() != null ? vehicle.getInsurancePolicy().getPolicyNumber() : null;

        vehicle.setRegistrationNumber(registrationNumber);
        vehicle.setInsurancePolicy(insurance);
        vehicle.setFuelType(request.fuelType());

        VehicleDetails saved = vehicleRepository.save(vehicle);

        recordHistory(asset, "VEHICLE_UPDATED", "registrationNumber", oldRegistration,
                registrationNumber, "Vehicle registration number updated", request.changedBy());
        recordHistory(asset, "VEHICLE_UPDATED", "fuelType", oldFuelType,
                request.fuelType().name(), "Vehicle fuel type updated", request.changedBy());
        String newInsurance = insurance != null ? insurance.getPolicyNumber() : null;
        recordHistory(asset, "VEHICLE_UPDATED", "insurancePolicy", oldInsurance,
                newInsurance, "Vehicle insurance policy reference updated", request.changedBy());

        return toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(UUID vehicleId) {
        VehicleDetails vehicle = getEntity(vehicleId);
        Asset asset = requireMutableAsset(vehicle.getAsset());

        if (vehicleRepository.findByAsset_Id(asset.getId()).isEmpty()) {
            throw new ResourceNotFoundException("Vehicle details not found: " + vehicleId);
        }

        // Do not silently delete the vehicle master when operational history exists.
        // Service/fuel records are lifecycle history and must remain traceable.
        // The repository checks below avoid an accidental cascade/data loss.
        if (vehicleServiceLogExists(vehicleId) || vehicleFuelLogExists(vehicleId)) {
            throw new IllegalStateException("Vehicle cannot be deleted while service or fuel history exists");
        }

        vehicleRepository.delete(vehicle);
        recordHistory(asset, "VEHICLE_DETAILS_DELETED", null,
                vehicle.getRegistrationNumber(), null,
                "Vehicle details deleted for asset " + asset.getAssetCode(), null);
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleResponse findById(UUID vehicleId) {
        return toResponse(getEntity(vehicleId));
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleResponse findByAssetId(UUID assetId) {
        if (!assetRepository.existsById(assetId)) {
            throw new ResourceNotFoundException("Asset not found: " + assetId);
        }
        return toResponse(vehicleRepository.findByAsset_Id(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle details not found for asset: " + assetId)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponse> findAll() {
        return vehicleRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    private Asset getActiveVehicleAsset(UUID assetId) {
        Asset asset = assetRepository.findActiveById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Active asset not found: " + assetId));
        if (asset.getStatus() == AssetStatus.CLOSED) {
            throw new IllegalStateException("Closed/disposed asset cannot be used for vehicle management");
        }
        if (asset.getAssetType() != AssetType.VEHICLE_ASSET) {
            throw new IllegalArgumentException("Asset must have assetType VEHICLE_ASSET: " + assetId);
        }
        return asset;
    }

    private Asset requireMutableAsset(Asset asset) {
        if (asset == null || asset.getId() == null) {
            throw new ResourceNotFoundException("Vehicle asset not found");
        }
        Asset current = assetRepository.findActiveById(asset.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Active asset not found: " + asset.getId()));
        if (current.getStatus() == AssetStatus.CLOSED) {
            throw new IllegalStateException("Closed/disposed asset cannot be modified");
        }
        if (current.getAssetType() != AssetType.VEHICLE_ASSET) {
            throw new IllegalArgumentException("Asset must have assetType VEHICLE_ASSET: " + current.getId());
        }
        return current;
    }

    private AssetInsurance resolveInsurance(Asset asset, UUID insurancePolicyId) {
        if (insurancePolicyId == null) {
            return null;
        }
        AssetInsurance insurance = insuranceRepository.findById(insurancePolicyId)
                .orElseThrow(() -> new ResourceNotFoundException("Insurance policy not found: " + insurancePolicyId));
        if (insurance.getAsset() == null || !asset.getId().equals(insurance.getAsset().getId())) {
            throw new IllegalArgumentException("Insurance policy does not belong to the selected vehicle asset");
        }
        return insurance;
    }

    private VehicleDetails getEntity(UUID vehicleId) {
        return vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle details not found: " + vehicleId));
    }

    private String normalizeRegistration(String registrationNumber) {
        String value = registrationNumber == null ? "" : registrationNumber.trim().toUpperCase(Locale.ROOT);
        if (value.isBlank()) {
            throw new IllegalArgumentException("registrationNumber is required");
        }
        return value;
    }

    private VehicleResponse toResponse(VehicleDetails vehicle) {
        Asset asset = vehicle.getAsset();
        AssetInsurance insurance = vehicle.getInsurancePolicy();
        return new VehicleResponse(
                vehicle.getId(),
                asset != null ? asset.getId() : null,
                asset != null ? asset.getAssetCode() : null,
                asset != null ? asset.getName() : null,
                asset != null ? asset.getStatus() : null,
                vehicle.getRegistrationNumber(),
                insurance != null ? insurance.getId() : null,
                insurance != null ? insurance.getPolicyNumber() : null,
                vehicle.getFuelType(),
                vehicle.getCreatedAt(),
                vehicle.getUpdatedAt()
        );
    }

    private void recordHistory(Asset asset, String action, String field,
                               String oldValue, String newValue,
                               String description, String changedBy) {
        assetHistoryService.create(new AssetHistoryRequest(
                asset.getId(), action, field, oldValue, newValue, description,
                changedBy == null || changedBy.isBlank() ? "SYSTEM" : changedBy.trim()));
    }

    private boolean vehicleServiceLogExists(UUID vehicleId) {
        return vehicleServiceLogRepository.existsByVehicleDetails_Id(vehicleId);
    }

    private boolean vehicleFuelLogExists(UUID vehicleId) {
        return !vehicleFuelLogRepository.findByVehicleDetails_IdOrderByDateDesc(vehicleId).isEmpty();
    }
}
