package com.example.assets_management.security.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class PasswordConfigTest {

    private final PasswordEncoder passwordEncoder =
            new PasswordConfig().passwordEncoder();

    @Test
    void shouldEncodeAndMatchPassword() {
        String rawPassword = "TestPassword@123";

        String encodedPassword = passwordEncoder.encode(rawPassword);

        assertNotNull(encodedPassword);
        assertNotEquals(rawPassword, encodedPassword);
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
    }

    @Test
    void shouldRejectWrongPassword() {
        String encodedPassword = passwordEncoder.encode("TestPassword@123");

        assertFalse(
                passwordEncoder.matches("WrongPassword@123", encodedPassword)
        );
    }
}
