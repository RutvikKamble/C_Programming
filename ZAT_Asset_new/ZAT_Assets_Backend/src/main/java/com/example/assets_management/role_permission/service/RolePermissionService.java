package com.example.assets_management.role_permission.service;

import com.example.assets_management.role_permission.dto.request.RolePermissionAssignRequest;
import com.example.assets_management.role_permission.dto.response.RolePermissionResponse;

import java.util.List;
import java.util.UUID;

public interface RolePermissionService {

    List<RolePermissionResponse> getRolePermissions(UUID roleId);

    RolePermissionResponse addPermissionToRole(
            UUID roleId,
            RolePermissionAssignRequest request
    );

    void removePermissionFromRole(UUID roleId, UUID permissionId);

    List<RolePermissionResponse> replacePermissions(UUID roleId, List<UUID> permissionIds);
}
