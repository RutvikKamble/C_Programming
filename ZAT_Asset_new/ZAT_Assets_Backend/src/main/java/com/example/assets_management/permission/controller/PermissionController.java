package com.example.assets_management.permission.controller;

import com.example.assets_management.permission.dto.PermissionCreateRequest;
import com.example.assets_management.permission.dto.PermissionResponse;
import com.example.assets_management.permission.dto.PermissionUpdateRequest;
import com.example.assets_management.permission.entity.Permission;
import com.example.assets_management.permission.repository.PermissionRepository;
import com.example.assets_management.permission.service.PermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Permission catalog. Permission keys follow {@code application.module.action}
 * (stored as {@code Asset.Module.Action}, e.g. {@code Asset.Asset_Master.Create}).
 */
@RestController
@RequestMapping({"/api/v1/permissions", "/api/permissions"})
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionRepository repo;
    private final PermissionService permissionService;

    /** Full permission entities (kept for existing clients). */
    @GetMapping
    @PreAuthorize("hasAuthority('Asset.Permission.View')")
    public ResponseEntity<List<Permission>> all() {
        return ResponseEntity.ok(repo.findAll());
    }

    /** module -> permissions, convenient for building a permission picker. */
    @GetMapping("/grouped")
    @PreAuthorize("hasAuthority('Asset.Permission.View')")
    public ResponseEntity<Map<String, List<PermissionResponse>>> grouped() {
        return ResponseEntity.ok(permissionService.getGroupedByModule());
    }

    @GetMapping("/{permissionId}")
    @PreAuthorize("hasAuthority('Asset.Permission.View')")
    public ResponseEntity<PermissionResponse> getById(@PathVariable UUID permissionId) {
        return ResponseEntity.ok(permissionService.getById(permissionId));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('Asset.Permission.Manage')")
    public ResponseEntity<PermissionResponse> create(@Valid @RequestBody PermissionCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(permissionService.create(request));
    }

    @PutMapping("/{permissionId}")
    @PreAuthorize("hasAuthority('Asset.Permission.Manage')")
    public ResponseEntity<PermissionResponse> update(@PathVariable UUID permissionId,
                                                     @Valid @RequestBody PermissionUpdateRequest request) {
        return ResponseEntity.ok(permissionService.update(permissionId, request));
    }

    @PatchMapping("/{permissionId}/activate")
    @PreAuthorize("hasAuthority('Asset.Permission.Manage')")
    public ResponseEntity<PermissionResponse> activate(@PathVariable UUID permissionId) {
        return ResponseEntity.ok(permissionService.activate(permissionId));
    }

    @PatchMapping("/{permissionId}/deactivate")
    @PreAuthorize("hasAuthority('Asset.Permission.Manage')")
    public ResponseEntity<PermissionResponse> deactivate(@PathVariable UUID permissionId) {
        return ResponseEntity.ok(permissionService.deactivate(permissionId));
    }

    @DeleteMapping("/{permissionId}")
    @PreAuthorize("hasAuthority('Asset.Permission.Manage')")
    public ResponseEntity<String> delete(@PathVariable UUID permissionId) {
        permissionService.delete(permissionId);
        return ResponseEntity.ok("Permission deleted successfully");
    }
}
