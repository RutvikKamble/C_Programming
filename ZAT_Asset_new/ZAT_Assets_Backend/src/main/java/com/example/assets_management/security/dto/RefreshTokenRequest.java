package com.example.assets_management.security.dto; import jakarta.validation.constraints.NotBlank; public record RefreshTokenRequest(@NotBlank String refreshToken) {}
