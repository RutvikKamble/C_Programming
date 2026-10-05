package com.example.assets_management.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * Enables Spring Security method-level authorization such as:
 * @PreAuthorize("hasAuthority(\'Asset.Asset_Master.Create\')")
 */
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfig {
}
