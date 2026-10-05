package com.example.assets_management.permission.entity;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PermissionTest {

    @Test
    void shouldCreatePermissionWithCanonicalFields() {
        Permission permission = new Permission();
        permission.setPermissionKey("Asset.Asset_Master.Create");
        permission.setDescription("Create asset");

        assertEquals(
                "Asset.Asset_Master.Create",
                permission.getPermissionKey()
        );
        assertEquals("Create asset", permission.getDescription());
    }

    @Test
    void shouldHaveUuidId() throws NoSuchFieldException {
        Field idField = Permission.class.getDeclaredField("id");

        assertEquals(UUID.class, idField.getType());
    }

    @Test
    void shouldContainRequiredPermissionFields() throws Exception {
        assertNotNull(Permission.class.getDeclaredField("permissionKey"));
        assertNotNull(Permission.class.getDeclaredField("application"));
        assertNotNull(Permission.class.getDeclaredField("module"));
        assertNotNull(Permission.class.getDeclaredField("action"));
        assertNotNull(Permission.class.getDeclaredField("description"));
        assertNotNull(Permission.class.getDeclaredField("createdAt"));
        assertNotNull(Permission.class.getDeclaredField("updatedAt"));
    }

    @Test
    void shouldSynchronizeApplicationModuleAndActionBeforePersist() throws Exception {
        Permission permission = new Permission();

        permission.setPermissionKey("Asset.Asset_Master.Create");

        var method = Permission.class
                .getDeclaredMethod("validateAndSynchronizePermissionKey");

        method.setAccessible(true);
        method.invoke(permission);

        assertEquals("Asset", permission.getApplication());
        assertEquals("Asset_Master", permission.getModule());
        assertEquals("Create", permission.getAction());
    }

}
