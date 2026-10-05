package com.example.assets_management.audit_run.dto.request;

import java.util.UUID;

public record AuditScanRequest(
        String code,
        String foundLocation,
        UUID actualOwnerId,
        String actualCondition
) {}
