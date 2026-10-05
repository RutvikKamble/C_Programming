package com.example.assets_management.role_permission.entity;

import com.example.assets_management.permission.entity.Permission;
import com.example.assets_management.role.entity.Role;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RolePermissionTest {

    @Test
    void shouldCreateRolePermissionWithRoleAndPermission() {
        Role role = new Role();
        Permission permission = new Permission();

        RolePermission rolePermission = new RolePermission();
        rolePermission.setRole(role);
        rolePermission.setPermission(permission);

        assertSame(role, rolePermission.getRole());
        assertSame(permission, rolePermission.getPermission());
    }

    @Test
    void shouldHaveUuidId() throws NoSuchFieldException {
        Field idField = RolePermission.class.getDeclaredField("id");

        assertEquals(UUID.class, idField.getType());
    }

    @Test
    void shouldContainRequiredRelationshipFields() throws Exception {
        assertNotNull(RolePermission.class.getDeclaredField("role"));
        assertNotNull(RolePermission.class.getDeclaredField("permission"));
        assertNotNull(RolePermission.class.getDeclaredField("assignedAt"));
    }
}
