package com.example.assets_management.security.authorization;

import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PermissionAuthorizationServiceTest {
    private final PermissionAuthorizationService service = new PermissionAuthorizationService();

    @Test
    void shouldAllowRequiredPermission() {
        var authentication = new UsernamePasswordAuthenticationToken(
                "user@example.com", null,
                List.of(new SimpleGrantedAuthority("Asset.Asset_Master.View")));

        assertTrue(service.has(authentication, "Asset.Asset_Master.View"));
        assertDoesNotThrow(() -> service.check(authentication, "Asset.Asset_Master.View"));
    }

    @Test
    void shouldRejectMissingPermission() {
        var authentication = new UsernamePasswordAuthenticationToken(
                "user@example.com", null,
                List.of(new SimpleGrantedAuthority("Asset.Asset_Master.View")));

        assertFalse(service.has(authentication, "Asset.Asset_Master.Delete"));
        assertThrows(AccessDeniedException.class,
                () -> service.check(authentication, "Asset.Asset_Master.Delete"));
    }

    @Test
    void shouldRejectUnauthenticatedRequest() {
        assertThrows(InsufficientAuthenticationException.class,
                () -> service.check(null, "Asset.Asset_Master.View"));
    }
}
