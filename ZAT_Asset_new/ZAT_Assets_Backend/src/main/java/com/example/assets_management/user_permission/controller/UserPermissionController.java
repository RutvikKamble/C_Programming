package com.example.assets_management.user_permission.controller;

import com.example.assets_management.user_permission.dto.request.UserPermissionAssignRequest;
import com.example.assets_management.user_permission.dto.request.UserPermissionReplaceRequest;
import com.example.assets_management.user_permission.dto.response.UserPermissionResponse;
import com.example.assets_management.user_permission.service.UserPermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Per-user permissions (permissions keyed on the user's UUID).
 *
 * <p>A user's permission set is seeded from the role when it is assigned, kept in step when the
 * role's permissions change, and can be customised here for one user without touching the role or
 * any other user of that role.
 */
@RestController
@RequestMapping({"/api/users", "/api/v1/users"})
@RequiredArgsConstructor
public class UserPermissionController {

    private final UserPermissionService userPermissionService;

    /** All permission rows of the user (inherited / direct / revoked). */
    @GetMapping("/{userId}/permissions")
    @PreAuthorize("hasAuthority('Asset.User.View') or @currentUserService.requireUserId().equals(#userId)")
    public ResponseEntity<List<UserPermissionResponse>> getUserPermissions(@PathVariable UUID userId) {
        return ResponseEntity.ok(userPermissionService.getUserPermissions(userId));
    }

    /** Permission keys the user can use right now. */
    @GetMapping("/{userId}/effective-permissions")
    @PreAuthorize("hasAuthority('Asset.User.View') or @currentUserService.requireUserId().equals(#userId)")
    public ResponseEntity<Set<String>> getEffectivePermissions(@PathVariable UUID userId) {
        return ResponseEntity.ok(userPermissionService.getEffectivePermissionKeys(userId));
    }

    @GetMapping("/{userId}/permissions/direct")
    @PreAuthorize("hasAuthority('Asset.User.Manage_Permissions')")
    public ResponseEntity<List<UserPermissionResponse>> getUserDirectPermissions(@PathVariable UUID userId) {
        return ResponseEntity.ok(userPermissionService.getUserDirectPermissions(userId));
    }

    /** Add one permission to this user only. */
    @PostMapping("/{userId}/permissions")
    @PreAuthorize("hasAuthority('Asset.User.Manage_Permissions')")
    public ResponseEntity<UserPermissionResponse> grantPermission(
            @PathVariable UUID userId,
            @Valid @RequestBody UserPermissionAssignRequest request) {
        return ResponseEntity.ok(userPermissionService.grantPermission(userId, request));
    }

    /** Set this user's permissions to exactly the given list (checkbox-style editing). */
    @PutMapping("/{userId}/permissions")
    @PreAuthorize("hasAuthority('Asset.User.Manage_Permissions')")
    public ResponseEntity<List<UserPermissionResponse>> replacePermissions(
            @PathVariable UUID userId,
            @Valid @RequestBody UserPermissionReplaceRequest request) {
        return ResponseEntity.ok(userPermissionService.replaceUserPermissions(userId, request.permissionIds()));
    }

    /** Remove one permission from this user only. */
    @DeleteMapping("/{userId}/permissions/{permissionId}")
    @PreAuthorize("hasAuthority('Asset.User.Manage_Permissions')")
    public ResponseEntity<String> revokePermission(
            @PathVariable UUID userId,
            @PathVariable UUID permissionId) {
        userPermissionService.revokePermission(userId, permissionId);
        return ResponseEntity.ok("Permission removed from user successfully");
    }

    /** Throw away this user's customisations and re-copy the role's permissions. */
    @PostMapping("/{userId}/permissions/reset-to-role")
    @PreAuthorize("hasAuthority('Asset.User.Manage_Permissions')")
    public ResponseEntity<List<UserPermissionResponse>> resetToRole(@PathVariable UUID userId) {
        return ResponseEntity.ok(userPermissionService.resetToRole(userId));
    }
}
