package com.example.assets_management.security.aspect;

import com.example.assets_management.security.annotation.RequirePermission;
import com.example.assets_management.security.authorization.PermissionAuthorizationService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAuthorizationAspect {

    private final PermissionAuthorizationService permissionAuthorizationService;

    @Around("@annotation(requirePermission)")
    public Object checkMethodPermission(ProceedingJoinPoint joinPoint,
                                        RequirePermission requirePermission) throws Throwable {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        permissionAuthorizationService.check(authentication, requirePermission.value());
        return joinPoint.proceed();
    }

    @Around("@within(requirePermission) && !@annotation(com.example.assets_management.security.annotation.RequirePermission)")
    public Object checkClassPermission(ProceedingJoinPoint joinPoint,
                                       RequirePermission requirePermission) throws Throwable {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        permissionAuthorizationService.check(authentication, requirePermission.value());
        return joinPoint.proceed();
    }
}
