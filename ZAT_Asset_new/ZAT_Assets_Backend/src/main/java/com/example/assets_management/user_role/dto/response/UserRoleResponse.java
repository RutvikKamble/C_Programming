package com.example.assets_management.user_role.dto.response;

import com.example.assets_management.role.enums.RoleStatus;
import com.example.assets_management.role.enums.RoleType;

import java.util.UUID;

public record UserRoleResponse(
        UUID userId,
        String employeeCode,
        String fullName,
        UUID roleId,
        String roleName,
        RoleType roleType,
        RoleStatus roleStatus
) {
}
