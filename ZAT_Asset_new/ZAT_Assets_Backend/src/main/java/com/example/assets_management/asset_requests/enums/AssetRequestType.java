package com.example.assets_management.asset_requests.enums;

public enum AssetRequestType {
    // New workflow types
    CREATE,
    NEW_ASSET,
    PURCHASE,
    BRANCH_TRANSFER,
    HEAD_OFFICE_TRANSFER,

    // Existing employee-request types kept for backward compatibility
    NEWEST,
    REPAIR,
    REPLACEMENT
}
