package com.example.assets_management.dashboard.service;

import com.example.assets_management.dashboard.dto.AdminDashboardResponse;

/**
 * Service interface for Admin Dashboard operations.
 * Provides a consolidated view of Phase I and Phase II asset management data.
 */
public interface DashboardService {

    /**
     * Get the complete admin dashboard data.
     * This is a single consolidated endpoint that provides all dashboard information.
     *
     * @return AdminDashboardResponse containing all dashboard metrics and data
     */
    AdminDashboardResponse getAdminDashboard();
}
