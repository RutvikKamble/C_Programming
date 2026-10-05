package com.example.assets_management.permission.repository;

import com.example.assets_management.permission.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PermissionRepository extends JpaRepository<Permission, UUID> {

    Optional<Permission> findByPermissionKey(String permissionKey);

    boolean existsByPermissionKeyIgnoreCase(String permissionKey);

    List<Permission> findAllByOrderByModuleAscActionAsc();
}
