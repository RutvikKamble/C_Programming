package com.example.assets_management.asset_vehicle.controller;

import com.example.assets_management.asset_vehicle.dto.request.VehicleCreateRequest;
import com.example.assets_management.asset_vehicle.dto.request.VehicleUpdateRequest;
import com.example.assets_management.asset_vehicle.dto.response.VehicleResponse;
import com.example.assets_management.asset_vehicle.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @PreAuthorize("hasAuthority('Asset.Vehicle.View')")
    @GetMapping
    public ResponseEntity<List<VehicleResponse>> getAll() {
        return ResponseEntity.ok(vehicleService.findAll());
    }

    @PreAuthorize("hasAuthority('Asset.Vehicle.View')")
    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(vehicleService.findById(id));
    }

    @PreAuthorize("hasAuthority('Asset.Vehicle.View')")
    @GetMapping("/asset/{assetId}")
    public ResponseEntity<VehicleResponse> getByAsset(@PathVariable UUID assetId) {
        return ResponseEntity.ok(vehicleService.findByAssetId(assetId));
    }

    @PreAuthorize("hasAuthority('Asset.Vehicle.Create')")
    @PostMapping
    public ResponseEntity<VehicleResponse> create(@Valid @RequestBody VehicleCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehicleService.create(request));
    }

    @PreAuthorize("hasAuthority('Asset.Vehicle.Update')")
    @PatchMapping("/{id}")
    public ResponseEntity<VehicleResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody VehicleUpdateRequest request) {
        return ResponseEntity.ok(vehicleService.update(id, request));
    }

    @PreAuthorize("hasAuthority('Asset.Vehicle.Delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        vehicleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
