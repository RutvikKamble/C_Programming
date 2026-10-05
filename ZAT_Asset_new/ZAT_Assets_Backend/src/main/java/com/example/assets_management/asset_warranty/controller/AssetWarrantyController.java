package com.example.assets_management.asset_warranty.controller;

import org.springframework.security.access.prepost.PreAuthorize;

import com.example.assets_management.asset_warranty.service.AssetWarrantyService;
import com.example.assets_management.asset_warranty.entity.AssetWarranty;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/asset-warranty")
public class AssetWarrantyController {

    private final AssetWarrantyService warrantyService;

    public AssetWarrantyController(AssetWarrantyService warrantyService) {
        this.warrantyService = warrantyService;
    }

    // Creates a new warranty record
    @PreAuthorize("hasAuthority(\'Asset.Warranty.Create\')")
    @PostMapping
    public ResponseEntity<AssetWarranty> createWarranty(@Valid @RequestBody AssetWarranty warranty) {
        return new ResponseEntity<>(warrantyService.createWarranty(warranty), HttpStatus.CREATED);
    }

    // Fully updates an existing warranty record (all fields replaced)
    @PreAuthorize("hasAuthority(\'Asset.Warranty.Update\')")
    @PutMapping("/{id}")
    public ResponseEntity<AssetWarranty> updateWarranty(@PathVariable Long id,
                                                        @Valid @RequestBody AssetWarranty warranty) {
        return new ResponseEntity<>(warrantyService.updateWarranty(id, warranty), HttpStatus.OK);
    }

    // Deletes a warranty record by ID
    @PreAuthorize("hasAuthority(\'Asset.Warranty.Delete\')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWarranty(@PathVariable Long id) {
        warrantyService.deleteWarranty(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Retrieves a single warranty record by ID
    @PreAuthorize("hasAuthority(\'Asset.Warranty.View\')")
    @GetMapping("/{id}")
    public ResponseEntity<AssetWarranty> getWarrantyById(@PathVariable Long id) {
        return new ResponseEntity<>(warrantyService.getWarrantyById(id), HttpStatus.OK);
    }

    // Retrieves all warranty records
    @PreAuthorize("hasAuthority(\'Asset.Warranty.View\')")
    @GetMapping
    public ResponseEntity<List<AssetWarranty>> getAllWarranty() {
        return new ResponseEntity<>(warrantyService.getAllWarranty(), HttpStatus.OK);
    }

    // Retrieves all warranties for a specific asset
    @PreAuthorize("hasAuthority(\'Asset.Warranty.View\')")
    @GetMapping("/asset/{assetId}")
    public ResponseEntity<List<AssetWarranty>> getWarrantyByAssetId(@PathVariable UUID assetId) {
        return new ResponseEntity<>(warrantyService.getWarrantyByAssetId(assetId), HttpStatus.OK);
    }

    // Retrieves all warranties that are expiring within the next 30 days (alert functionality)
    @PreAuthorize("hasAuthority(\'Asset.Warranty.View\')")
    @GetMapping("/expiring-soon")
    public ResponseEntity<List<AssetWarranty>> getExpiringSoon() {
        return new ResponseEntity<>(warrantyService.getExpiringSoonWarranties(), HttpStatus.OK);
    }
}
