package com.example.assets_management.security.controller;

import com.example.assets_management.security.dto.LoginRequest;
import com.example.assets_management.security.dto.LoginResponse;
import com.example.assets_management.security.service.CustomUserDetails;
import com.example.assets_management.security.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Test
    void shouldLoginAndReturnJwtResponse() {
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        JwtService jwtService = mock(JwtService.class);
        AuthController controller = new AuthController(authenticationManager, jwtService);

        CustomUserDetails user = new CustomUserDetails(
                UUID.randomUUID(), "admin@example.com", "EMP001", "hash",
                null, null, UUID.randomUUID(), "ADMIN", true,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");
        when(jwtService.getExpirationMs()).thenReturn(3600000L);

        LoginResponse response = controller.login(
                new LoginRequest("admin@example.com", "password"))
                .getBody();

        assertNotNull(response);
        assertEquals("Bearer", response.tokenType());
        assertEquals("jwt-token", response.accessToken());
        assertEquals("admin@example.com", response.email());
        assertEquals("ADMIN", response.role());
        assertEquals(user.getUserId(), response.userId());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}
