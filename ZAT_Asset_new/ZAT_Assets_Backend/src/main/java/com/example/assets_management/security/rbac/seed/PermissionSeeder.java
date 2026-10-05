package com.example.assets_management.security.rbac.seed;

import com.example.assets_management.permission.entity.Permission;
import com.example.assets_management.permission.enums.PermissionStatus;
import com.example.assets_management.permission.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Step 1 of the RBAC bootstrap: makes sure every permission in {@link RbacDefaults#CATALOG} exists
 * (format {@code Asset.Module.Action}). Only missing permissions are inserted - existing rows
 * (and any admin edits to them) are never touched, so it is safe on every startup.
 */
@Component
@RequiredArgsConstructor
@Order(1)
public class PermissionSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(PermissionSeeder.class);

    private final PermissionRepository permissionRepository;

    @Override
    @Transactional
    public void run(String... args) {
        int created = 0;
        for (var module : RbacDefaults.CATALOG.entrySet()) {
            for (String action : module.getValue()) {
                String key = RbacDefaults.key(module.getKey(), action);
                if (permissionRepository.existsByPermissionKeyIgnoreCase(key)) {
                    continue;
                }
                Permission permission = new Permission();
                permission.setPermissionKey(key);
                permission.setDescription(RbacDefaults.describe(module.getKey(), action));
                permission.setSystemPermission(true);
                permission.setStatus(PermissionStatus.ACTIVE);
                permissionRepository.save(permission);
                created++;
            }
        }
        if (created > 0) {
            log.info("RBAC seed: created {} permission(s)", created);
        }
    }
}
