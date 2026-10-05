package com.example.assets_management.security.rbac.service;

import com.example.assets_management.employee.entity.Employee;
import com.example.assets_management.employee.repository.EmployeeRepository;
import com.example.assets_management.permission.entity.Permission;
import com.example.assets_management.role.entity.Role;
import com.example.assets_management.role.enums.RoleType;
import com.example.assets_management.role.enums.SystemRole;
import com.example.assets_management.role_permission.repository.RolePermissionRepository;
import com.example.assets_management.security.context.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Guards against privilege escalation inside the RBAC administration APIs. The
 * {@code @PreAuthorize} annotations decide who may CALL an endpoint; this guard decides what the
 * caller may do with the data:
 * <ul>
 *   <li>nobody can hand out a permission they do not hold themselves (Super Admin excepted);</li>
 *   <li>only a Super Admin can touch a Super Admin user or assign the Super Admin role;</li>
 *   <li>a user cannot edit their own permissions;</li>
 *   <li>the Super Admin role is system-managed and immutable, and the permissions of the other
 *       built-in roles (shared by every company) can only be changed by a Super Admin, while custom
 *       roles can be changed by Admin / Super Admin of the owning company.</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
public class RbacGuard {

    private final CurrentUserService current;
    private final EmployeeRepository employees;
    private final RolePermissionRepository rolePermissions;

    /** Whether the authenticated caller holds the built-in Super Admin role (checked in the DB, not by name). */
    @Transactional(readOnly = true)
    public boolean isSuperAdmin() {
        if (!current.isAuthenticated()) {
            return false;
        }
        return employees.findById(current.requireUserId())
                .map(Employee::getRole)
                .map(SystemRole::isSuperAdmin)
                .orElse(false);
    }

    /** The caller may only grant permissions they hold. */
    public void assertCanGrant(Collection<Permission> permissions) {
        if (permissions == null || permissions.isEmpty() || isSuperAdmin()) {
            return;
        }
        Set<String> held = new HashSet<>();
        current.requireCurrentUser().getAuthorities().forEach(a -> held.add(a.getAuthority()));
        for (Permission permission : permissions) {
            if (!held.contains(permission.getPermissionKey())) {
                throw new AccessDeniedException(
                        "You cannot grant a permission you do not hold: " + permission.getPermissionKey());
            }
        }
    }

    /** Guard for editing one user's permissions. */
    public void assertCanManageUserPermissions(Employee target) {
        if (isSuperAdmin()) {
            return;
        }
        if (SystemRole.isSuperAdmin(target.getRole())) {
            throw new AccessDeniedException("Only a Super Admin can change a Super Admin's access");
        }
        UUID actor = current.requireUserId();
        if (Objects.equals(actor, target.getId())) {
            throw new AccessDeniedException("You cannot change your own permissions");
        }
    }

    /** Guard for assigning {@code role} to a user whose current role is {@code currentRole}. */
    @Transactional(readOnly = true)
    public void assertCanAssignRole(Role role, Role currentRole) {
        if (isSuperAdmin()) {
            return;
        }
        if (SystemRole.isSuperAdmin(role) || SystemRole.isSuperAdmin(currentRole)) {
            throw new AccessDeniedException("Only a Super Admin can assign or change the Super Admin role");
        }
        // The caller cannot hand a user more access than the caller has.
        assertCanGrant(rolePermissions.findPermissionsByRoleId(role.getId()));
    }

    /** Guard for adding / removing / replacing the permissions of a role. */
    public void assertCanModifyRolePermissions(Role role) {
        if (SystemRole.isSuperAdmin(role)) {
            throw new IllegalStateException(
                    "The Super Admin role is system-managed and always holds every permission");
        }
        if (isSuperAdmin()) {
            return;
        }
        if (role.getType() == RoleType.SYSTEM) {
            throw new AccessDeniedException(
                    "Only a Super Admin can change the permissions of a built-in role");
        }
        if (role.getCompany() != null
                && !Objects.equals(role.getCompany().getId(), current.getCompanyId())) {
            throw new AccessDeniedException("Role belongs to another company");
        }
    }
}
