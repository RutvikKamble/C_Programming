package com.example.assets_management.role_permission.service;

import com.example.assets_management.permission.entity.Permission;
import com.example.assets_management.permission.enums.PermissionStatus;
import com.example.assets_management.permission.repository.PermissionRepository;
import com.example.assets_management.role.entity.Role;
import com.example.assets_management.role.enums.RoleStatus;
import com.example.assets_management.role.repository.RoleRepository;
import com.example.assets_management.role_permission.dto.request.RolePermissionAssignRequest;
import com.example.assets_management.role_permission.dto.response.RolePermissionResponse;
import com.example.assets_management.role_permission.entity.RolePermission;
import com.example.assets_management.role_permission.repository.RolePermissionRepository;
import com.example.assets_management.employee.entity.Employee;
import com.example.assets_management.employee.repository.EmployeeRepository;
import com.example.assets_management.security.context.CurrentUserService;
import com.example.assets_management.security.rbac.audit.*;
import com.example.assets_management.security.rbac.service.RbacGuard;
import com.example.assets_management.user_permission.service.UserPermissionSyncService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Role -> permission management. Every change is propagated to all users that hold the role
 * (see {@link UserPermissionSyncService#syncRoleUsers(UUID)}), so adding a permission to a role
 * gives it to every user of that role and removing it takes it away from all of them, while
 * per-user customisations are preserved.
 */
@Service
@Transactional
public class RolePermissionServiceImpl implements RolePermissionService {

    private final RolePermissionRepository rolePermissionRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final EmployeeRepository employeeRepository;
    private final CurrentUserService currentUserService;
    private final SecurityAuditService securityAuditService;
    private final UserPermissionSyncService syncService;
    private final RbacGuard guard;

    public RolePermissionServiceImpl(RolePermissionRepository rolePermissionRepository, RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this(rolePermissionRepository, roleRepository, permissionRepository, null, null, null, null, null);
    }

    @org.springframework.beans.factory.annotation.Autowired
    public RolePermissionServiceImpl(RolePermissionRepository rolePermissionRepository, RoleRepository roleRepository, PermissionRepository permissionRepository, EmployeeRepository employeeRepository, CurrentUserService currentUserService, SecurityAuditService securityAuditService, UserPermissionSyncService syncService, RbacGuard guard) {
        this.rolePermissionRepository = rolePermissionRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.employeeRepository = employeeRepository;
        this.currentUserService = currentUserService;
        this.securityAuditService = securityAuditService;
        this.syncService = syncService;
        this.guard = guard;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RolePermissionResponse> getRolePermissions(UUID roleId) {
        getRole(roleId);

        return rolePermissionRepository.findByRole_Id(roleId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public RolePermissionResponse addPermissionToRole(
            UUID roleId,
            RolePermissionAssignRequest request
    ) {
        Role role = getRole(roleId);

        if (role.getStatus() != RoleStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Inactive role cannot receive permissions"
            );
        }

        Permission permission = permissionRepository.findById(request.permissionId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Permission not found with id: " + request.permissionId()
                ));

        if (permission.getStatus() != PermissionStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Inactive permission cannot be assigned to a role"
            );
        }

        if (rolePermissionRepository.existsByRole_IdAndPermission_Id(
                roleId,
                permission.getId()
        )) {
            throw new IllegalStateException(
                    "Permission is already assigned to this role"
            );
        }

        if (guard != null) {
            guard.assertCanModifyRolePermissions(role);
            guard.assertCanGrant(List.of(permission));
        }

        RolePermission rolePermission = new RolePermission();
        rolePermission.setRole(role);
        rolePermission.setPermission(permission);
        rolePermission.setAssignedBy(actor());

        RolePermission saved = rolePermissionRepository.save(rolePermission);
        if (securityAuditService != null && currentUserService != null) {
            securityAuditService.record(companyId(role), currentUserService.requireUserId(), null, roleId, permission.getId(), SecurityAuditEventType.ROLE_PERMISSION_ADDED, null, permission.getPermissionKey());
        }
        syncUsers(roleId);
        return toResponse(saved);
    }

    @Override
    public List<RolePermissionResponse> replacePermissions(UUID roleId, List<UUID> permissionIds) {
        Role role = getRole(roleId);
        if (role.getStatus() != RoleStatus.ACTIVE) {
            throw new IllegalStateException("Inactive role cannot receive permissions");
        }

        Set<UUID> distinctIds = new LinkedHashSet<>(permissionIds);
        List<Permission> permissions = new ArrayList<>();
        for (UUID id : distinctIds) {
            permissions.add(permissionRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Permission not found: " + id)));
        }
        if (permissions.stream().anyMatch(p -> p.getStatus() != PermissionStatus.ACTIVE)) {
            throw new IllegalStateException("Inactive permission cannot be assigned to a role");
        }

        Map<UUID, RolePermission> existing = new LinkedHashMap<>();
        for (RolePermission rp : rolePermissionRepository.findByRole_Id(roleId)) {
            existing.put(rp.getPermission().getId(), rp);
        }

        if (guard != null) {
            guard.assertCanModifyRolePermissions(role);
            guard.assertCanGrant(permissions.stream().filter(p -> !existing.containsKey(p.getId())).toList());
        }

        // only touch what changed, so unchanged rows keep their assignedAt / assignedBy
        List<RolePermission> removed = existing.entrySet().stream()
                .filter(e -> !distinctIds.contains(e.getKey()))
                .map(Map.Entry::getValue)
                .toList();
        if (!removed.isEmpty()) {
            rolePermissionRepository.deleteAll(removed);
            // deletes must reach the database before any re-insert
            rolePermissionRepository.flush();
        }

        Employee actor = actor();
        List<RolePermission> rows = new ArrayList<>();
        for (Permission p : permissions) {
            RolePermission rp = existing.get(p.getId());
            if (rp == null) {
                rp = new RolePermission();
                rp.setRole(role);
                rp.setPermission(p);
                rp.setAssignedBy(actor);
                rp = rolePermissionRepository.save(rp);
            }
            rows.add(rp);
        }

        if (securityAuditService != null && currentUserService != null) {
            securityAuditService.record(companyId(role), currentUserService.requireUserId(), null, roleId, null, SecurityAuditEventType.ROLE_PERMISSION_ADDED, "REPLACED", Integer.toString(permissions.size()));
        }
        syncUsers(roleId);
        return rows.stream().map(this::toResponse).toList();
    }

    @Override
    public void removePermissionFromRole(
            UUID roleId,
            UUID permissionId
    ) {
        Role role = getRole(roleId);

        RolePermission rolePermission = rolePermissionRepository
                .findByRole_IdAndPermission_Id(roleId, permissionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Permission is not assigned to this role"
                ));

        if (guard != null) {
            guard.assertCanModifyRolePermissions(role);
        }

        rolePermissionRepository.delete(rolePermission);
        if (securityAuditService != null && currentUserService != null) {
            securityAuditService.record(companyId(role), currentUserService.requireUserId(), null, roleId, permissionId, SecurityAuditEventType.ROLE_PERMISSION_REMOVED, rolePermission.getPermission() == null ? null : rolePermission.getPermission().getPermissionKey(), null);
        }
        syncUsers(roleId);
    }

    /** Push the role's current permissions to every user holding the role. */
    private void syncUsers(UUID roleId) {
        if (syncService != null) {
            syncService.syncRoleUsers(roleId);
        }
    }

    private Employee actor() {
        if (employeeRepository != null && currentUserService != null && currentUserService.isAuthenticated()) {
            return employeeRepository.findById(currentUserService.requireUserId()).orElse(null);
        }
        return null;
    }

    private UUID companyId(Role role) {
        return role.getCompany() == null ? null : role.getCompany().getId();
    }

    private Role getRole(UUID roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Role not found with id: " + roleId
                ));
    }

    private RolePermissionResponse toResponse(RolePermission rolePermission) {
        Role role = rolePermission.getRole();
        Permission permission = rolePermission.getPermission();

        return new RolePermissionResponse(
                rolePermission.getId(),
                role.getId(),
                role.getName(),
                permission.getId(),
                permission.getPermissionKey(),
                permission.getStatus(),
                rolePermission.getAssignedAt()
        );
    }
}
