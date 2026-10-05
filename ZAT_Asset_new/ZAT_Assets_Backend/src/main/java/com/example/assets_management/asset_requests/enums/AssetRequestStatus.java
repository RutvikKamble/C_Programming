package com.example.assets_management.asset_requests.enums;

public enum AssetRequestStatus {
    DRAFT,
    SENT,
    RECEIVED,
    APPROVED,
    REJECTED,
    RECEIVED_ASSET,
    PAY_BILL,
    TRANSFERRED,
    COMPLETED,
    CANCELLED,

    // Existing employee-request statuses kept for backward compatibility
    SEND,
    PENDING_MANAGER,
    ACCEPT,
    REJECT,
    FULFILLED
}
