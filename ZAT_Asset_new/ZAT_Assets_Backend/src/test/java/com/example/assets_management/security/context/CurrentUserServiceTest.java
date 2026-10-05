package com.example.assets_management.security.context;

import com.example.assets_management.security.service.CustomUserDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CurrentUserServiceTest {
    private final CurrentUserService service = new CurrentUserService();

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldExposeAuthenticatedUserContext() {
        UUID userId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();
        UUID branchId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();

        CustomUserDetails details = new CustomUserDetails(
                userId, "user@example.com", "EMP001", "hash",
                companyId, branchId, roleId, "ADMIN", true, java.util.List.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities()));

        assertTrue(service.isAuthenticated());
        assertEquals(userId, service.requireUserId());
        assertEquals("user@example.com", service.requireEmail());
        assertEquals(companyId, service.getCompanyId());
        assertEquals(branchId, service.getBranchId());
        assertEquals(roleId, service.getRoleId());
        assertEquals("ADMIN", service.getRoleName());
    }

    @Test
    void shouldRejectMissingAuthentication() {
        assertFalse(service.isAuthenticated());
        assertThrows(IllegalStateException.class, service::requireCurrentUser);
    }
}
