package com.example.assets_management.security.rbac.service;

import com.example.assets_management.security.rbac.dto.UserPermissionOverrideRequest;
import com.example.assets_management.security.rbac.entity.OverrideEffect;
import com.example.assets_management.user_permission.dto.request.UserPermissionAssignRequest;
import com.example.assets_management.user_permission.dto.response.UserPermissionResponse;
import com.example.assets_management.user_permission.enums.UserPermissionSource;
import com.example.assets_management.user_permission.service.UserPermissionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserPermissionOverrideServiceTest {

    @Mock
    UserPermissionService userPermissions;

    @InjectMocks
    UserPermissionOverrideServiceImpl service;

    private UserPermissionResponse row(UUID uid, UUID pid, UserPermissionSource source, boolean revoked) {
        return new UserPermissionResponse(UUID.randomUUID(), uid, pid, "Asset.Report.Export",
                source, LocalDateTime.now(), revoked);
    }

    @Test
    void grantAddsPermissionForThatUserOnly() {
        UUID uid = UUID.randomUUID(), pid = UUID.randomUUID();
        when(userPermissions.grantPermission(eq(uid), any(UserPermissionAssignRequest.class)))
                .thenReturn(row(uid, pid, UserPermissionSource.DIRECT, false));

        var r = service.upsert(uid, pid, new UserPermissionOverrideRequest(OverrideEffect.GRANT, "Temporary reporting"));

        assertEquals(OverrideEffect.GRANT, r.effect());
        verify(userPermissions).grantPermission(eq(uid), any(UserPermissionAssignRequest.class));
    }

    @Test
    void denyRevokesInheritedPermissionAndReportsDeny() {
        UUID uid = UUID.randomUUID(), pid = UUID.randomUUID();
        when(userPermissions.getUserPermissions(uid))
                .thenReturn(List.of(row(uid, pid, UserPermissionSource.ROLE, true)));

        var r = service.upsert(uid, pid, new UserPermissionOverrideRequest(OverrideEffect.DENY, "Left the team"));

        assertEquals(OverrideEffect.DENY, r.effect());
        verify(userPermissions).revokePermission(uid, pid);
    }

    @Test
    void removingADenyRestoresTheInheritedPermission() {
        UUID uid = UUID.randomUUID(), pid = UUID.randomUUID();
        when(userPermissions.getUserPermissions(uid))
                .thenReturn(List.of(row(uid, pid, UserPermissionSource.ROLE, true)));

        service.remove(uid, pid);

        verify(userPermissions).grantPermission(eq(uid), any(UserPermissionAssignRequest.class));
    }
}
