package com.example.assets_management.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Phase 15: Password hashing foundation.
 *
 * Existing database password_hash values are not rewritten by this phase.
 * BCrypt is exposed as the single PasswordEncoder bean for later
 * registration/password-update/login flows.
 */
@Configuration
public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
