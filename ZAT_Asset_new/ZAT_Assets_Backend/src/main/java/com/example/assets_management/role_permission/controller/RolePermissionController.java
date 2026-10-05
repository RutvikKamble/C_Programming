package com.example.assets_management.role_permission.controller;

import com.example.assets_management.role_permission.dto.request.RolePermissionAssignRequest;
import com.example.assets_management.role_permission.dto.request.RolePermissionReplaceRequest;
import com.example.assets_management.role_permission.dto.response.RolePermissionResponse;
import com.example.assets_management.role_permission.service.RolePermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping({"/api/roles", "/api/v1/roles"})
@RequiredArgsConstructor
public class RolePermissionController {
    private final RolePermissionService rolePermissionService;

    @GetMapping("/{roleId}/permissions")
    @PreAuthorize("hasAuthority('Asset.Role.View')")
    public ResponseEntity<List<RolePermissionResponse>> getRolePermissions(@PathVariable UUID roleId) {
        return ResponseEntity.ok(rolePermissionService.getRolePermissions(roleId));
    }

    @PatchMapping("/{roleId}/permissions")
    @PreAuthorize("hasAuthority('Asset.Role.Assign_Permissions')")
    public ResponseEntity<List<RolePermissionResponse>> replacePermissions(
            @PathVariable UUID roleId,
            @Valid @RequestBody RolePermissionReplaceRequest request) {
        return ResponseEntity.ok(rolePermissionService.replacePermissions(roleId, request.permissionIds()));
    }

    @PostMapping("/{roleId}/permissions")
    @PreAuthorize("hasAuthority('Asset.Role.Assign_Permissions')")
    public ResponseEntity<RolePermissionResponse> addPermissionToRole(
            @PathVariable UUID roleId,
            @Valid @RequestBody RolePermissionAssignRequest request) {
        return ResponseEntity.ok(rolePermissionService.addPermissionToRole(roleId, request));
    }

    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    @PreAuthorize("hasAuthority('Asset.Role.Assign_Permissions')")
    public ResponseEntity<String> removePermissionFromRole(
            @PathVariable UUID roleId,
            @PathVariable UUID permissionId) {
        rolePermissionService.removePermissionFromRole(roleId, permissionId);
        return ResponseEntity.ok("Permission removed from role successfully");
    }
}
