package com.example.assets_management.user_role.service;

import com.example.assets_management.user_role.dto.request.UserRoleAssignRequest;
import com.example.assets_management.user_role.dto.response.UserRoleResponse;

import java.util.UUID;

public interface UserRoleService {

    UserRoleResponse getUserRole(UUID userId);

    UserRoleResponse assignRole(
            UUID userId,
            UserRoleAssignRequest request
    );
}
