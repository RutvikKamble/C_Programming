package com.example.assets_management.role.enums;

import com.example.assets_management.role.entity.Role;

/**
 * System-defined roles provided by ZAT Asset Management.
 *
 * These roles are seeded in Phase 6 and are protected from deletion
 * by the role-management rules introduced in later phases.
 */
public enum SystemRole {

    SUPER_ADMIN("Super Admin"),
    ADMIN("Admin"),
    ASSET_MANAGER("Asset Manager"),
    DEPARTMENT_HEAD("Department Head"),
    EMPLOYEE("Employee");

    private final String displayName;

    SystemRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /** Stable role code stored in {@code roles.code} (e.g. {@code ASSET_MANAGER}). */
    public String getCode() {
        return name();
    }

    /** True when the role is the built-in Super Admin role. */
    public static boolean isSuperAdmin(Role role) {
        return role != null
                && role.getType() == RoleType.SYSTEM
                && (SUPER_ADMIN.getCode().equalsIgnoreCase(role.getCode())
                || SUPER_ADMIN.getDisplayName().equalsIgnoreCase(role.getName()));
    }
}
