package com.example.assets_management.user_role.service;

import com.example.assets_management.employee.entity.Employee;
import com.example.assets_management.employee.repository.EmployeeRepository;
import com.example.assets_management.role.entity.Role;
import com.example.assets_management.role.enums.RoleStatus;
import com.example.assets_management.role.repository.RoleRepository;
import com.example.assets_management.security.context.CurrentUserService;
import com.example.assets_management.security.rbac.audit.*;
import com.example.assets_management.security.rbac.service.RbacGuard;
import com.example.assets_management.user_permission.service.UserPermissionSyncService;
import com.example.assets_management.user_role.dto.request.UserRoleAssignRequest;
import com.example.assets_management.user_role.dto.response.UserRoleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserRoleServiceImpl implements UserRoleService {

    private final EmployeeRepository users;
    private final RoleRepository roles;
    private final CurrentUserService current;
    private final SecurityAuditService audit;
    private final UserPermissionSyncService permissionSync;
    private final RbacGuard guard;

    @Override
    @Transactional(readOnly = true)
    public UserRoleResponse getUserRole(UUID id) {
        return to(user(id));
    }

    /**
     * Assigns a role to a user. Every permission of the role is copied onto the user's UUID
     * ({@code user_permissions}); from then on the user's permissions can be edited individually and
     * are also kept in step with later changes to the role. Assigning the role the user already has
     * does not touch their permissions (use "reset to role" for that).
     */
    @Override
    public UserRoleResponse assignRole(UUID uid, UserRoleAssignRequest req) {
        Employee u = user(uid);
        Role r = roles.findById(req.roleId()).orElseThrow(() -> new IllegalArgumentException("Role not found"));
        if (r.getStatus() != RoleStatus.ACTIVE) {
            throw new IllegalStateException("Inactive role cannot be assigned");
        }
        UUID userCompanyId = u.getCompany() == null ? null : u.getCompany().getId();
        if (r.getCompany() != null && !Objects.equals(r.getCompany().getId(), userCompanyId)) {
            throw new IllegalStateException("Role belongs to another company");
        }

        Role old = u.getRole();
        boolean roleChanged = old == null || !Objects.equals(old.getId(), r.getId());

        if (guard != null) {
            guard.assertCanAssignRole(r, old);
        }

        u.setRole(r);
        users.save(u);

        if (roleChanged && permissionSync != null) {
            permissionSync.assignFromRole(u);
        }

        if (audit != null) {
            UUID actor = current == null ? null : current.requireUserId();
            audit.record(userCompanyId, actor, uid, r.getId(), null, SecurityAuditEventType.ROLE_ASSIGNED,
                    old == null ? null : String.valueOf(old.getId()), r.getId().toString());
        }
        return to(u);
    }

    private Employee user(UUID id) {
        return users.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private UserRoleResponse to(Employee u) {
        Role r = u.getRole();
        return new UserRoleResponse(u.getId(), u.getEmployeeCode(), u.getFullName(),
                r == null ? null : r.getId(), r == null ? null : r.getName(),
                r == null ? null : r.getType(), r == null ? null : r.getStatus());
    }
}
