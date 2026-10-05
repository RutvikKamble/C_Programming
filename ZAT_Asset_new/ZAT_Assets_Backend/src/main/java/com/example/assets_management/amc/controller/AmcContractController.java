package com.example.assets_management.amc.controller;

import org.springframework.security.access.prepost.PreAuthorize;

import com.example.assets_management.amc.entity.AmcContract;
import com.example.assets_management.amc.service.AmcContractService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/amc-contracts")
public class AmcContractController {

    private final AmcContractService service;

    public AmcContractController(AmcContractService service) {
        this.service = service;
    }

    @PreAuthorize("hasAuthority(\'Asset.AMC.View\')")
    @GetMapping
    public ResponseEntity<List<AmcContract>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @PreAuthorize("hasAuthority(\'Asset.AMC.View\')")
    @GetMapping("/{id}")
    public ResponseEntity<AmcContract> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PreAuthorize("hasAuthority(\'Asset.AMC.Create\')")
    @PostMapping
    public ResponseEntity<AmcContract> create(@Valid @RequestBody AmcContract request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PreAuthorize("hasAuthority(\'Asset.AMC.Update\')")
    @PutMapping("/{id}")
    public ResponseEntity<AmcContract> update(@PathVariable UUID id,
                                              @Valid @RequestBody AmcContract request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @PreAuthorize("hasAuthority(\'Asset.AMC.Delete\')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority(\'Asset.AMC.View\')")
    @GetMapping("/asset/{assetId}")
    public ResponseEntity<List<AmcContract>> getByAsset(@PathVariable UUID assetId) {
        return ResponseEntity.ok(service.findByAsset(assetId));
    }

    @PreAuthorize("hasAuthority(\'Asset.AMC.View\')")
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<AmcContract>> getByCategory(@PathVariable UUID categoryId) {
        return ResponseEntity.ok(service.findByCategory(categoryId));
    }

    @PreAuthorize("hasAuthority(\'Asset.AMC.View\')")
    @GetMapping("/renewals")
    public ResponseEntity<List<AmcContract>> getRenewals(
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(service.findRenewals(days));
    }
}
