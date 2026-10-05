package com.example.assets_management.asset_vehicle.controller;

import com.example.assets_management.asset_vehicle.dto.request.VehicleServiceLogRequest;
import com.example.assets_management.asset_vehicle.dto.response.VehicleServiceLogResponse;
import com.example.assets_management.asset_vehicle.service.VehicleServiceLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/vehicles/{vehicleId}/service-logs")
@RequiredArgsConstructor
public class VehicleServiceLogController {

    private final VehicleServiceLogService serviceLogService;

    @PreAuthorize("hasAuthority('Asset.Vehicle.View')")
    @GetMapping
    public ResponseEntity<List<VehicleServiceLogResponse>> getAll(@PathVariable UUID vehicleId) {
        return ResponseEntity.ok(serviceLogService.findByVehicleId(vehicleId));
    }

    @PreAuthorize("hasAuthority('Asset.Vehicle.View')")
    @GetMapping("/{logId}")
    public ResponseEntity<VehicleServiceLogResponse> getById(
            @PathVariable UUID vehicleId,
            @PathVariable UUID logId) {
        return ResponseEntity.ok(serviceLogService.findById(vehicleId, logId));
    }

    @PreAuthorize("hasAuthority('Asset.Vehicle.Update')")
    @PostMapping
    public ResponseEntity<VehicleServiceLogResponse> create(
            @PathVariable UUID vehicleId,
            @Valid @RequestBody VehicleServiceLogRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(serviceLogService.create(vehicleId, request));
    }

    @PreAuthorize("hasAuthority('Asset.Vehicle.Update')")
    @PatchMapping("/{logId}")
    public ResponseEntity<VehicleServiceLogResponse> update(
            @PathVariable UUID vehicleId,
            @PathVariable UUID logId,
            @Valid @RequestBody VehicleServiceLogRequest request) {
        return ResponseEntity.ok(serviceLogService.update(vehicleId, logId, request));
    }

    @PreAuthorize("hasAuthority('Asset.Vehicle.Delete')")
    @DeleteMapping("/{logId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID vehicleId,
            @PathVariable UUID logId) {
        serviceLogService.delete(vehicleId, logId);
        return ResponseEntity.noContent().build();
    }
}
