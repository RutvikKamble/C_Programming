package com.example.assets_management.user_permission.service;

import com.example.assets_management.employee.entity.Employee;
import com.example.assets_management.employee.repository.EmployeeRepository;
import com.example.assets_management.permission.entity.Permission;
import com.example.assets_management.role.entity.Role;
import com.example.assets_management.role_permission.repository.RolePermissionRepository;
import com.example.assets_management.user_permission.entity.UserPermission;
import com.example.assets_management.user_permission.enums.UserPermissionSource;
import com.example.assets_management.user_permission.repository.UserPermissionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserPermissionSyncServiceTest {

    @Mock
    UserPermissionRepository userPermissionRepository;
    @Mock
    RolePermissionRepository rolePermissionRepository;
    @Mock
    EmployeeRepository employeeRepository;

    @InjectMocks
    UserPermissionSyncServiceImpl service;

    private Permission permission(String key) {
        Permission p = new Permission();
        p.setId(UUID.randomUUID());
        p.setPermissionKey(key);
        return p;
    }

    private Employee user(Role role) {
        Employee e = new Employee();
        e.setId(UUID.randomUUID());
        e.setRole(role);
        return e;
    }

    private Role role() {
        Role r = new Role();
        r.setId(UUID.randomUUID());
        return r;
    }

    private UserPermission row(Employee user, Permission p, UserPermissionSource source, boolean revoked) {
        UserPermission up = new UserPermission();
        up.setUser(user);
        up.setPermission(p);
        up.setSource(source);
        up.setRevoked(revoked);
        return up;
    }

    @Test
    @SuppressWarnings("unchecked")
    void assigningARoleCopiesEveryRolePermissionOntoTheUser() {
        Role role = role();
        Employee user = user(role);
        Permission view = permission("Asset.Asset_Master.View"), create = permission("Asset.Asset_Master.Create");
        when(userPermissionRepository.findByUser_Id(user.getId())).thenReturn(List.of());
        when(rolePermissionRepository.findPermissionsByRoleId(role.getId())).thenReturn(List.of(view, create));

        int copied = service.assignFromRole(user);

        assertEquals(2, copied);
        ArgumentCaptor<List<UserPermission>> captor = ArgumentCaptor.forClass(List.class);
        verify(userPermissionRepository).saveAll(captor.capture());
        assertTrue(captor.getValue().stream().allMatch(r -> r.getSource() == UserPermissionSource.ROLE
                && !r.isRevoked() && r.getUser() == user));
    }

    @Test
    void assigningANewRoleDiscardsThePreviousPermissionRows() {
        Role role = role();
        Employee user = user(role);
        UserPermission old = row(user, permission("Asset.Role.View"), UserPermissionSource.DIRECT, false);
        when(userPermissionRepository.findByUser_Id(user.getId())).thenReturn(List.of(old));
        when(rolePermissionRepository.findPermissionsByRoleId(role.getId())).thenReturn(List.of());

        service.assignFromRole(user);

        verify(userPermissionRepository).deleteAll(List.of(old));
        verify(userPermissionRepository).flush();
    }

    @Test
    @SuppressWarnings("unchecked")
    void roleSyncAddsNewRolePermissionsAndRemovesDroppedOnesButKeepsCustomisations() {
        Role role = role();
        Employee alice = user(role), bob = user(role);
        Permission kept = permission("Asset.Asset_Master.View");
        Permission added = permission("Asset.Asset_Master.Create");
        Permission dropped = permission("Asset.Asset_Master.Delete");
        Permission bobRevoked = permission("Asset.Maintenance.View");
        Permission aliceDirect = permission("Asset.Report.Export");

        when(employeeRepository.findByRole_Id(role.getId())).thenReturn(List.of(alice, bob));
        // role now holds: kept, added, bobRevoked
        when(rolePermissionRepository.findPermissionsByRoleId(role.getId())).thenReturn(List.of(kept, added, bobRevoked));

        UserPermission aliceKept = row(alice, kept, UserPermissionSource.ROLE, false);
        UserPermission aliceDropped = row(alice, dropped, UserPermissionSource.ROLE, false);
        UserPermission aliceMaint = row(alice, bobRevoked, UserPermissionSource.ROLE, false);
        UserPermission aliceExtra = row(alice, aliceDirect, UserPermissionSource.DIRECT, false);
        UserPermission bobKept = row(bob, kept, UserPermissionSource.ROLE, false);
        UserPermission bobMaint = row(bob, bobRevoked, UserPermissionSource.ROLE, true);
        when(userPermissionRepository.findByUser_Role_Id(role.getId()))
                .thenReturn(List.of(aliceKept, aliceDropped, aliceMaint, aliceExtra, bobKept, bobMaint));

        var result = service.syncRoleUsers(role.getId());

        assertEquals(2, result.usersProcessed());
        assertEquals(2, result.permissionsAdded());   // "added" permission for alice and bob
        assertEquals(1, result.permissionsRemoved()); // "dropped" for alice only

        ArgumentCaptor<List<UserPermission>> deleted = ArgumentCaptor.forClass(List.class);
        verify(userPermissionRepository).deleteAll(deleted.capture());
        assertEquals(List.of(aliceDropped), deleted.getValue());

        ArgumentCaptor<List<UserPermission>> inserted = ArgumentCaptor.forClass(List.class);
        verify(userPermissionRepository).saveAll(inserted.capture());
        assertTrue(inserted.getValue().stream().allMatch(r -> r.getPermission() == added));
        // bob's revoked Maintenance.View is NOT re-granted, alice's direct Report.Export is NOT removed
        assertFalse(inserted.getValue().stream().anyMatch(r -> r.getPermission() == bobRevoked));
    }

    @Test
    void roleSyncDoesNothingWhenNoUserHoldsTheRole() {
        UUID roleId = UUID.randomUUID();
        when(employeeRepository.findByRole_Id(roleId)).thenReturn(List.of());

        var result = service.syncRoleUsers(roleId);

        assertEquals(0, result.usersProcessed());
        verify(userPermissionRepository, never()).saveAll(anyList());
    }
}
