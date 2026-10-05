package com.example.assets_management.security.rbac.service;

import com.example.assets_management.user_permission.repository.UserPermissionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EffectivePermissionServiceTest {

    @Mock
    UserPermissionRepository userPermissions;

    @InjectMocks
    EffectivePermissionService service;

    @Test
    void effectivePermissionsComeFromTheUsersOwnPermissionRows() {
        UUID id = UUID.randomUUID();
        when(userPermissions.findEffectivePermissionKeys(id))
                .thenReturn(Set.of("Asset.Asset_Master.View", "Asset.Report.Export"));

        assertEquals(Set.of("Asset.Asset_Master.View", "Asset.Report.Export"), service.getEffectivePermissions(id));
        assertTrue(service.hasPermission(id, "Asset.Report.Export"));
        assertFalse(service.hasPermission(id, "Asset.Disposal.Initiate"));
    }

    @Test
    void returnedSetIsReadOnly() {
        UUID id = UUID.randomUUID();
        when(userPermissions.findEffectivePermissionKeys(id)).thenReturn(Set.of("Asset.Asset_Master.View"));

        assertThrows(UnsupportedOperationException.class,
                () -> service.getEffectivePermissions(id).add("Asset.Role.Create"));
    }
}
