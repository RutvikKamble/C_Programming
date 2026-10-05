package com.example.assets_management.asset_vehicle.service;

import com.example.assets_management.asset_history.dto.request.AssetHistoryRequest;
import com.example.assets_management.asset_history.service.AssetHistoryService;
import com.example.assets_management.asset_master.entity.Asset;
import com.example.assets_management.asset_master.enums.AssetStatus;
import com.example.assets_management.asset_vehicle.dto.request.VehicleServiceLogRequest;
import com.example.assets_management.asset_vehicle.dto.response.VehicleServiceLogResponse;
import com.example.assets_management.asset_vehicle.entity.VehicleDetails;
import com.example.assets_management.asset_vehicle.entity.VehicleServiceLog;
import com.example.assets_management.asset_vehicle.repository.VehicleDetailsRepository;
import com.example.assets_management.asset_vehicle.repository.VehicleServiceLogRepository;
import com.example.assets_management.exception.ResourceNotFoundException;
import com.example.assets_management.vendor.entity.Vendor;
import com.example.assets_management.vendor.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleServiceLogServiceImpl implements VehicleServiceLogService {

    private final VehicleDetailsRepository vehicleRepository;
    private final VehicleServiceLogRepository serviceLogRepository;
    private final VendorRepository vendorRepository;
    private final AssetHistoryService assetHistoryService;

    @Override
    @Transactional
    public VehicleServiceLogResponse create(UUID vehicleId, VehicleServiceLogRequest request) {
        VehicleDetails vehicle = getMutableVehicle(vehicleId);
        validateDate(request.serviceDate());
        validateOdometerNotBelowExisting(vehicleId, request.odometerReading(), null);
        Vendor vendor = resolveVendor(request.vendorId());

        VehicleServiceLog log = new VehicleServiceLog();
        log.setVehicleDetails(vehicle);
        log.setServiceDate(request.serviceDate());
        log.setOdometerReading(request.odometerReading());
        log.setCost(request.cost());
        log.setVendor(vendor);
        log.setRemarks(trimToNull(request.remarks()));

        VehicleServiceLog saved = serviceLogRepository.save(log);
        recordHistory(vehicle.getAsset(), "VEHICLE_SERVICE_ADDED", "serviceLogId", null,
                saved.getId().toString(),
                "Vehicle service recorded at odometer " + saved.getOdometerReading(), request.changedBy());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public VehicleServiceLogResponse update(UUID vehicleId, UUID logId, VehicleServiceLogRequest request) {
        VehicleDetails vehicle = getMutableVehicle(vehicleId);
        VehicleServiceLog log = getLog(vehicleId, logId);
        validateDate(request.serviceDate());
        validateOdometerNotBelowExisting(vehicleId, request.odometerReading(), logId);
        Vendor vendor = resolveVendor(request.vendorId());

        log.setServiceDate(request.serviceDate());
        log.setOdometerReading(request.odometerReading());
        log.setCost(request.cost());
        log.setVendor(vendor);
        log.setRemarks(trimToNull(request.remarks()));

        VehicleServiceLog saved = serviceLogRepository.save(log);
        recordHistory(vehicle.getAsset(), "VEHICLE_SERVICE_UPDATED", "serviceLogId",
                logId.toString(), logId.toString(), "Vehicle service log updated", request.changedBy());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(UUID vehicleId, UUID logId) {
        VehicleDetails vehicle = getMutableVehicle(vehicleId);
        VehicleServiceLog log = getLog(vehicleId, logId);
        serviceLogRepository.delete(log);
        recordHistory(vehicle.getAsset(), "VEHICLE_SERVICE_DELETED", "serviceLogId",
                logId.toString(), null, "Vehicle service log deleted", null);
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleServiceLogResponse findById(UUID vehicleId, UUID logId) {
        return toResponse(getLog(vehicleId, logId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleServiceLogResponse> findByVehicleId(UUID vehicleId) {
        if (!vehicleRepository.existsById(vehicleId)) {
            throw new ResourceNotFoundException("Vehicle details not found: " + vehicleId);
        }
        return serviceLogRepository.findByVehicleDetails_IdOrderByServiceDateDescOdometerReadingDesc(vehicleId)
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

    private VehicleServiceLog getLog(UUID vehicleId, UUID logId) {
        VehicleServiceLog log = serviceLogRepository.findById(logId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle service log not found: " + logId));
        if (log.getVehicleDetails() == null || !vehicleId.equals(log.getVehicleDetails().getId())) {
            throw new ResourceNotFoundException("Vehicle service log not found for vehicle: " + vehicleId);
        }
        return log;
    }

    private Vendor resolveVendor(UUID vendorId) {
        if (vendorId == null) return null;
        return vendorRepository.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found: " + vendorId));
    }

    private void validateDate(LocalDate date) {
        if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("serviceDate cannot be in the future");
        }
    }

    private void validateOdometerNotBelowExisting(UUID vehicleId, BigDecimal reading, UUID currentLogId) {
        List<VehicleServiceLog> logs = serviceLogRepository
                .findByVehicleDetails_IdOrderByServiceDateDescOdometerReadingDesc(vehicleId);
        logs.stream()
                .filter(item -> currentLogId == null || !item.getId().equals(currentLogId))
                .map(VehicleServiceLog::getOdometerReading)
                .max(BigDecimal::compareTo)
                .ifPresent(maxOther -> {
                    if (reading.compareTo(maxOther) < 0) {
                        throw new IllegalArgumentException(
                                "odometerReading cannot be lower than the vehicle's existing service odometer reading of " + maxOther);
                    }
                });
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

    private VehicleServiceLogResponse toResponse(VehicleServiceLog log) {
        VehicleDetails vehicle = log.getVehicleDetails();
        Asset asset = vehicle != null ? vehicle.getAsset() : null;
        Vendor vendor = log.getVendor();
        return new VehicleServiceLogResponse(
                log.getId(),
                vehicle != null ? vehicle.getId() : null,
                asset != null ? asset.getId() : null,
                asset != null ? asset.getAssetCode() : null,
                log.getServiceDate(),
                log.getOdometerReading(),
                log.getCost(),
                vendor != null ? vendor.getId() : null,
                vendor != null ? vendor.getName() : null,
                log.getRemarks(),
                log.getCreatedAt(),
                log.getUpdatedAt()
        );
    }
}
