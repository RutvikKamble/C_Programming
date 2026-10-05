package com.example.assets_management.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.crypto.SecretKey;
import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private CustomUserDetails userDetails;
    private SecretKey key;

    @BeforeEach
    void setUp() throws Exception {

        byte[] secret = new byte[32];

        for (int i = 0; i < secret.length; i++) {
            secret[i] = (byte) (i + 1);
        }

        key = io.jsonwebtoken.security.Keys.hmacShaKeyFor(secret);

        jwtService = new JwtService(key);

        Field expiration =
                JwtService.class.getDeclaredField("expirationMs");

        expiration.setAccessible(true);
        expiration.setLong(jwtService, 3600000L);

        userDetails = new CustomUserDetails(
                UUID.randomUUID(),
                "admin@example.com",
                "EMP001",
                "{bcrypt}hash",
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                "ADMIN",
                true,
                List.of(
                        new SimpleGrantedAuthority("ROLE_ADMIN"),
                        new SimpleGrantedAuthority(
                                "Asset.Asset_Master.View")
                )
        );
    }

    /**
     * Verifies that a JWT is generated correctly and
     * that its important claims can be extracted.
     */
    @Test
    void shouldGenerateAndReadJwtClaims() {

        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isBlank());

        assertEquals(
                "admin@example.com",
                jwtService.extractUsername(token)
        );

        assertEquals(
                userDetails.getUserId(),
                jwtService.extractUserId(token)
        );

        assertTrue(
                jwtService.isTokenValid(token, userDetails)
        );
    }

    /**
     * Verifies that a token generated for one user
     * cannot be used by another user.
     */
    @Test
    void shouldRejectTokenForDifferentUser() {

        String token = jwtService.generateToken(userDetails);

        CustomUserDetails other =
                new CustomUserDetails(
                        UUID.randomUUID(),
                        "other@example.com",
                        "EMP002",
                        "hash",
                        null,
                        null,
                        null,
                        null,
                        true,
                        List.of()
                );

        assertFalse(
                jwtService.isTokenValid(token, other)
        );
    }

    /**
     * Important JWT hardening test:
     *
     * The email/username is intentionally the same,
     * but the user UUID is different.
     *
     * The token must still be rejected because the
     * authenticated identity is based on the user UUID.
     */
    @Test
    void shouldRejectTokenWhenUserIdDiffersEvenIfEmailIsSame() {

        String token = jwtService.generateToken(userDetails);

        CustomUserDetails sameEmailDifferentUser =
                new CustomUserDetails(
                        UUID.randomUUID(),
                        userDetails.getUsername(),
                        "EMP999",
                        "hash",
                        userDetails.getCompanyId(),
                        userDetails.getBranchId(),
                        userDetails.getRoleId(),
                        userDetails.getRoleName(),
                        true,
                        List.of()
                );

        assertEquals(
                userDetails.getUsername(),
                sameEmailDifferentUser.getUsername()
        );

        assertNotEquals(
                userDetails.getUserId(),
                sameEmailDifferentUser.getUserId()
        );

        assertFalse(
                jwtService.isTokenValid(
                        token,
                        sameEmailDifferentUser
                )
        );
    }

    /**
     * Verifies directly that the UUID stored in the JWT
     * is the UUID of the user who generated the token.
     */
    @Test
    void shouldStoreCorrectUserIdInToken() {

        String token = jwtService.generateToken(userDetails);

        UUID extractedUserId =
                jwtService.extractUserId(token);

        assertNotNull(extractedUserId);

        assertEquals(
                userDetails.getUserId(),
                extractedUserId
        );
    }

    /**
     * Verifies that the username/email stored in the JWT
     * belongs to the authenticated user.
     */
    @Test
    void shouldStoreCorrectUsernameInToken() {

        String token = jwtService.generateToken(userDetails);

        String extractedUsername =
                jwtService.extractUsername(token);

        assertNotNull(extractedUsername);

        assertEquals(
                userDetails.getUsername(),
                extractedUsername
        );
    }
}