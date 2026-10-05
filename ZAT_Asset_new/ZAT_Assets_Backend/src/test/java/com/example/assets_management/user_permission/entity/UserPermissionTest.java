package com.example.assets_management.user_permission.entity;

import com.example.assets_management.employee.entity.Employee;
import com.example.assets_management.permission.entity.Permission;
import com.example.assets_management.user_permission.enums.UserPermissionSource;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserPermissionTest {

    @Test
    void shouldCreateRoleDerivedUserPermission() {
        Employee user = new Employee();
        Permission permission = new Permission();

        UserPermission userPermission = new UserPermission();
        userPermission.setUser(user);
        userPermission.setPermission(permission);
        userPermission.setSource(UserPermissionSource.ROLE);

        assertSame(user, userPermission.getUser());
        assertSame(permission, userPermission.getPermission());
        assertEquals(UserPermissionSource.ROLE, userPermission.getSource());
    }

    @Test
    void shouldDefaultSourceToRole() {
        UserPermission userPermission = new UserPermission();

        assertEquals(UserPermissionSource.ROLE, userPermission.getSource());
    }

    @Test
    void shouldBeEffectiveUntilRevoked() {
        UserPermission userPermission = new UserPermission();

        assertFalse(userPermission.isRevoked());
        assertTrue(userPermission.isEffective());

        userPermission.setRevoked(true);
        assertFalse(userPermission.isEffective());
    }

    @Test
    void shouldHaveUuidId() throws NoSuchFieldException {
        Field idField = UserPermission.class.getDeclaredField("id");

        assertEquals(UUID.class, idField.getType());
    }

    @Test
    void shouldContainRequiredFields() throws Exception {
        assertNotNull(UserPermission.class.getDeclaredField("user"));
        assertNotNull(UserPermission.class.getDeclaredField("permission"));
        assertNotNull(UserPermission.class.getDeclaredField("source"));
        assertNotNull(UserPermission.class.getDeclaredField("createdAt"));
    }
}
