package com.example.assets_management.user_permission.enums;

/**
 * Identifies how a permission became available to a user.
 *
 * ROLE:
 *     Permission was inherited from the user's assigned role.
 *
 * DIRECT:
 *     Permission was explicitly assigned to the user UUID.
 *
 * Phase 9 creates only ROLE-derived records. DIRECT permissions are
 * implemented in a later phase.
 */
public enum UserPermissionSource {
    ROLE,
    DIRECT
}
