package com.example.assets_management.dashboard.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.example.assets_management.dashboard.service.DashboardSummaryService;

import com.example.assets_management.dashboard.dto.response.DashboardSummaryResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing the Dashboard Summary API.
 *
 * <p>Base path: {@code /api/dashboard/summary}.</p>
 *
 * <ul>
 *   <li>GET {@code /api/dashboard/summary} ÃƒÂ¢Ã¢â€šÂ¬Ã¢â‚¬Â aggregated dashboard statistics</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardSummaryController {

    private final DashboardSummaryService service;

    public DashboardSummaryController(DashboardSummaryService service) {
        this.service = service;
    }

    /**
     * Retrieve the aggregated dashboard summary.
     *
     * @return 200 with the summary payload
     */
    @PreAuthorize("hasAuthority(\'Asset.Dashboard.View\')")
    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> getSummary() {
        return ResponseEntity.ok(service.getSummary());
    }
}
