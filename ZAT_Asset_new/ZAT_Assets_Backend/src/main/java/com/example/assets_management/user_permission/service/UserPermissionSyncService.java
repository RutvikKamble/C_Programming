package com.example.assets_management.user_permission.service;

import com.example.assets_management.employee.entity.Employee;

import java.util.UUID;

/**
 * Keeps the per-user {@code user_permissions} table (permissions keyed on the user's UUID) in step
 * with role assignments and role permission edits.
 */
public interface UserPermissionSyncService {

    /** Outcome of {@link #syncRoleUsers(UUID)}. */
    record RoleSyncResult(int usersProcessed, int permissionsAdded, int permissionsRemoved) {
    }

    /**
     * Role assignment: discards every permission row of the user and copies the user's current role
     * permissions onto the user's UUID. Returns the number of permissions copied.
     */
    int assignFromRole(Employee user);

    /**
     * Role permission change: reconciles every user holding the role with the role's current
     * permissions. Newly added role permissions are given to all of them, removed ones are taken
     * away from all of them, and per-user customisations (DIRECT additions, revoked markers) are kept.
     */
    RoleSyncResult syncRoleUsers(UUID roleId);

    /** Removes all permission rows of a user (user deletion). */
    void clearUser(UUID userId);

    /** Removes every user row referencing a permission (permission deletion). */
    void removePermissionEverywhere(UUID permissionId);

    /** Startup backfill: users with a role but no permission rows get their role's permissions. */
    int backfillMissingUsers();
}
