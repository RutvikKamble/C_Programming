package com.example.assets_management.user_permission.service;

import com.example.assets_management.employee.entity.Employee;
import com.example.assets_management.employee.repository.EmployeeRepository;
import com.example.assets_management.permission.entity.Permission;
import com.example.assets_management.permission.enums.PermissionStatus;
import com.example.assets_management.permission.repository.PermissionRepository;
import com.example.assets_management.user_permission.dto.request.UserPermissionAssignRequest;
import com.example.assets_management.user_permission.entity.UserPermission;
import com.example.assets_management.user_permission.enums.UserPermissionSource;
import com.example.assets_management.user_permission.repository.UserPermissionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserPermissionServiceTest {

    @Mock
    private UserPermissionRepository userPermissionRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private UserPermissionSyncService syncService;

    @InjectMocks
    private UserPermissionServiceImpl service;

    private Permission permission(UUID id, PermissionStatus status) {
        Permission p = new Permission();
        p.setId(id);
        p.setPermissionKey("Asset.Asset_Master.Create");
        p.setStatus(status);
        return p;
    }

    private Employee user(UUID id) {
        Employee e = new Employee();
        e.setId(id);
        return e;
    }

    @Test
    void shouldAddDirectPermissionToUserUuid() {
        UUID userId = UUID.randomUUID(), permissionId = UUID.randomUUID();
        Employee user = user(userId);
        Permission permission = permission(permissionId, PermissionStatus.ACTIVE);

        when(employeeRepository.findById(userId)).thenReturn(Optional.of(user));
        when(permissionRepository.findById(permissionId)).thenReturn(Optional.of(permission));
        when(userPermissionRepository.findByUser_IdAndPermission_Id(userId, permissionId)).thenReturn(Optional.empty());
        when(userPermissionRepository.save(any(UserPermission.class))).thenAnswer(i -> i.getArgument(0));

        var response = service.grantPermission(userId, new UserPermissionAssignRequest(permissionId));

        assertEquals(permissionId, response.permissionId());
        assertEquals(UserPermissionSource.DIRECT, response.source());
        assertFalse(response.revoked());
    }

    @Test
    void shouldRejectInactivePermission() {
        UUID userId = UUID.randomUUID(), permissionId = UUID.randomUUID();
        when(employeeRepository.findById(userId)).thenReturn(Optional.of(user(userId)));
        when(permissionRepository.findById(permissionId))
                .thenReturn(Optional.of(permission(permissionId, PermissionStatus.INACTIVE)));

        assertThrows(IllegalStateException.class,
                () -> service.grantPermission(userId, new UserPermissionAssignRequest(permissionId)));
        verify(userPermissionRepository, never()).save(any(UserPermission.class));
    }

    @Test
    void shouldRejectPermissionTheUserAlreadyHas() {
        UUID userId = UUID.randomUUID(), permissionId = UUID.randomUUID();
        Permission permission = permission(permissionId, PermissionStatus.ACTIVE);
        UserPermission existing = new UserPermission();
        existing.setPermission(permission);
        existing.setSource(UserPermissionSource.ROLE);

        when(employeeRepository.findById(userId)).thenReturn(Optional.of(user(userId)));
        when(permissionRepository.findById(permissionId)).thenReturn(Optional.of(permission));
        when(userPermissionRepository.findByUser_IdAndPermission_Id(userId, permissionId)).thenReturn(Optional.of(existing));

        assertThrows(IllegalStateException.class,
                () -> service.grantPermission(userId, new UserPermissionAssignRequest(permissionId)));
        verify(userPermissionRepository, never()).save(any(UserPermission.class));
    }

    @Test
    void grantingARevokedInheritedPermissionRestoresIt() {
        UUID userId = UUID.randomUUID(), permissionId = UUID.randomUUID();
        Employee user = user(userId);
        Permission permission = permission(permissionId, PermissionStatus.ACTIVE);
        UserPermission row = new UserPermission();
        row.setUser(user);
        row.setPermission(permission);
        row.setSource(UserPermissionSource.ROLE);
        row.setRevoked(true);

        when(employeeRepository.findById(userId)).thenReturn(Optional.of(user));
        when(permissionRepository.findById(permissionId)).thenReturn(Optional.of(permission));
        when(userPermissionRepository.findByUser_IdAndPermission_Id(userId, permissionId)).thenReturn(Optional.of(row));
        when(userPermissionRepository.save(row)).thenReturn(row);

        var response = service.grantPermission(userId, new UserPermissionAssignRequest(permissionId));

        assertFalse(row.isRevoked());
        assertEquals(UserPermissionSource.ROLE, response.source());
    }

    @Test
    void revokingAnInheritedPermissionKeepsARevokedMarker() {
        UUID userId = UUID.randomUUID(), permissionId = UUID.randomUUID();
        Employee user = user(userId);
        UserPermission row = new UserPermission();
        row.setUser(user);
        row.setPermission(permission(permissionId, PermissionStatus.ACTIVE));
        row.setSource(UserPermissionSource.ROLE);

        when(employeeRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userPermissionRepository.findByUser_IdAndPermission_Id(userId, permissionId)).thenReturn(Optional.of(row));

        service.revokePermission(userId, permissionId);

        assertTrue(row.isRevoked());
        verify(userPermissionRepository).save(row);
        verify(userPermissionRepository, never()).delete(any(UserPermission.class));
    }

    @Test
    void revokingADirectPermissionDeletesTheRow() {
        UUID userId = UUID.randomUUID(), permissionId = UUID.randomUUID();
        Employee user = user(userId);
        UserPermission row = new UserPermission();
        row.setUser(user);
        row.setPermission(permission(permissionId, PermissionStatus.ACTIVE));
        row.setSource(UserPermissionSource.DIRECT);

        when(employeeRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userPermissionRepository.findByUser_IdAndPermission_Id(userId, permissionId)).thenReturn(Optional.of(row));

        service.revokePermission(userId, permissionId);

        verify(userPermissionRepository).delete(row);
    }

    @Test
    void revokingAPermissionTheUserDoesNotHoldFails() {
        UUID userId = UUID.randomUUID(), permissionId = UUID.randomUUID();
        when(employeeRepository.findById(userId)).thenReturn(Optional.of(user(userId)));
        when(userPermissionRepository.findByUser_IdAndPermission_Id(userId, permissionId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.revokePermission(userId, permissionId));
    }

    @Test
    void resetToRoleCopiesTheRolePermissionsAgain() {
        UUID userId = UUID.randomUUID();
        Employee user = user(userId);
        when(employeeRepository.findById(userId)).thenReturn(Optional.of(user));
        when(syncService.assignFromRole(user)).thenReturn(3);
        when(userPermissionRepository.findByUser_Id(userId)).thenReturn(List.of());

        assertTrue(service.resetToRole(userId).isEmpty());
        verify(syncService).assignFromRole(user);
    }
}
