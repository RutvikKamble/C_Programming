package com.example.assets_management.security.config;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.util.Base64;

@Configuration
public class JwtConfig {

    @Bean
    public SecretKey jwtSigningKey(
            @Value("${JWT_SECRET_BASE64:}") String secretBase64) {
        if (secretBase64 == null || secretBase64.isBlank()) {
            throw new IllegalStateException(
                    "JWT_SECRET_BASE64 must be configured with a Base64-encoded secret of at least 256 bits");
        }

        final byte[] secret;
        try {
            secret = Base64.getDecoder().decode(secretBase64);
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException("JWT_SECRET_BASE64 is not valid Base64", ex);
        }

        if (secret.length < 32) {
            throw new IllegalStateException(
                    "JWT_SECRET_BASE64 must decode to at least 32 bytes (256 bits)");
        }

        return Keys.hmacShaKeyFor(secret);
    }
}
