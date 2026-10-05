package com.example.assets_management.asset_vehicle.service;

import com.example.assets_management.asset_history.dto.request.AssetHistoryRequest;
import com.example.assets_management.asset_history.service.AssetHistoryService;
import com.example.assets_management.asset_master.entity.Asset;
import com.example.assets_management.asset_master.enums.AssetStatus;
import com.example.assets_management.asset_vehicle.dto.request.VehicleFuelLogRequest;
import com.example.assets_management.asset_vehicle.dto.response.VehicleFuelLogResponse;
import com.example.assets_management.asset_vehicle.entity.VehicleDetails;
import com.example.assets_management.asset_vehicle.entity.VehicleFuelLog;
import com.example.assets_management.asset_vehicle.repository.VehicleDetailsRepository;
import com.example.assets_management.asset_vehicle.repository.VehicleFuelLogRepository;
import com.example.assets_management.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleFuelLogServiceImpl implements VehicleFuelLogService {

    private final VehicleDetailsRepository vehicleRepository;
    private final VehicleFuelLogRepository fuelLogRepository;
    private final AssetHistoryService assetHistoryService;

    @Override
    @Transactional
    public VehicleFuelLogResponse create(UUID vehicleId, VehicleFuelLogRequest request) {
        VehicleDetails vehicle = getMutableVehicle(vehicleId);
        validateDate(request.date());

        VehicleFuelLog log = new VehicleFuelLog();
        log.setVehicleDetails(vehicle);
        log.setDate(request.date());
        log.setAmount(request.amount());
        log.setCost(request.cost());
        log.setRemarks(trimToNull(request.remarks()));

        VehicleFuelLog saved = fuelLogRepository.save(log);
        recordHistory(vehicle.getAsset(), "VEHICLE_FUEL_ADDED", "fuelLogId", null,
                saved.getId().toString(), "Vehicle fuel entry recorded", request.changedBy());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public VehicleFuelLogResponse update(UUID vehicleId, UUID logId, VehicleFuelLogRequest request) {
        VehicleDetails vehicle = getMutableVehicle(vehicleId);
        VehicleFuelLog log = getLog(vehicleId, logId);
        validateDate(request.date());

        log.setDate(request.date());
        log.setAmount(request.amount());
        log.setCost(request.cost());
        log.setRemarks(trimToNull(request.remarks()));

        VehicleFuelLog saved = fuelLogRepository.save(log);
        recordHistory(vehicle.getAsset(), "VEHICLE_FUEL_UPDATED", "fuelLogId",
                logId.toString(), logId.toString(), "Vehicle fuel entry updated", request.changedBy());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(UUID vehicleId, UUID logId) {
        VehicleDetails vehicle = getMutableVehicle(vehicleId);
        VehicleFuelLog log = getLog(vehicleId, logId);
        fuelLogRepository.delete(log);
        recordHistory(vehicle.getAsset(), "VEHICLE_FUEL_DELETED", "fuelLogId",
                logId.toString(), null, "Vehicle fuel entry deleted", null);
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleFuelLogResponse findById(UUID vehicleId, UUID logId) {
        return toResponse(getLog(vehicleId, logId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleFuelLogResponse> findByVehicleId(UUID vehicleId) {
        if (!vehicleRepository.existsById(vehicleId)) {
            throw new ResourceNotFoundException("Vehicle details not found: " + vehicleId);
        }
        return fuelLogRepository.findByVehicleDetails_IdOrderByDateDesc(vehicleId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    private VehicleDetails getMutableVehicle(UUID vehicleId) {
        VehicleDetails vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle details not found: " + vehicleId));
        Asset asset = vehicle.getAsset();
        if (asset == null || asset.getStatus() == AssetStatus.CLOSED) {
            throw new IllegalStateException("Closed/disposed asset cannot be modified");
        }
        return vehicle;
    }

    private VehicleFuelLog getLog(UUID vehicleId, UUID logId) {
        VehicleFuelLog log = fuelLogRepository.findById(logId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle fuel log not found: " + logId));
        if (log.getVehicleDetails() == null || !vehicleId.equals(log.getVehicleDetails().getId())) {
            throw new ResourceNotFoundException("Vehicle fuel log not found for vehicle: " + vehicleId);
        }
        return log;
    }

    private void validateDate(LocalDate date) {
        if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("date cannot be in the future");
        }
    }

    private String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private void recordHistory(Asset asset, String action, String field, String oldValue,
                               String newValue, String description, String changedBy) {
        assetHistoryService.create(new AssetHistoryRequest(
                asset.getId(), action, field, oldValue, newValue, description,
                changedBy == null || changedBy.isBlank() ? "SYSTEM" : changedBy.trim()));
    }

    private VehicleFuelLogResponse toResponse(VehicleFuelLog log) {
        VehicleDetails vehicle = log.getVehicleDetails();
        Asset asset = vehicle != null ? vehicle.getAsset() : null;
        return new VehicleFuelLogResponse(
                log.getId(),
                vehicle != null ? vehicle.getId() : null,
                asset != null ? asset.getId() : null,
                asset != null ? asset.getAssetCode() : null,
                log.getDate(),
                log.getAmount(),
                log.getCost(),
                log.getRemarks(),
                log.getCreatedAt(),
                log.getUpdatedAt()
        );
    }
}
