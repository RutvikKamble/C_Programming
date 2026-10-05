package com.example.assets_management.permission.service;

import com.example.assets_management.permission.dto.PermissionCreateRequest;
import com.example.assets_management.permission.dto.PermissionResponse;
import com.example.assets_management.permission.dto.PermissionUpdateRequest;
import com.example.assets_management.permission.entity.Permission;
import com.example.assets_management.permission.enums.PermissionStatus;
import com.example.assets_management.permission.repository.PermissionRepository;
import com.example.assets_management.permission.util.PermissionKeyValidator;
import com.example.assets_management.role.entity.Role;
import com.example.assets_management.role.enums.RoleType;
import com.example.assets_management.role.enums.SystemRole;
import com.example.assets_management.role.repository.RoleRepository;
import com.example.assets_management.role_permission.entity.RolePermission;
import com.example.assets_management.role_permission.repository.RolePermissionRepository;
import com.example.assets_management.user_permission.service.UserPermissionSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final RoleRepository roleRepository;
    private final UserPermissionSyncService syncService;

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponse> getAll() {
        return permissionRepository.findAllByOrderByModuleAscActionAsc().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, List<PermissionResponse>> getGroupedByModule() {
        Map<String, List<PermissionResponse>> grouped = new LinkedHashMap<>();
        for (PermissionResponse response : getAll()) {
            grouped.computeIfAbsent(response.module(), k -> new java.util.ArrayList<>()).add(response);
        }
        return grouped;
    }

    @Override
    @Transactional(readOnly = true)
    public PermissionResponse getById(UUID id) {
        return toResponse(get(id));
    }

    @Override
    public PermissionResponse create(PermissionCreateRequest request) {
        String key = PermissionKeyValidator.normalize(request.permissionKey());
        PermissionKeyValidator.validate(key);

        if (permissionRepository.existsByPermissionKeyIgnoreCase(key)) {
            throw new IllegalStateException("Permission already exists: " + key);
        }

        Permission permission = new Permission();
        permission.setPermissionKey(key);
        permission.setDescription(request.description() == null || request.description().isBlank()
                ? PermissionKeyValidator.application(key) + " " + PermissionKeyValidator.module(key)
                + " " + PermissionKeyValidator.action(key) + " permission"
                : request.description().trim());
        permission.setSystemPermission(false);
        permission.setStatus(PermissionStatus.ACTIVE);
        Permission saved = permissionRepository.save(permission);

        grantToSuperAdmin(saved);
        return toResponse(saved);
    }

    @Override
    public PermissionResponse update(UUID id, PermissionUpdateRequest request) {
        Permission permission = get(id);
        permission.setDescription(request.description() == null ? null : request.description().trim());
        return toResponse(permissionRepository.save(permission));
    }

    @Override
    public PermissionResponse activate(UUID id) {
        Permission permission = get(id);
        permission.setStatus(PermissionStatus.ACTIVE);
        return toResponse(permissionRepository.save(permission));
    }

    @Override
    public PermissionResponse deactivate(UUID id) {
        Permission permission = get(id);
        ensureCustom(permission, "deactivated");
        permission.setStatus(PermissionStatus.INACTIVE);
        return toResponse(permissionRepository.save(permission));
    }

    @Override
    public void delete(UUID id) {
        Permission permission = get(id);
        ensureCustom(permission, "deleted");

        List<RolePermission> rolePermissions = rolePermissionRepository.findByPermission_Id(id);
        if (!rolePermissions.isEmpty()) {
            rolePermissionRepository.deleteAll(rolePermissions);
            rolePermissionRepository.flush();
        }
        syncService.removePermissionEverywhere(id);
        permissionRepository.delete(permission);
    }

    /** The Super Admin role always holds every permission, including newly created ones. */
    private void grantToSuperAdmin(Permission permission) {
        Role superAdmin = roleRepository
                .findFirstByTypeAndNameIgnoreCase(RoleType.SYSTEM, SystemRole.SUPER_ADMIN.getDisplayName())
                .or(() -> roleRepository.findFirstByTypeAndCodeIgnoreCase(RoleType.SYSTEM, SystemRole.SUPER_ADMIN.getCode()))
                .orElse(null);
        if (superAdmin == null) {
            return;
        }
        if (!rolePermissionRepository.existsByRole_IdAndPermission_Id(superAdmin.getId(), permission.getId())) {
            RolePermission rp = new RolePermission();
            rp.setRole(superAdmin);
            rp.setPermission(permission);
            rolePermissionRepository.save(rp);
        }
        syncService.syncRoleUsers(superAdmin.getId());
    }

    private void ensureCustom(Permission permission, String verb) {
        if (permission.isSystemPermission()) {
            throw new IllegalStateException(
                    "System permissions are required by the application and cannot be " + verb);
        }
    }

    private Permission get(UUID id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Permission not found: " + id));
    }

    private PermissionResponse toResponse(Permission p) {
        return new PermissionResponse(p.getId(), p.getPermissionKey(), p.getApplication(), p.getModule(),
                p.getAction(), p.getDescription(), p.isSystemPermission(), p.getStatus(),
                p.getCreatedAt(), p.getUpdatedAt());
    }
}
