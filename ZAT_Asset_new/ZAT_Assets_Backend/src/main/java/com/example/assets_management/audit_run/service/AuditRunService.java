package com.example.assets_management.audit_run.service;

import com.example.assets_management.audit_run.dto.request.AuditRunCreateRequest;
import com.example.assets_management.audit_run.dto.request.AuditScanRequest;
import com.example.assets_management.audit_run.dto.response.AuditRunSummaryResponse;
import com.example.assets_management.audit_run.entity.AuditRun;
import com.example.assets_management.audit_run.entity.AuditScan;

import java.util.List;

public interface AuditRunService {
    AuditRun create(AuditRunCreateRequest request);
    List<AuditRun> findAll();
    AuditRun findById(Long id);
    List<AuditScan> findScans(Long id);
    AuditRun initialize(Long id);
    AuditScan scan(Long id, AuditScanRequest request);
    AuditRun reconcile(Long id);
    AuditRun complete(Long id);
    AuditRunSummaryResponse summary(Long id);
    List<AuditScan> exceptions(Long id);
}
