package com.example.assets_management.role.entity;

import com.example.assets_management.role.enums.RoleStatus;
import com.example.assets_management.role.enums.RoleType;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    void shouldDefaultToCustomActiveRole() {
        Role role = new Role();

        assertEquals(RoleType.CUSTOM, role.getType());
        assertEquals(RoleStatus.ACTIVE, role.getStatus());
        assertFalse(role.isSystemDefined());
    }

    @Test
    void shouldSynchronizeSystemRoleFlag() throws Exception {
        Role role = new Role();
        role.setName("System Test Role");
        role.setType(RoleType.SYSTEM);
        role.setStatus(RoleStatus.ACTIVE);

        invokeLifecycleMethod(role);

        assertEquals(RoleType.SYSTEM, role.getType());
        assertTrue(role.isSystemDefined());
    }

    @Test
    void shouldKeepCustomRoleAsNotSystemDefined() throws Exception {
        Role role = new Role();
        role.setName("Custom Test Role");
        role.setType(RoleType.CUSTOM);
        role.setStatus(RoleStatus.ACTIVE);

        invokeLifecycleMethod(role);

        assertEquals(RoleType.CUSTOM, role.getType());
        assertFalse(role.isSystemDefined());
    }

    @Test
    void shouldAllowInactiveRole() {
        Role role = new Role();

        role.setStatus(RoleStatus.INACTIVE);

        assertEquals(RoleStatus.INACTIVE, role.getStatus());
    }

    @Test
    void shouldHaveUuidId() throws NoSuchFieldException {
        Field idField = Role.class.getDeclaredField("id");

        assertEquals(UUID.class, idField.getType());
    }

    @Test
    void shouldContainRequiredRoleFields() throws Exception {
        assertNotNull(Role.class.getDeclaredField("name"));
        assertNotNull(Role.class.getDeclaredField("description"));
        assertNotNull(Role.class.getDeclaredField("type"));
        assertNotNull(Role.class.getDeclaredField("status"));
        assertNotNull(Role.class.getDeclaredField("systemDefined"));
        assertNotNull(Role.class.getDeclaredField("createdAt"));
        assertNotNull(Role.class.getDeclaredField("updatedAt"));
    }

    private void invokeLifecycleMethod(Role role) throws Exception {
        var method = Role.class.getDeclaredMethod("sync");
        method.setAccessible(true);
        method.invoke(role);
    }
}
