package com.example.assets_management.vendor.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.example.assets_management.vendor.entity.Vendor;
import com.example.assets_management.vendor.service.VendorService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/vendors")
@RequiredArgsConstructor
public class VendorController {

    private final VendorService vendorService;

    @PreAuthorize("hasAuthority(\'Asset.Vendor.View\')")
    @GetMapping
    public List<Vendor> getAllVendors() {
        return vendorService.findAll();
    }

    @PreAuthorize("hasAuthority(\'Asset.Vendor.View\')")
    @GetMapping("/{id}")
    public ResponseEntity<Vendor> getVendorById(@PathVariable UUID id) {

        return vendorService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAuthority(\'Asset.Vendor.Create\')")
    @PostMapping
    public Vendor createVendor(@RequestBody Vendor vendor) {
        return vendorService.save(vendor);
    }

    @PreAuthorize("hasAuthority(\'Asset.Vendor.Update\')")
    @PutMapping("/{id}")
    public ResponseEntity<Vendor> updateVendor(
            @PathVariable UUID id,
            @RequestBody Vendor vendor) {

        try {

            Vendor updated = vendorService.update(id, vendor);
            return ResponseEntity.ok(updated);

        } catch (RuntimeException e) {

            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAuthority(\'Asset.Vendor.Delete\')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVendor(@PathVariable UUID id) {

        vendorService.delete(id);
        return ResponseEntity.noContent().build();

    }
}