package com.example.assets_management.security.rbac.service;

import com.example.assets_management.security.rbac.dto.UserPermissionOverrideRequest;
import com.example.assets_management.security.rbac.dto.UserPermissionOverrideResponse;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Backward-compatible GRANT / DENY view over the per-user permission table
 * ({@code /permission-overrides} endpoints). New code should use the
 * {@code /users/{id}/permissions} API instead.
 *
 * <ul>
 *   <li>GRANT = give this user a permission (restore an inherited one or add a direct one)</li>
 *   <li>DENY  = take a permission away from this user only</li>
 * </ul>
 */
public interface UserPermissionOverrideService {
    List<UserPermissionOverrideResponse> get(UUID userId);

    UserPermissionOverrideResponse upsert(UUID userId, UUID permissionId, UserPermissionOverrideRequest request);

    void remove(UUID userId, UUID permissionId);

    Set<String> effective(UUID userId);
}
