package com.example.assets_management.user_permission.service;

import com.example.assets_management.user_permission.dto.request.UserPermissionAssignRequest;
import com.example.assets_management.user_permission.dto.response.UserPermissionResponse;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface UserPermissionService {

    /** Every permission row of the user: inherited, direct and revoked ones. */
    List<UserPermissionResponse> getUserPermissions(UUID userId);

    /** Only permissions added to this user individually. */
    List<UserPermissionResponse> getUserDirectPermissions(UUID userId);

    /** Permission keys the user can use right now. */
    Set<String> getEffectivePermissionKeys(UUID userId);

    /**
     * Gives ONE user a permission: restores it if it was inherited from the role and removed from the
     * user, otherwise adds it as a DIRECT permission.
     */
    UserPermissionResponse grantPermission(UUID userId, UserPermissionAssignRequest request);

    /**
     * Removes a permission from ONE user: an inherited permission is kept as a revoked marker (so
     * role syncs do not give it back), a direct permission is deleted.
     */
    void revokePermission(UUID userId, UUID permissionId);

    /** Sets the user's effective permissions to exactly {@code permissionIds}. */
    List<UserPermissionResponse> replaceUserPermissions(UUID userId, List<UUID> permissionIds);

    /** Discards the user's customisations and copies the role's current permissions again. */
    List<UserPermissionResponse> resetToRole(UUID userId);
}
