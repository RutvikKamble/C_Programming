package com.example.assets_management.security.rbac.seed;

import com.example.assets_management.permission.util.PermissionKeyValidator;
import com.example.assets_management.role.enums.SystemRole;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RbacDefaultsTest {

    @Test
    void everyCatalogKeyUsesTheCanonicalFormat() {
        List<String> keys = RbacDefaults.allKeys();
        assertFalse(keys.isEmpty());
        keys.forEach(k -> assertTrue(PermissionKeyValidator.isValid(k), k));
        assertEquals(keys.size(), new HashSet<>(keys).size(), "duplicate permission keys");
    }

    @Test
    void everyBuiltInRoleHasPermissionsAndOnlyKnownOnes() {
        Set<String> all = new HashSet<>(RbacDefaults.allKeys());
        Map<SystemRole, Set<String>> defaults = RbacDefaults.defaultRolePermissions();

        for (SystemRole role : SystemRole.values()) {
            assertFalse(defaults.get(role).isEmpty(), role + " has no default permissions");
            assertTrue(all.containsAll(defaults.get(role)), role + " references unknown permissions");
        }
        assertEquals(all, defaults.get(SystemRole.SUPER_ADMIN));
        assertEquals(all, defaults.get(SystemRole.ADMIN));
    }

    @Test
    void lowerRolesAreLeastPrivilege() {
        Map<SystemRole, Set<String>> defaults = RbacDefaults.defaultRolePermissions();
        Set<String> employee = defaults.get(SystemRole.EMPLOYEE);

        assertTrue(employee.contains("Asset.Asset_Request.Create"));
        assertFalse(employee.contains("Asset.Role.Create"));
        assertFalse(employee.contains("Asset.User.Manage_Permissions"));
        assertFalse(defaults.get(SystemRole.ASSET_MANAGER).contains("Asset.Role.Assign_Permissions"));
        assertTrue(defaults.get(SystemRole.ADMIN).contains("Asset.Role.Create"));
    }
}
