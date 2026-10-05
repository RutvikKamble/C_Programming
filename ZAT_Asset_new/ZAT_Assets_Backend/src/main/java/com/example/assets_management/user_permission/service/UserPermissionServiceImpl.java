package com.example.assets_management.user_permission.service;

import com.example.assets_management.employee.entity.Employee;
import com.example.assets_management.employee.repository.EmployeeRepository;
import com.example.assets_management.permission.entity.Permission;
import com.example.assets_management.permission.enums.PermissionStatus;
import com.example.assets_management.permission.repository.PermissionRepository;
import com.example.assets_management.security.context.CurrentUserService;
import com.example.assets_management.security.rbac.audit.SecurityAuditEventType;
import com.example.assets_management.security.rbac.audit.SecurityAuditService;
import com.example.assets_management.security.rbac.service.RbacGuard;
import com.example.assets_management.user_permission.dto.request.UserPermissionAssignRequest;
import com.example.assets_management.user_permission.dto.response.UserPermissionResponse;
import com.example.assets_management.user_permission.entity.UserPermission;
import com.example.assets_management.user_permission.enums.UserPermissionSource;
import com.example.assets_management.user_permission.repository.UserPermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserPermissionServiceImpl implements UserPermissionService {

    private final UserPermissionRepository userPermissionRepository;
    private final EmployeeRepository employeeRepository;
    private final PermissionRepository permissionRepository;
    private final UserPermissionSyncService syncService;
    private final RbacGuard guard;
    private final CurrentUserService currentUserService;
    private final SecurityAuditService auditService;

    @Override
    @Transactional(readOnly = true)
    public List<UserPermissionResponse> getUserPermissions(UUID userId) {
        getUser(userId);
        return loadResponses(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserPermissionResponse> getUserDirectPermissions(UUID userId) {
        getUser(userId);

        return userPermissionRepository
                .findByUser_IdAndSource(userId, UserPermissionSource.DIRECT)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Set<String> getEffectivePermissionKeys(UUID userId) {
        getUser(userId);
        return Collections.unmodifiableSet(
                new java.util.TreeSet<>(userPermissionRepository.findEffectivePermissionKeys(userId)));
    }

    @Override
    public UserPermissionResponse grantPermission(UUID userId, UserPermissionAssignRequest request) {
        Employee user = getUser(userId);
        Permission permission = getActivePermission(request.permissionId());

        if (guard != null) {
            guard.assertCanManageUserPermissions(user);
            guard.assertCanGrant(List.of(permission));
        }

        Optional<UserPermission> existing =
                userPermissionRepository.findByUser_IdAndPermission_Id(userId, permission.getId());

        if (existing.isPresent()) {
            UserPermission row = existing.get();
            if (!row.isRevoked()) {
                throw new IllegalStateException("User already has this permission");
            }
            row.setRevoked(false);
            UserPermission restored = userPermissionRepository.save(row);
            audit(user, permission, SecurityAuditEventType.USER_PERMISSION_GRANTED,
                    "REVOKED", "RESTORED");
            return toResponse(restored);
        }

        UserPermission row = newRow(user, permission, UserPermissionSource.DIRECT);
        UserPermission saved = userPermissionRepository.save(row);
        audit(user, permission, SecurityAuditEventType.USER_PERMISSION_GRANTED, null, "DIRECT");
        return toResponse(saved);
    }

    @Override
    public void revokePermission(UUID userId, UUID permissionId) {
        Employee user = getUser(userId);

        if (guard != null) {
            guard.assertCanManageUserPermissions(user);
        }

        UserPermission row = userPermissionRepository
                .findByUser_IdAndPermission_Id(userId, permissionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User does not hold this permission: " + permissionId));

        if (row.isRevoked()) {
            throw new IllegalStateException("Permission is already removed from this user");
        }

        Permission permission = row.getPermission();
        if (row.getSource() == UserPermissionSource.ROLE) {
            // keep the row as a marker so a later role sync does not hand the permission back
            row.setRevoked(true);
            userPermissionRepository.save(row);
            audit(user, permission, SecurityAuditEventType.USER_PERMISSION_DENIED, "ROLE", "REVOKED");
        } else {
            userPermissionRepository.delete(row);
            audit(user, permission, SecurityAuditEventType.USER_PERMISSION_OVERRIDE_REMOVED, "DIRECT", null);
        }
    }

    @Override
    public List<UserPermissionResponse> replaceUserPermissions(UUID userId, List<UUID> permissionIds) {
        Employee user = getUser(userId);

        if (guard != null) {
            guard.assertCanManageUserPermissions(user);
        }

        Map<UUID, Permission> wanted = new LinkedHashMap<>();
        for (UUID id : new LinkedHashSet<>(permissionIds)) {
            wanted.put(id, getActivePermission(id));
        }

        Map<UUID, UserPermission> existing = new HashMap<>();
        for (UserPermission row : userPermissionRepository.findByUser_Id(userId)) {
            existing.put(row.getPermission().getId(), row);
        }

        // permissions the user does not effectively hold today but will after this call
        List<Permission> newlyGranted = new ArrayList<>();
        for (Map.Entry<UUID, Permission> entry : wanted.entrySet()) {
            UserPermission row = existing.get(entry.getKey());
            if (row == null || row.isRevoked()) {
                newlyGranted.add(entry.getValue());
            }
        }
        if (guard != null) {
            guard.assertCanGrant(newlyGranted);
        }

        List<UserPermission> toSave = new ArrayList<>();
        List<UserPermission> toDelete = new ArrayList<>();

        for (Map.Entry<UUID, UserPermission> entry : existing.entrySet()) {
            UserPermission row = entry.getValue();
            if (wanted.containsKey(entry.getKey())) {
                if (row.isRevoked()) {
                    row.setRevoked(false);
                    toSave.add(row);
                }
            } else if (row.getSource() == UserPermissionSource.ROLE) {
                if (!row.isRevoked()) {
                    row.setRevoked(true);
                    toSave.add(row);
                }
            } else {
                toDelete.add(row);
            }
        }

        for (Map.Entry<UUID, Permission> entry : wanted.entrySet()) {
            if (!existing.containsKey(entry.getKey())) {
                toSave.add(newRow(user, entry.getValue(), UserPermissionSource.DIRECT));
            }
        }

        if (!toDelete.isEmpty()) {
            userPermissionRepository.deleteAll(toDelete);
            userPermissionRepository.flush();
        }
        if (!toSave.isEmpty()) {
            userPermissionRepository.saveAll(toSave);
        }

        audit(user, null, SecurityAuditEventType.USER_PERMISSION_GRANTED,
                null, "REPLACED:" + wanted.size());
        return loadResponses(userId);
    }

    @Override
    public List<UserPermissionResponse> resetToRole(UUID userId) {
        Employee user = getUser(userId);

        if (guard != null) {
            guard.assertCanManageUserPermissions(user);
        }

        int copied = syncService.assignFromRole(user);
        audit(user, null, SecurityAuditEventType.ROLE_ASSIGNED, "RESET", Integer.toString(copied));
        return loadResponses(userId);
    }

    // ------------------------------------------------------------------

    private List<UserPermissionResponse> loadResponses(UUID userId) {
        return userPermissionRepository.findByUser_Id(userId)
                .stream()
                .map(this::toResponse)
                .sorted(Comparator.comparing(UserPermissionResponse::permissionKey,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }

    private Employee getUser(UUID userId) {
        return employeeRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found with id: " + userId
                ));
    }

    private Permission getActivePermission(UUID permissionId) {
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Permission not found with id: " + permissionId
                ));

        if (permission.getStatus() != PermissionStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Inactive permission cannot be assigned to a user"
            );
        }
        return permission;
    }

    private UserPermission newRow(Employee user, Permission permission, UserPermissionSource source) {
        UserPermission row = new UserPermission();
        row.setUser(user);
        row.setPermission(permission);
        row.setSource(source);
        row.setRevoked(false);
        return row;
    }

    private void audit(Employee user, Permission permission, SecurityAuditEventType type,
                       String oldValue, String newValue) {
        if (auditService == null || currentUserService == null) {
            return;
        }
        UUID actor = currentUserService.isAuthenticated() ? currentUserService.requireUserId() : null;
        UUID companyId = user.getCompany() == null ? null : user.getCompany().getId();
        UUID roleId = user.getRole() == null ? null : user.getRole().getId();
        auditService.record(companyId, actor, user.getId(), roleId,
                permission == null ? null : permission.getId(), type, oldValue, newValue);
    }

    private UserPermissionResponse toResponse(UserPermission userPermission) {
        Permission permission = userPermission.getPermission();

        return new UserPermissionResponse(
                userPermission.getId(),
                userPermission.getUser().getId(),
                permission.getId(),
                permission.getPermissionKey(),
                userPermission.getSource(),
                userPermission.getCreatedAt(),
                userPermission.isRevoked()
        );
    }
}
