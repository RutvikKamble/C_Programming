package com.example.assets_management.asset_software.controller;

import com.example.assets_management.asset_software.dto.request.SoftwareLicenseCreateRequest;
import com.example.assets_management.asset_software.dto.request.SoftwareLicenseUpdateRequest;
import com.example.assets_management.asset_software.dto.response.SoftwareLicenseResponse;
import com.example.assets_management.asset_software.enums.SoftwareLicenseStatus;
import com.example.assets_management.asset_software.service.SoftwareLicenseService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/software-licenses")
public class SoftwareLicenseController {
    private final SoftwareLicenseService service;

    public SoftwareLicenseController(SoftwareLicenseService service) {
        this.service = service;
    }

    @PreAuthorize("hasAuthority('Asset.Software_Asset.Create')")
    @PostMapping
    public ResponseEntity<SoftwareLicenseResponse> create(@Valid @RequestBody SoftwareLicenseCreateRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(r));
    }

    @PreAuthorize("hasAuthority('Asset.Software_Asset.View')")
    @GetMapping
    public ResponseEntity<List<SoftwareLicenseResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @PreAuthorize("hasAuthority('Asset.Software_Asset.View')")
    @GetMapping("/search")
    public ResponseEntity<Page<SoftwareLicenseResponse>> search(@RequestParam(required = false) String search, @RequestParam(required = false) String publisher, @RequestParam(required = false) String category, @RequestParam(required = false) SoftwareLicenseStatus status, @RequestParam(required = false) Boolean maintained, @RequestParam(required = false) Boolean reassignable, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "expiryDate") String sort, @RequestParam(defaultValue = "asc") String order) {
        if (page < 0 || size < 1 || size > 200)
            throw new IllegalArgumentException("page must be >=0 and size must be between 1 and 200");
        Sort.Direction d = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return ResponseEntity.ok(service.search(search, publisher, category, status, maintained, reassignable, PageRequest.of(page, size, Sort.by(d, sort))));
    }

    @PreAuthorize("hasAuthority('Asset.Software_Asset.View')")
    @GetMapping("/expiring-soon")
    public ResponseEntity<List<SoftwareLicenseResponse>> expiringSoon(@RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(service.findExpiringSoon(days));
    }

    @PreAuthorize("hasAuthority('Asset.Software_Asset.View')")
    @GetMapping("/asset/{assetId}")
    public ResponseEntity<SoftwareLicenseResponse> findByAssetId(@PathVariable UUID assetId) {
        return ResponseEntity.ok(service.findByAssetId(assetId));
    }

    @PreAuthorize("hasAuthority('Asset.Software_Asset.View')")
    @GetMapping("/{id}")
    public ResponseEntity<SoftwareLicenseResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PreAuthorize("hasAuthority('Asset.Software_Asset.Update')")
    @PatchMapping("/{id}")
    public ResponseEntity<SoftwareLicenseResponse> update(@PathVariable UUID id, @Valid @RequestBody SoftwareLicenseUpdateRequest r) {
        return ResponseEntity.ok(service.update(id, r));
    }

    @PreAuthorize("hasAuthority('Asset.Software_Asset.View')")
    @GetMapping(value = "/export", produces = "text/csv")
    public ResponseEntity<byte[]> export() {
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=software_licenses.csv").body(service.exportCsv());
    }

    @PreAuthorize("hasAuthority('Asset.Software_Asset.Manage')")
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> importCsv(@RequestPart("file") MultipartFile file, @RequestParam(required = false) String changedBy) throws Exception {
        if (file.isEmpty()) throw new IllegalArgumentException("CSV file is empty");
        return ResponseEntity.status(HttpStatus.CREATED).body("Imported software licenses: " + service.importCsv(file.getBytes(), changedBy));
    }
}
