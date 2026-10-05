package com.example.assets_management.security.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice(basePackages = "com.example.assets_management")
public class SecurityAuthorizationExceptionAdvice {

    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationRequired(
            InsufficientAuthenticationException exception) {
        return response(HttpStatus.UNAUTHORIZED, "Unauthorized", "Authentication is required");
    }


    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(
            AuthenticationException exception) {
        return response(HttpStatus.UNAUTHORIZED, "Unauthorized", "Authentication is required");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(
            AccessDeniedException exception) {
        return response(HttpStatus.FORBIDDEN, "Forbidden", "Insufficient permission");
    }

    private ResponseEntity<Map<String, Object>> response(HttpStatus status,
                                                         String error,
                                                         String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}
