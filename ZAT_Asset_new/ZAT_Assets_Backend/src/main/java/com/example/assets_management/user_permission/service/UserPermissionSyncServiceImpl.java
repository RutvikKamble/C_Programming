package com.example.assets_management.user_permission.service;

import com.example.assets_management.employee.entity.Employee;
import com.example.assets_management.employee.repository.EmployeeRepository;
import com.example.assets_management.permission.entity.Permission;
import com.example.assets_management.role.entity.Role;
import com.example.assets_management.role_permission.repository.RolePermissionRepository;
import com.example.assets_management.user_permission.entity.UserPermission;
import com.example.assets_management.user_permission.enums.UserPermissionSource;
import com.example.assets_management.user_permission.repository.UserPermissionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserPermissionSyncServiceImpl implements UserPermissionSyncService {

    private static final Logger log = LoggerFactory.getLogger(UserPermissionSyncServiceImpl.class);

    private final UserPermissionRepository userPermissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public int assignFromRole(Employee user) {
        List<UserPermission> existing = userPermissionRepository.findByUser_Id(user.getId());
        if (!existing.isEmpty()) {
            userPermissionRepository.deleteAll(existing);
            // deletes must hit the database before the inserts below (unique user+permission)
            userPermissionRepository.flush();
        }

        Role role = user.getRole();
        if (role == null) {
            return 0;
        }

        List<UserPermission> rows = new ArrayList<>();
        for (Permission permission : rolePermissionRepository.findPermissionsByRoleId(role.getId())) {
            rows.add(newRow(user, permission, UserPermissionSource.ROLE));
        }
        userPermissionRepository.saveAll(rows);
        return rows.size();
    }

    @Override
    public RoleSyncResult syncRoleUsers(UUID roleId) {
        List<Employee> users = employeeRepository.findByRole_Id(roleId);
        if (users.isEmpty()) {
            return new RoleSyncResult(0, 0, 0);
        }

        Map<UUID, Permission> desired = new LinkedHashMap<>();
        for (Permission permission : rolePermissionRepository.findPermissionsByRoleId(roleId)) {
            desired.put(permission.getId(), permission);
        }

        // existing rows grouped: userId -> (permissionId -> row)
        Map<UUID, Map<UUID, UserPermission>> byUser = new HashMap<>();
        for (UserPermission row : userPermissionRepository.findByUser_Role_Id(roleId)) {
            byUser.computeIfAbsent(row.getUser().getId(), k -> new HashMap<>())
                    .put(row.getPermission().getId(), row);
        }

        List<UserPermission> toDelete = new ArrayList<>();
        List<UserPermission> toInsert = new ArrayList<>();

        for (Employee user : users) {
            Map<UUID, UserPermission> mine = byUser.getOrDefault(user.getId(), Map.of());

            // permission removed from the role -> take the inherited row away from the user
            for (Map.Entry<UUID, UserPermission> entry : mine.entrySet()) {
                UserPermission row = entry.getValue();
                if (row.getSource() == UserPermissionSource.ROLE && !desired.containsKey(entry.getKey())) {
                    toDelete.add(row);
                }
            }

            // permission added to the role -> give it to the user unless the user already has a row
            // (a revoked row stays revoked: that was a deliberate per-user decision)
            for (Permission permission : desired.values()) {
                if (!mine.containsKey(permission.getId())) {
                    toInsert.add(newRow(user, permission, UserPermissionSource.ROLE));
                }
            }
        }

        if (!toDelete.isEmpty()) {
            userPermissionRepository.deleteAll(toDelete);
            userPermissionRepository.flush();
        }
        if (!toInsert.isEmpty()) {
            userPermissionRepository.saveAll(toInsert);
        }

        log.info("RBAC role sync: role={}, users={}, added={}, removed={}",
                roleId, users.size(), toInsert.size(), toDelete.size());
        return new RoleSyncResult(users.size(), toInsert.size(), toDelete.size());
    }

    @Override
    public void clearUser(UUID userId) {
        List<UserPermission> rows = userPermissionRepository.findByUser_Id(userId);
        if (!rows.isEmpty()) {
            userPermissionRepository.deleteAll(rows);
            userPermissionRepository.flush();
        }
    }

    @Override
    public void removePermissionEverywhere(UUID permissionId) {
        List<UserPermission> rows = userPermissionRepository.findByPermission_Id(permissionId);
        if (!rows.isEmpty()) {
            userPermissionRepository.deleteAll(rows);
            userPermissionRepository.flush();
        }
    }

    @Override
    public int backfillMissingUsers() {
        int count = 0;
        for (UUID userId : userPermissionRepository.findUserIdsWithRoleButNoPermissions()) {
            Employee user = employeeRepository.findById(userId).orElse(null);
            if (user == null || user.getRole() == null) {
                continue;
            }
            // additive: keep whatever rows the user already has (e.g. migrated direct grants)
            Set<UUID> have = new HashSet<>();
            for (UserPermission row : userPermissionRepository.findByUser_Id(userId)) {
                have.add(row.getPermission().getId());
            }
            List<UserPermission> rows = new ArrayList<>();
            for (Permission permission : rolePermissionRepository.findPermissionsByRoleId(user.getRole().getId())) {
                if (!have.contains(permission.getId())) {
                    rows.add(newRow(user, permission, UserPermissionSource.ROLE));
                }
            }
            userPermissionRepository.saveAll(rows);
            count++;
        }
        return count;
    }

    private UserPermission newRow(Employee user, Permission permission, UserPermissionSource source) {
        UserPermission row = new UserPermission();
        row.setUser(user);
        row.setPermission(permission);
        row.setSource(source);
        row.setRevoked(false);
        return row;
    }
}
