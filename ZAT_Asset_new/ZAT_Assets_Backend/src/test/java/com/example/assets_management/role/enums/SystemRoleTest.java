package com.example.assets_management.role.enums;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class SystemRoleTest {

    @Test
    void shouldContainExactlyFiveSystemRoles() {
        assertEquals(5, SystemRole.values().length);

        assertArrayEquals(
                new SystemRole[]{
                        SystemRole.SUPER_ADMIN,
                        SystemRole.ADMIN,
                        SystemRole.ASSET_MANAGER,
                        SystemRole.DEPARTMENT_HEAD,
                        SystemRole.EMPLOYEE
                },
                SystemRole.values()
        );
    }

    @Test
    void shouldExposeExpectedDisplayNames() {
        assertEquals("Super Admin", SystemRole.SUPER_ADMIN.getDisplayName());
        assertEquals("Admin", SystemRole.ADMIN.getDisplayName());
        assertEquals("Asset Manager", SystemRole.ASSET_MANAGER.getDisplayName());
        assertEquals("Department Head", SystemRole.DEPARTMENT_HEAD.getDisplayName());
        assertEquals("Employee", SystemRole.EMPLOYEE.getDisplayName());
    }

    @Test
    void shouldHaveUniqueDisplayNames() {
        long uniqueNames = Arrays.stream(SystemRole.values())
                .map(SystemRole::getDisplayName)
                .distinct()
                .count();

        assertEquals(SystemRole.values().length, uniqueNames);
    }
}
