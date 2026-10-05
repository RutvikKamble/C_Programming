package com.example.assets_management.security.audit;

import com.example.assets_management.asset_history.dto.request.AssetHistoryRequest;
import com.example.assets_management.security.context.CurrentUserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AuthenticatedAssetHistoryAspectTest {

    @Test
    void replacesChangedByWithAuthenticatedUserId() throws Throwable {
        CurrentUserService currentUserService = mock(CurrentUserService.class);
        UUID userId = UUID.randomUUID();
        when(currentUserService.isAuthenticated()).thenReturn(true);
        when(currentUserService.requireUserId()).thenReturn(userId);

        AuthenticatedAssetHistoryAspect aspect =
                new AuthenticatedAssetHistoryAspect(currentUserService);
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);

        AssetHistoryRequest request = new AssetHistoryRequest(
                UUID.randomUUID(), "UPDATED", null, "old", "new", "desc", "spoofed-user");

        aspect.applyAuthenticatedActor(joinPoint, request);

        verify(joinPoint).proceed(argThat(args -> {
            AssetHistoryRequest actual = (AssetHistoryRequest) args[0];
            return userId.toString().equals(actual.changedBy());
        }));
    }

    @Test
    void preservesSystemActorWhenUnauthenticated() throws Throwable {
        CurrentUserService currentUserService = mock(CurrentUserService.class);
        when(currentUserService.isAuthenticated()).thenReturn(false);

        AuthenticatedAssetHistoryAspect aspect =
                new AuthenticatedAssetHistoryAspect(currentUserService);
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        AssetHistoryRequest request = new AssetHistoryRequest(
                UUID.randomUUID(), "CREATED", null, null, null, "system job", "system");

        aspect.applyAuthenticatedActor(joinPoint, request);

        verify(joinPoint).proceed();
    }
}
