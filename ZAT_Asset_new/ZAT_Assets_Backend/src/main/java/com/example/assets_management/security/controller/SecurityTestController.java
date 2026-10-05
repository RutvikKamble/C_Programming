package com.example.assets_management.security.controller;

import com.example.assets_management.security.context.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test/security")
@PreAuthorize("isAuthenticated()")
public class SecurityTestController {

    private final CurrentUserService currentUserService;

    @GetMapping("/current-user")
    public String currentUser() {
        return currentUserService.requireCurrentUser().getUsername();
    }

    @GetMapping("/user-id")
    public UUID userId() {
        return currentUserService.requireUserId();
    }

    @GetMapping("/email")
    public String email() {
        return currentUserService.requireEmail();
    }

    @GetMapping("/company-id")
    public UUID companyId() {
        return currentUserService.getCompanyId();
    }

    @GetMapping("/branch-id")
    public UUID branchId() {
        return currentUserService.getBranchId();
    }

    @GetMapping("/role")
    public String role() {
        return currentUserService.getRoleName();
    }
}