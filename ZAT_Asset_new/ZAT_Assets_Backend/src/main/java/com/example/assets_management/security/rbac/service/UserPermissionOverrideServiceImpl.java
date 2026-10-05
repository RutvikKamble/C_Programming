package com.example.assets_management.security.rbac.service;

import com.example.assets_management.security.rbac.dto.UserPermissionOverrideRequest;
import com.example.assets_management.security.rbac.dto.UserPermissionOverrideResponse;
import com.example.assets_management.security.rbac.entity.OverrideEffect;
import com.example.assets_management.user_permission.dto.request.UserPermissionAssignRequest;
import com.example.assets_management.user_permission.dto.response.UserPermissionResponse;
import com.example.assets_management.user_permission.enums.UserPermissionSource;
import com.example.assets_management.user_permission.service.UserPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserPermissionOverrideServiceImpl implements UserPermissionOverrideService {

    private final UserPermissionService userPermissions;

    @Override
    @Transactional(readOnly = true)
    public List<UserPermissionOverrideResponse> get(UUID userId) {
        // customisations only: direct additions (GRANT) and removed inherited permissions (DENY)
        return userPermissions.getUserPermissions(userId).stream()
                .filter(row -> row.revoked() || row.source() == UserPermissionSource.DIRECT)
                .map(this::toOverride)
                .toList();
    }

    @Override
    public UserPermissionOverrideResponse upsert(UUID userId, UUID permissionId, UserPermissionOverrideRequest request) {
        if (request.effect() == OverrideEffect.GRANT) {
            UserPermissionResponse row = userPermissions.grantPermission(
                    userId, new UserPermissionAssignRequest(permissionId));
            return toOverride(row);
        }

        userPermissions.revokePermission(userId, permissionId);
        return userPermissions.getUserPermissions(userId).stream()
                .filter(row -> row.permissionId().equals(permissionId))
                .findFirst()
                .map(this::toOverride)
                // a removed DIRECT permission leaves no row behind
                .orElseGet(() -> new UserPermissionOverrideResponse(
                        null, userId, permissionId, null, OverrideEffect.DENY,
                        request.reason(), null, null, null));
    }

    @Override
    public void remove(UUID userId, UUID permissionId) {
        UserPermissionResponse row = userPermissions.getUserPermissions(userId).stream()
                .filter(r -> r.permissionId().equals(permissionId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Permission override not found"));

        if (row.revoked()) {
            // removing a DENY restores the inherited permission
            userPermissions.grantPermission(userId, new UserPermissionAssignRequest(permissionId));
        } else if (row.source() == UserPermissionSource.DIRECT) {
            // removing a GRANT takes the direct permission away again
            userPermissions.revokePermission(userId, permissionId);
        } else {
            throw new IllegalArgumentException("Permission override not found");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Set<String> effective(UUID userId) {
        return userPermissions.getEffectivePermissionKeys(userId);
    }

    private UserPermissionOverrideResponse toOverride(UserPermissionResponse row) {
        OverrideEffect effect = row.revoked() ? OverrideEffect.DENY : OverrideEffect.GRANT;
        return new UserPermissionOverrideResponse(
                row.id(), row.userId(), row.permissionId(), row.permissionKey(),
                effect, null, null, row.createdAt(), row.createdAt());
    }
}
