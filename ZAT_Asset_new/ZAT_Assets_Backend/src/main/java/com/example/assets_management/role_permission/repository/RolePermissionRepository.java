package com.example.assets_management.role_permission.repository;

import com.example.assets_management.permission.entity.Permission;
import com.example.assets_management.role.entity.Role;
import com.example.assets_management.role_permission.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Set;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RolePermissionRepository extends JpaRepository<RolePermission, UUID> {

    List<RolePermission> findByRole_Id(UUID roleId);

    List<RolePermission> findByPermission_Id(UUID permissionId);

    Optional<RolePermission> findByRole_IdAndPermission_Id(
            UUID roleId,
            UUID permissionId
    );

    boolean existsByRole_IdAndPermission_Id(
            UUID roleId,
            UUID permissionId
    );

    long countByRole_Id(UUID roleId);

    long countByPermission_Id(UUID permissionId);

    /** All permissions currently assigned to the role (any status). */
    @Query("select rp.permission from RolePermission rp where rp.role.id = :roleId")
    List<Permission> findPermissionsByRoleId(@Param("roleId") UUID roleId);

    @Query("select rp.permission.permissionKey from RolePermission rp where rp.role.id = (select u.role.id from Employee u where u.id = :userId) and rp.permission.status = com.example.assets_management.permission.enums.PermissionStatus.ACTIVE")
    Set<String> findPermissionCodesByUserId(@Param("userId") UUID userId);
}
