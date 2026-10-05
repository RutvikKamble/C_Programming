package com.example.assets_management.security.audit;

import com.example.assets_management.asset_history.dto.request.AssetHistoryRequest;
import com.example.assets_management.security.context.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Prevents a caller from spoofing Asset History's changedBy value.
 *
 * For an authenticated HTTP request the authenticated user UUID is written
 * into changedBy. For non-user background/system work the existing value is
 * retained so scheduled jobs remain functional.
 */
@Aspect
@Component
@RequiredArgsConstructor
public class AuthenticatedAssetHistoryAspect {

    private final CurrentUserService currentUserService;

    @Around("execution(* com.example.assets_management.asset_history.service.AssetHistoryService.create(..)) && args(request)")
    public Object applyAuthenticatedActor(ProceedingJoinPoint joinPoint,
                                          AssetHistoryRequest request) throws Throwable {
        if (!currentUserService.isAuthenticated() || request == null) {
            return joinPoint.proceed();
        }

        AssetHistoryRequest authenticatedRequest = new AssetHistoryRequest(
                request.assetId(),
                request.action(),
                request.fieldChanged(),
                request.oldValue(),
                request.newValue(),
                request.description(),
                currentUserService.requireUserId().toString()
        );

        return joinPoint.proceed(new Object[]{authenticatedRequest});
    }
}
