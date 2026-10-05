package com.example.assets_management.asset_software.controller;

import com.example.assets_management.asset_software.dto.request.BulkLicenseAllocationRequest;
import com.example.assets_management.asset_software.dto.request.LicenseAllocationRequest;
import com.example.assets_management.asset_software.dto.request.LicenseAllocationUpdateRequest;
import com.example.assets_management.asset_software.dto.response.LicenseAllocationResponse;
import com.example.assets_management.asset_software.service.LicenseAllocationService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/software-licenses/{licenseId}/allocations")
public class SoftwareLicenseAllocationController {
    private final LicenseAllocationService service;

    public SoftwareLicenseAllocationController(LicenseAllocationService service) {
        this.service = service;
    }

    @PreAuthorize("hasAuthority('Asset.Software_Asset.Manage')")
    @PostMapping
    public ResponseEntity<LicenseAllocationResponse> allocate(@PathVariable UUID licenseId, @Valid @RequestBody LicenseAllocationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.allocate(licenseId, request));
    }

    @PreAuthorize("hasAuthority('Asset.Software_Asset.Manage')")
    @PostMapping("/bulk")
    public ResponseEntity<List<LicenseAllocationResponse>> bulkAllocate(@PathVariable UUID licenseId, @Valid @RequestBody BulkLicenseAllocationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.bulkAllocate(licenseId, request));
    }

    @PreAuthorize("hasAuthority('Asset.Software_Asset.Manage')")
    @PostMapping("/bulk-release")
    public ResponseEntity<String> bulkRelease(@PathVariable UUID licenseId, @RequestParam(required = false) String changedBy) {
        return ResponseEntity.ok("Released allocations: " + service.bulkRelease(licenseId, changedBy));
    }

    @PreAuthorize("hasAuthority('Asset.Software_Asset.View')")
    @GetMapping
    public ResponseEntity<List<LicenseAllocationResponse>> findAll(@PathVariable UUID licenseId) {
        return ResponseEntity.ok(service.findByLicense(licenseId));
    }

    @PreAuthorize("hasAuthority('Asset.Software_Asset.View')")
    @GetMapping("/{allocationId}")
    public ResponseEntity<LicenseAllocationResponse> findById(@PathVariable UUID licenseId, @PathVariable UUID allocationId) {
        return ResponseEntity.ok(service.findById(licenseId, allocationId));
    }

    @PreAuthorize("hasAuthority('Asset.Software_Asset.Update')")
    @PatchMapping("/{allocationId}")
    public ResponseEntity<LicenseAllocationResponse> update(@PathVariable UUID licenseId, @PathVariable UUID allocationId, @Valid @RequestBody LicenseAllocationUpdateRequest request) {
        return ResponseEntity.ok(service.update(licenseId, allocationId, request));
    }

    @PreAuthorize("hasAuthority('Asset.Software_Asset.Manage')")
    @PostMapping("/{allocationId}/release")
    public ResponseEntity<LicenseAllocationResponse> release(@PathVariable UUID licenseId, @PathVariable UUID allocationId, @RequestParam(required = false) String changedBy) {
        return ResponseEntity.ok(service.release(licenseId, allocationId, changedBy));
    }

    // Snipe-IT-style seat aliases; a ZAT allocation record is the persistent seat identifier.
    @PreAuthorize("hasAuthority('Asset.Software_Asset.View')")
    @GetMapping("/seats")
    public ResponseEntity<List<LicenseAllocationResponse>> findSeats(@PathVariable UUID licenseId) {
        return ResponseEntity.ok(service.findByLicense(licenseId));
    }

    @PreAuthorize("hasAuthority('Asset.Software_Asset.View')")
    @GetMapping("/seats/{seatId}")
    public ResponseEntity<LicenseAllocationResponse> findSeat(@PathVariable UUID licenseId, @PathVariable UUID seatId) {
        return ResponseEntity.ok(service.findById(licenseId, seatId));
    }

}
