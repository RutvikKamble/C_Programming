package com.example.assets_management.security.authorization;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class PermissionAuthorizationService {

    public void check(Authentication authentication, String permission) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new InsufficientAuthenticationException("Authentication is required");
        }
        if (permission == null || permission.isBlank()) {
            throw new IllegalArgumentException("Permission is required");
        }
        boolean allowed = authentication.getAuthorities().stream()
                .anyMatch(authority -> permission.equals(authority.getAuthority()));
        if (!allowed) {
            throw new AccessDeniedException("Insufficient permission");
        }
    }

    public boolean has(Authentication authentication, String permission) {
        if (authentication == null || !authentication.isAuthenticated() || permission == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> permission.equals(authority.getAuthority()));
    }
}
