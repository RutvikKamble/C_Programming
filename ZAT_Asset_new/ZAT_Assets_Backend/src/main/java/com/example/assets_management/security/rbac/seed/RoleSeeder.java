package com.example.assets_management.security.rbac.seed;

import com.example.assets_management.permission.entity.Permission;
import com.example.assets_management.permission.repository.PermissionRepository;
import com.example.assets_management.role.entity.Role;
import com.example.assets_management.role.enums.RoleStatus;
import com.example.assets_management.role.enums.RoleType;
import com.example.assets_management.role.enums.SystemRole;
import com.example.assets_management.role.repository.RoleRepository;
import com.example.assets_management.role_permission.entity.RolePermission;
import com.example.assets_management.role_permission.repository.RolePermissionRepository;
import com.example.assets_management.user_permission.service.UserPermissionSyncService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Step 2 of the RBAC bootstrap: makes sure the five built-in roles exist and hold their default
 * permissions.
 * <ul>
 *   <li><b>Super Admin</b> always holds every permission (re-checked on every startup, so permissions
 *       added in later releases reach it automatically).</li>
 *   <li>The other built-in roles receive their default set <b>once</b> (tracked by
 *       {@code roles.default_permissions_seeded}); afterwards a Super Admin can add / remove
 *       permissions freely and a restart will not undo those edits.</li>
 * </ul>
 * Users already holding a changed role are synced through {@link UserPermissionSyncService}.
 */
@Component
@RequiredArgsConstructor
@Order(2)
public class RoleSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(RoleSeeder.class);

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserPermissionSyncService syncService;

    @Override
    @Transactional
    public void run(String... args) {
        Map<String, Permission> byKey = new HashMap<>();
        for (Permission permission : permissionRepository.findAll()) {
            byKey.put(permission.getPermissionKey(), permission);
        }

        Map<SystemRole, Set<String>> defaults = RbacDefaults.defaultRolePermissions();

        for (SystemRole systemRole : SystemRole.values()) {
            Role role = findOrCreate(systemRole);
            Set<UUID> assigned = new HashSet<>();
            for (RolePermission rp : rolePermissionRepository.findByRole_Id(role.getId())) {
                assigned.add(rp.getPermission().getId());
            }

            boolean superAdmin = systemRole == SystemRole.SUPER_ADMIN;
            boolean seedDefaults = superAdmin || !role.isDefaultPermissionsSeeded();
            if (!seedDefaults) {
                continue;
            }

            int added = 0;
            for (String key : defaults.get(systemRole)) {
                Permission permission = byKey.get(key);
                if (permission != null && !assigned.contains(permission.getId())) {
                    RolePermission rp = new RolePermission();
                    rp.setRole(role);
                    rp.setPermission(permission);
                    rolePermissionRepository.save(rp);
                    added++;
                }
            }

            if (!role.isDefaultPermissionsSeeded()) {
                role.setDefaultPermissionsSeeded(true);
                roleRepository.save(role);
            }
            if (added > 0) {
                log.info("RBAC seed: role '{}' received {} default permission(s)", role.getName(), added);
                syncService.syncRoleUsers(role.getId());
            }
        }
    }

    private Role findOrCreate(SystemRole systemRole) {
        return roleRepository.findFirstByTypeAndNameIgnoreCase(RoleType.SYSTEM, systemRole.getDisplayName())
                .or(() -> roleRepository.findFirstByTypeAndCodeIgnoreCase(RoleType.SYSTEM, systemRole.getCode()))
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName(systemRole.getDisplayName());
                    role.setCode(systemRole.getCode());
                    role.setDescription("System-defined role: " + systemRole.getDisplayName());
                    role.setType(RoleType.SYSTEM);
                    role.setStatus(RoleStatus.ACTIVE);
                    log.info("RBAC seed: created built-in role '{}'", role.getName());
                    return roleRepository.save(role);
                });
    }
}
