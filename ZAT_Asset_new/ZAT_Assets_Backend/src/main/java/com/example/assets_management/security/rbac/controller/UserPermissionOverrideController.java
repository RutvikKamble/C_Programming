package com.example.assets_management.security.rbac.controller;

import com.example.assets_management.security.rbac.dto.UserPermissionOverrideRequest;
import com.example.assets_management.security.rbac.dto.UserPermissionOverrideResponse;
import com.example.assets_management.security.rbac.service.UserPermissionOverrideService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Backward-compatible GRANT / DENY endpoints. They now operate on the per-user permission table.
 * Prefer {@code /api/v1/users/{userId}/permissions}.
 */
@RestController
@RequestMapping({"/api/v1/users", "/api/users"})
@RequiredArgsConstructor
public class UserPermissionOverrideController {

    private final UserPermissionOverrideService service;

    @GetMapping("/{userId}/permission-overrides")
    @PreAuthorize("hasAuthority('Asset.User.View') or @currentUserService.requireUserId().equals(#userId)")
    public ResponseEntity<List<UserPermissionOverrideResponse>> get(@PathVariable UUID userId) {
        return ResponseEntity.ok(service.get(userId));
    }

    @PutMapping("/{userId}/permission-overrides/{permissionId}")
    @PreAuthorize("hasAuthority('Asset.User.Manage_Permissions')")
    public ResponseEntity<UserPermissionOverrideResponse> upsert(
            @PathVariable UUID userId,
            @PathVariable UUID permissionId,
            @Valid @RequestBody UserPermissionOverrideRequest req) {
        return ResponseEntity.ok(service.upsert(userId, permissionId, req));
    }

    @DeleteMapping("/{userId}/permission-overrides/{permissionId}")
    @PreAuthorize("hasAuthority('Asset.User.Manage_Permissions')")
    public ResponseEntity<Void> remove(@PathVariable UUID userId, @PathVariable UUID permissionId) {
        service.remove(userId, permissionId);
        return ResponseEntity.noContent().build();
    }
}
