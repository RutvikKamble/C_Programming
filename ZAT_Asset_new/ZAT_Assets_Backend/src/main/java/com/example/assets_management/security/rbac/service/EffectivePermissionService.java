package com.example.assets_management.security.rbac.service;

import com.example.assets_management.user_permission.repository.UserPermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Resolves what a user may do. The source of truth is the per-user {@code user_permissions} table
 * (permissions keyed on the user's UUID): rows that are not revoked, whose permission is ACTIVE and,
 * for role-inherited rows, whose role is ACTIVE.
 */
@Service("permissionService")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EffectivePermissionService {

    private final UserPermissionRepository userPermissionRepository;

    public Set<String> getEffectivePermissions(UUID userId) {
        return Collections.unmodifiableSet(
                new HashSet<>(userPermissionRepository.findEffectivePermissionKeys(userId)));
    }

    public boolean hasPermission(UUID userId, String permission) {
        return getEffectivePermissions(userId).contains(permission);
    }

    public boolean hasPermissionForAuthentication(org.springframework.security.core.Authentication a, String permission) {
        if (a == null || !a.isAuthenticated() || a.getName() == null) {
            return false;
        }
        try {
            return hasPermission(UUID.fromString(a.getName()), permission);
        } catch (Exception ex) {
            return a.getAuthorities().stream().anyMatch(x -> permission.equals(x.getAuthority()));
        }
    }
}
