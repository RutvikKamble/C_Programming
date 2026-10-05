package com.example.assets_management.dashboard.service;

import com.example.assets_management.dashboard.dto.response.DashboardSummaryResponse;

/**
 * Service contract for the Dashboard Summary module.
 *
 * <p>Computes aggregated statistics across assets, categories and
 * allocations. This is a read-only aggregation service and does not own a
 * backing entity of its own.</p>
 */
public interface DashboardSummaryService {

    /**
     * Compute the full dashboard summary.
     *
     * @return the aggregated summary
     */
    DashboardSummaryResponse getSummary();
}
