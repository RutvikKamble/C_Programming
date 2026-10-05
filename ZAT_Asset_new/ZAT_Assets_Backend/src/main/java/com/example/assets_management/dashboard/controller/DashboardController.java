package com.example.assets_management.dashboard.controller;

import org.springframework.security.access.prepost.PreAuthorize;

import com.example.assets_management.dashboard.dto.AdminDashboardResponse;
import com.example.assets_management.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for Admin Dashboard operations.
 * Provides a single consolidated endpoint for all dashboard data.
 */
@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * Get the complete admin dashboard data.
     * This endpoint combines Phase I and Phase II data into a single operational view.
     *
     * @return AdminDashboardResponse containing all dashboard metrics and data
     */
    @PreAuthorize("hasAuthority(\'Asset.Dashboard.View\')")
    @GetMapping("/admin")
    public ResponseEntity<AdminDashboardResponse> getAdminDashboard() {
        log.info("GET /api/dashboard/admin - Fetching admin dashboard data");

        try {
            AdminDashboardResponse response = dashboardService.getAdminDashboard();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching admin dashboard data", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
