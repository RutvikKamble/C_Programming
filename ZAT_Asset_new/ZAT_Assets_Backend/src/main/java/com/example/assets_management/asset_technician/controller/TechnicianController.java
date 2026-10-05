package com.example.assets_management.asset_technician.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.example.assets_management.asset_technician.entity.Technician;
import com.example.assets_management.asset_technician.service.TechnicianService;

import com.example.assets_management.asset_technician.dto.request.BulkDeleteRequest;
import com.example.assets_management.asset_technician.dto.request.BulkTechnicianUpdateRequest;
import com.example.assets_management.asset_technician.dto.request.TechnicianPatchDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/technicians")
public class TechnicianController {

    private final TechnicianService technicianService;

    public TechnicianController(TechnicianService technicianService) {
        this.technicianService = technicianService;
    }

    // --- CREATE ---
    @PreAuthorize("hasAuthority(\'Asset.Technician.Create\')")
    @PostMapping
    public ResponseEntity<Technician> createTechnician(@Valid @RequestBody Technician technician) {
        return new ResponseEntity<>(technicianService.createTechnician(technician), HttpStatus.CREATED);
    }

    // --- FULL UPDATE ---
    @PreAuthorize("hasAuthority(\'Asset.Technician.Update\')")
    @PutMapping("/{id}")
    public ResponseEntity<Technician> updateTechnician(@PathVariable Long id,
                                                       @Valid @RequestBody Technician technician) {
        return new ResponseEntity<>(technicianService.updateTechnician(id, technician), HttpStatus.OK);
    }

    // --- PARTIAL UPDATE (PATCH) ---
    @PreAuthorize("hasAuthority(\'Asset.Technician.Update\')")
    @PatchMapping("/{id}")
    public ResponseEntity<Technician> patchTechnician(@PathVariable Long id,
                                                      @Valid @RequestBody TechnicianPatchDTO patchDTO) {
        return new ResponseEntity<>(technicianService.patchTechnician(id, patchDTO), HttpStatus.OK);
    }

    // --- BULK UPDATE (same fields for multiple technicians) ---
    @PreAuthorize("hasAuthority(\'Asset.Technician.Update\')")
    @PatchMapping("/bulk-update")
    public ResponseEntity<List<Technician>> bulkUpdateTechnicians(@Valid @RequestBody BulkTechnicianUpdateRequest request) {
        return new ResponseEntity<>(technicianService.bulkUpdateTechnicians(request), HttpStatus.OK);
    }

    // --- DELETE SINGLE ---
    @PreAuthorize("hasAuthority(\'Asset.Technician.Delete\')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTechnician(@PathVariable Long id) {
        technicianService.deleteTechnician(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // --- BULK DELETE ---
    @PreAuthorize("hasAuthority(\'Asset.Technician.Delete\')")
    @DeleteMapping("/bulk-delete")
    public ResponseEntity<Void> bulkDeleteTechnicians(@Valid @RequestBody BulkDeleteRequest request) {
        technicianService.bulkDeleteTechnicians(request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // --- GET SINGLE ---
    @PreAuthorize("hasAuthority(\'Asset.Technician.View\')")
    @GetMapping("/{id}")
    public ResponseEntity<Technician> getTechnicianById(@PathVariable Long id) {
        return new ResponseEntity<>(technicianService.getTechnicianById(id), HttpStatus.OK);
    }

    // --- GET ALL ---
    @PreAuthorize("hasAuthority(\'Asset.Technician.View\')")
    @GetMapping
    public ResponseEntity<List<Technician>> getAllTechnicians() {
        return new ResponseEntity<>(technicianService.getAllTechnicians(), HttpStatus.OK);
    }

    // --- GET BY INTERNAL FLAG ---
    // URL FOR TEST API : http://localhost:8080/api/technicians/internal?isInternal=true
    @PreAuthorize("hasAuthority(\'Asset.Technician.View\')")
    @GetMapping("/internal")
    public ResponseEntity<List<Technician>> getTechniciansByInternal(@RequestParam Boolean isInternal) {
        return new ResponseEntity<>(technicianService.getTechniciansByInternalFlag(isInternal), HttpStatus.OK);
    }
}
