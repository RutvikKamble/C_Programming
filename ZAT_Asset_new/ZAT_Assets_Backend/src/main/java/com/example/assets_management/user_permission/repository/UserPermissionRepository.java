package com.example.assets_management.user_permission.repository;

import com.example.assets_management.user_permission.entity.UserPermission;
import com.example.assets_management.user_permission.enums.UserPermissionSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface UserPermissionRepository
        extends JpaRepository<UserPermission, UUID> {

    List<UserPermission> findByUser_Id(UUID userId);

    List<UserPermission> findByUser_IdAndSource(
            UUID userId,
            UserPermissionSource source
    );

    List<UserPermission> findByPermission_Id(UUID permissionId);

    Optional<UserPermission> findByUser_IdAndPermission_Id(UUID userId, UUID permissionId);

    /** Every permission row of every user that currently holds the given role. */
    List<UserPermission> findByUser_Role_Id(UUID roleId);

    boolean existsByUser_IdAndPermission_IdAndSource(
            UUID userId,
            UUID permissionId,
            UserPermissionSource source
    );

    long countByUser_Id(UUID userId);

    long countByUser_IdAndSource(
            UUID userId,
            UserPermissionSource source
    );

    /**
     * Permission keys the user may actually use right now: not revoked, permission ACTIVE, and
     * - for inherited rows - the user's role still ACTIVE.
     */
    @Query("select p.permissionKey from UserPermission up "
            + "join up.permission p join up.user u left join u.role r "
            + "where u.id = :userId and up.revoked = false "
            + "and p.status = com.example.assets_management.permission.enums.PermissionStatus.ACTIVE "
            + "and (up.source = com.example.assets_management.user_permission.enums.UserPermissionSource.DIRECT "
            + "or (r is not null and r.status = com.example.assets_management.role.enums.RoleStatus.ACTIVE))")
    Set<String> findEffectivePermissionKeys(@Param("userId") UUID userId);

    /** Users that hold a role but have no inherited (ROLE) rows yet (used by the startup backfill). */
    @Query("select u.id from Employee u where u.role is not null "
            + "and not exists (select 1 from UserPermission up where up.user = u "
            + "and up.source = com.example.assets_management.user_permission.enums.UserPermissionSource.ROLE)")
    List<UUID> findUserIdsWithRoleButNoPermissions();
}
