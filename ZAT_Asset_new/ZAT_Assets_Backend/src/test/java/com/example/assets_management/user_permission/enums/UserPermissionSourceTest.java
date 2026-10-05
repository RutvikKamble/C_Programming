package com.example.assets_management.user_permission.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserPermissionSourceTest {

    @Test
    void shouldContainRoleAndDirectSources() {
        assertArrayEquals(
                new UserPermissionSource[]{
                        UserPermissionSource.ROLE,
                        UserPermissionSource.DIRECT
                },
                UserPermissionSource.values()
        );
    }
}
