package com.example.assets_management.security.rbac.seed;

import com.example.assets_management.user_permission.service.UserPermissionSyncService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Step 3 of the RBAC bootstrap. Authorization now reads the per-user {@code user_permissions} table,
 * so users that already had a role before this table was populated would otherwise end up with no
 * permissions. Every user that holds a role but has no permission rows yet gets the role's permissions
 * copied onto their UUID. Users that already have rows (and their customisations) are never touched.
 */
@Component
@RequiredArgsConstructor
@Order(3)
public class UserPermissionBackfillSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(UserPermissionBackfillSeeder.class);

    private final UserPermissionSyncService syncService;

    @Override
    @Transactional
    public void run(String... args) {
        int users = syncService.backfillMissingUsers();
        if (users > 0) {
            log.info("RBAC backfill: copied role permissions onto {} existing user(s)", users);
        }
    }
}
