package com.example.assets_management.role_permission.service;

import com.example.assets_management.permission.entity.Permission;
import com.example.assets_management.permission.enums.PermissionStatus;
import com.example.assets_management.permission.repository.PermissionRepository;
import com.example.assets_management.role.entity.Role;
import com.example.assets_management.role.enums.RoleStatus;
import com.example.assets_management.role.repository.RoleRepository;
import com.example.assets_management.role_permission.dto.request.RolePermissionAssignRequest;
import com.example.assets_management.role_permission.entity.RolePermission;
import com.example.assets_management.role_permission.repository.RolePermissionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RolePermissionServiceTest {

    @Mock
    private RolePermissionRepository rolePermissionRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @InjectMocks
    private RolePermissionServiceImpl service;

    @Test
    void shouldAddActivePermissionToActiveRole() {
        UUID roleId = UUID.randomUUID();
        UUID permissionId = UUID.randomUUID();

        Role role = new Role();
        role.setId(roleId);
        role.setName("Asset Manager");
        role.setStatus(RoleStatus.ACTIVE);

        Permission permission = new Permission();
        permission.setId(permissionId);
        permission.setPermissionKey("Asset.Asset_Master.Create");
        permission.setStatus(PermissionStatus.ACTIVE);

        RolePermission saved = new RolePermission();
        saved.setRole(role);
        saved.setPermission(permission);

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(permissionRepository.findById(permissionId)).thenReturn(Optional.of(permission));
        when(rolePermissionRepository.existsByRole_IdAndPermission_Id(roleId, permissionId))
                .thenReturn(false);
        when(rolePermissionRepository.save(any(RolePermission.class))).thenReturn(saved);

        var response = service.addPermissionToRole(
                roleId,
                new RolePermissionAssignRequest(permissionId)
        );

        assertEquals("Asset Manager", response.roleName());
        assertEquals("Asset.Asset_Master.Create", response.permissionKey());
        assertEquals(PermissionStatus.ACTIVE, response.permissionStatus());

        verify(rolePermissionRepository).existsByRole_IdAndPermission_Id(roleId, permissionId);
        verify(rolePermissionRepository).save(any(RolePermission.class));
    }

    @Test
    void shouldRejectDuplicatePermissionAssignment() {
        UUID roleId = UUID.randomUUID();
        UUID permissionId = UUID.randomUUID();

        Role role = new Role();
        role.setId(roleId);
        role.setStatus(RoleStatus.ACTIVE);

        Permission permission = new Permission();
        permission.setId(permissionId);
        permission.setStatus(PermissionStatus.ACTIVE);

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(permissionRepository.findById(permissionId)).thenReturn(Optional.of(permission));
        when(rolePermissionRepository.existsByRole_IdAndPermission_Id(roleId, permissionId))
                .thenReturn(true);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.addPermissionToRole(
                        roleId,
                        new RolePermissionAssignRequest(permissionId)
                )
        );

        assertEquals("Permission is already assigned to this role", exception.getMessage());
        verify(rolePermissionRepository, never()).save(any(RolePermission.class));
    }

    @Test
    void shouldRejectInactivePermission() {
        UUID roleId = UUID.randomUUID();
        UUID permissionId = UUID.randomUUID();

        Role role = new Role();
        role.setId(roleId);
        role.setStatus(RoleStatus.ACTIVE);

        Permission permission = new Permission();
        permission.setId(permissionId);
        permission.setStatus(PermissionStatus.INACTIVE);

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(permissionRepository.findById(permissionId)).thenReturn(Optional.of(permission));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.addPermissionToRole(
                        roleId,
                        new RolePermissionAssignRequest(permissionId)
                )
        );

        assertEquals("Inactive permission cannot be assigned to a role", exception.getMessage());
        verify(rolePermissionRepository, never()).save(any(RolePermission.class));
    }

    @Test
    void shouldRejectInactiveRole() {
        UUID roleId = UUID.randomUUID();

        Role role = new Role();
        role.setId(roleId);
        role.setStatus(RoleStatus.INACTIVE);

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.addPermissionToRole(
                        roleId,
                        new RolePermissionAssignRequest(UUID.randomUUID())
                )
        );

        assertEquals("Inactive role cannot receive permissions", exception.getMessage());
        verify(permissionRepository, never()).findById(any(UUID.class));
        verify(rolePermissionRepository, never()).save(any(RolePermission.class));
    }

    @Test
    void shouldRemoveAssignedPermission() {
        UUID roleId = UUID.randomUUID();
        UUID permissionId = UUID.randomUUID();

        Role role = new Role();
        role.setId(roleId);

        RolePermission rolePermission = new RolePermission();
        rolePermission.setRole(role);

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(rolePermissionRepository.findByRole_IdAndPermission_Id(roleId, permissionId))
                .thenReturn(Optional.of(rolePermission));

        service.removePermissionFromRole(roleId, permissionId);

        verify(rolePermissionRepository).delete(rolePermission);
    }
}
