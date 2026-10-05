package com.example.assets_management.audit_run.service;

import com.example.assets_management.asset_category.entity.AssetCategory;
import com.example.assets_management.asset_category.repository.AssetCategoryRepository;
import com.example.assets_management.asset_history.service.AssetHistoryService;
import com.example.assets_management.asset_history.enums.AssetHistoryEventType;
import com.example.assets_management.asset_master.entity.Asset;
import com.example.assets_management.asset_master.repository.AssetRepository;
import com.example.assets_management.audit_run.dto.request.AuditRunCreateRequest;
import com.example.assets_management.audit_run.dto.request.AuditScanRequest;
import com.example.assets_management.audit_run.dto.response.AuditRunSummaryResponse;
import com.example.assets_management.audit_run.entity.AuditRun;
import com.example.assets_management.audit_run.entity.AuditScan;
import com.example.assets_management.audit_run.repository.AuditRunRepository;
import com.example.assets_management.audit_run.repository.AuditScanRepository;
import com.example.assets_management.branch.entity.Branch;
import com.example.assets_management.branch.repository.BranchRepository;
import com.example.assets_management.company.entity.Company;
import com.example.assets_management.company.repository.CompanyRepository;
import com.example.assets_management.employee.entity.Employee;
import com.example.assets_management.employee.repository.EmployeeRepository;
import com.example.assets_management.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class AuditRunServiceImpl implements AuditRunService {
    private final AuditRunRepository runRepository;
    private final AuditScanRepository scanRepository;
    private final AssetRepository assetRepository;
    private final CompanyRepository companyRepository;
    private final BranchRepository branchRepository;
    private final AssetCategoryRepository categoryRepository;
    private final EmployeeRepository employeeRepository;
    private final AssetHistoryService historyService;

    public AuditRunServiceImpl(AuditRunRepository runRepository,
                               AuditScanRepository scanRepository,
                               AssetRepository assetRepository,
                               CompanyRepository companyRepository,
                               BranchRepository branchRepository,
                               AssetCategoryRepository categoryRepository,
                               EmployeeRepository employeeRepository,
                               AssetHistoryService historyService) {
        this.runRepository = runRepository;
        this.scanRepository = scanRepository;
        this.assetRepository = assetRepository;
        this.companyRepository = companyRepository;
        this.branchRepository = branchRepository;
        this.categoryRepository = categoryRepository;
        this.employeeRepository = employeeRepository;
        this.historyService = historyService;
    }

    @Override @Transactional
    public AuditRun create(AuditRunCreateRequest r) {
        Company company = r.companyId() == null ? null : companyRepository.findById(r.companyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found: " + r.companyId()));
        Branch branch = r.branchId() == null ? null : branchRepository.findById(r.branchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found: " + r.branchId()));
        AssetCategory category = r.categoryId() == null ? null : categoryRepository.findById(r.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + r.categoryId()));

        if (branch != null && company != null && !branch.getCompany().getId().equals(company.getId()))
            throw new IllegalArgumentException("Branch does not belong to selected company");
        if (category != null && company != null && !category.getCompany().getId().equals(company.getId()))
            throw new IllegalArgumentException("Category does not belong to selected company");

        AuditRun run = new AuditRun();
        run.setName(r.name().trim());
        run.setCompany(company);
        run.setBranch(branch);
        run.setCategory(category);
        run.setAuditedBy(r.auditedBy());
        run.setNotes(r.notes());
        run.setStatus("OPEN");
        return runRepository.save(run);
    }

    @Override @Transactional(readOnly = true)
    public List<AuditRun> findAll() { return runRepository.findAll(); }

    @Override @Transactional(readOnly = true)
    public AuditRun findById(Long id) {
        return runRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Audit run not found: " + id));
    }

    @Override @Transactional(readOnly = true)
    public List<AuditScan> findScans(Long id) {
        return scanRepository.findByAuditRun(findById(id));
    }

    @Override @Transactional
    public AuditRun initialize(Long id) {
        AuditRun run = findById(id);
        ensureNotClosed(run);
        if (!"OPEN".equals(run.getStatus())) throw new IllegalStateException("Audit run is already initialized");
        List<Asset> expected = scopedAssets(run);
        for (Asset asset : expected) {
            if (!scanRepository.existsByAuditRunAndAsset(run, asset)) {
                AuditScan item = new AuditScan();
                item.setAuditRun(run);
                item.setAsset(asset);
                item.setScannedCode("NOT_SCANNED");
                snapshotSystem(item, asset);
                item.setResult("PENDING");
                item.setDiscrepancy(false);
                scanRepository.save(item);
            }
        }
        run.setTotalExpectedAssets(expected.size());
        run.setStatus("IN_PROGRESS");
        AuditRun savedRun = runRepository.save(run);
        for (Asset asset : expected) {
            historyService.recordEvent(asset, AssetHistoryEventType.AUDIT_STARTED, "auditRunId",
                    null, String.valueOf(run.getId()),
                    "Asset included in audit run " + run.getId(), run.getAuditedBy());
        }
        return savedRun;
    }

    @Override @Transactional
    public AuditScan scan(Long id, AuditScanRequest request) {
        AuditRun run = findById(id);
        if (!"IN_PROGRESS".equals(run.getStatus())) throw new IllegalStateException("Audit run must be IN_PROGRESS");
        if (request == null || request.code() == null || request.code().isBlank())
            throw new IllegalArgumentException("code is required");

        Asset asset = assetRepository.findByAssetCode(request.code().trim())
                .orElseGet(() -> assetRepository.findByQrCodeValue(request.code().trim())
                        .orElse(null));
        if (asset == null) asset = assetRepository.findByBarcodeValue(request.code().trim())
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found for scan code"));

        if (!belongsToScope(run, asset)) throw new IllegalArgumentException("Asset is outside this audit scope");

        AuditScan s = scanRepository.findByAuditRunAndAsset(run, asset).orElseGet(AuditScan::new);
        if (s.getId() != null && "VERIFIED".equals(s.getResult()) && request.actualCondition() == null
                && request.actualOwnerId() == null && request.foundLocation() == null)
            throw new IllegalStateException("Asset is already verified in this audit");

        s.setAuditRun(run);
        s.setAsset(asset);
        s.setScannedCode(request.code().trim());
        snapshotSystem(s, asset);
        s.setFoundLocation(request.foundLocation());
        s.setActualOwner(request.actualOwnerId() == null ? asset.getCurrentOwner() :
                employeeRepository.findById(request.actualOwnerId()).orElseThrow(
                        () -> new ResourceNotFoundException("Actual owner not found: " + request.actualOwnerId())));
        s.setActualCondition(normalizeCondition(request.actualCondition()));
        evaluate(s);
        s.setVerifiedAt(LocalDateTime.now());
        AuditScan saved = scanRepository.save(s);

        historyService.recordEvent(
                asset, auditEventType(saved.getResult()), null, null, null,
                "Asset " + asset.getAssetCode() + " checked in audit run " + run.getId(),
                run.getAuditedBy());
        return saved;
    }

    @Override @Transactional
    public AuditRun reconcile(Long id) {
        AuditRun run = findById(id);
        ensureNotClosed(run);
        if (!"IN_PROGRESS".equals(run.getStatus())) throw new IllegalStateException("Audit run must be IN_PROGRESS");
        List<AuditScan> items = scanRepository.findByAuditRun(run);
        for (AuditScan item : items) {
            if ("PENDING".equals(item.getResult())) {
                item.setResult("MISSING");
                item.setDiscrepancy(true);
                item.setDiscrepancyDetails("Asset was expected in the audit scope but was not physically verified.");
                scanRepository.save(item);
                historyService.recordEvent(item.getAsset(), AssetHistoryEventType.AUDIT_MISSING,
                        "auditRunId", null, String.valueOf(run.getId()),
                        "Asset was not physically found during audit run " + run.getId(), run.getAuditedBy());
            }
        }
        recalculate(run);
        run.setStatus("RECONCILIATION");
        return runRepository.save(run);
    }

    @Override @Transactional
    public AuditRun complete(Long id) {
        AuditRun run = findById(id);
        ensureNotClosed(run);
        if (!"RECONCILIATION".equals(run.getStatus()))
            throw new IllegalStateException("Audit must be reconciled before completion");
        recalculate(run);
        run.setStatus(run.getTotalDiscrepancies() == 0 ? "COMPLETED" : "COMPLETED_WITH_EXCEPTIONS");
        run.setClosedAt(LocalDateTime.now());
        AuditRun saved = runRepository.save(run);
        for (AuditScan item : scanRepository.findByAuditRun(run)) {
            if (!"PENDING".equals(item.getResult())) {
                historyService.recordEvent(item.getAsset().getId(), AssetHistoryEventType.AUDIT_COMPLETED, "auditRunId", null, String.valueOf(run.getId()), "Audit run " + run.getId() + " completed with result " + item.getResult(), run.getAuditedBy());
            }
        }
        return saved;
    }

    @Override @Transactional(readOnly = true)
    public AuditRunSummaryResponse summary(Long id) {
        AuditRun r = findById(id);
        int total = r.getTotalExpectedAssets() == null ? 0 : r.getTotalExpectedAssets();
        int verified = r.getTotalVerifiedAssets() == null ? 0 : r.getTotalVerifiedAssets();
        int missing = r.getTotalMissingAssets() == null ? 0 : r.getTotalMissingAssets();
        int damaged = r.getTotalDamagedAssets() == null ? 0 : r.getTotalDamagedAssets();
        int discrepancies = r.getTotalDiscrepancies() == null ? 0 : r.getTotalDiscrepancies();
        int scanned = verified + damaged + countResult(r, "LOCATION_MISMATCH") + countResult(r, "OWNER_MISMATCH")
                + countResult(r, "MULTIPLE_DISCREPANCIES") + countResult(r, "VERIFIED");
        double progress = total == 0 ? 0 : Math.round((scanned * 10000.0 / total)) / 100.0;
        return new AuditRunSummaryResponse(r.getId(), r.getStatus(), total, verified, missing, damaged,
                discrepancies, Math.min(scanned, total), progress, discrepancies == 0 && missing == 0 && damaged == 0);
    }

    @Override @Transactional(readOnly = true)
    public List<AuditScan> exceptions(Long id) {
        return scanRepository.findExceptions(findById(id));
    }

    private List<Asset> scopedAssets(AuditRun r) {
        List<Asset> result = new ArrayList<>();
        for (Asset a : assetRepository.findAllActive()) if (belongsToScope(r, a)) result.add(a);
        return result;
    }

    private boolean belongsToScope(AuditRun r, Asset a) {
        if (r.getCompany() != null && !r.getCompany().getId().equals(a.getCompany().getId())) return false;
        if (r.getBranch() != null && (a.getBranch() == null || !r.getBranch().getId().equals(a.getBranch().getId()))) return false;
        if (r.getCategory() != null && (a.getCategory() == null || !r.getCategory().getId().equals(a.getCategory().getId()))) return false;
        return true;
    }

    private void snapshotSystem(AuditScan s, Asset a) {
        s.setExpectedLocation(a.getCurrentLocation());
        s.setExpectedOwner(a.getCurrentOwner());
        s.setExpectedStatus(a.getStatus() == null ? null : a.getStatus().name());
    }

    private void evaluate(AuditScan s) {
        List<String> differences = new ArrayList<>();
        if (s.getExpectedLocation() != null && s.getFoundLocation() != null &&
                !s.getExpectedLocation().trim().equalsIgnoreCase(s.getFoundLocation().trim()))
            differences.add("LOCATION_MISMATCH");
        if (s.getExpectedOwner() != null && s.getActualOwner() != null &&
                !s.getExpectedOwner().getId().equals(s.getActualOwner().getId()))
            differences.add("OWNER_MISMATCH");
        if ("DAMAGED".equals(s.getActualCondition()) || "NEEDS_REPAIR".equals(s.getActualCondition()))
            differences.add("DAMAGED");

        s.setDiscrepancy(!differences.isEmpty());
        s.setDiscrepancyDetails(differences.isEmpty() ? null : String.join(", ", differences));
        if (differences.isEmpty()) s.setResult("VERIFIED");
        else if (differences.size() == 1) s.setResult(differences.get(0));
        else s.setResult("MULTIPLE_DISCREPANCIES");
    }

    private AssetHistoryEventType auditEventType(String result) {
        return switch (result == null ? "" : result) {
            case "VERIFIED" -> AssetHistoryEventType.AUDIT_VERIFIED;
            case "MISSING" -> AssetHistoryEventType.AUDIT_MISSING;
            case "DAMAGED" -> AssetHistoryEventType.AUDIT_DAMAGED;
            default -> AssetHistoryEventType.AUDIT_DISCREPANCY;
        };
    }

    private String normalizeCondition(String condition) {
        if (condition == null || condition.isBlank()) return "GOOD";
        return condition.trim().toUpperCase(Locale.ROOT).replace(' ', '_');
    }

    private void recalculate(AuditRun r) {
        List<AuditScan> items = scanRepository.findByAuditRun(r);
        int verified = 0, missing = 0, damaged = 0, discrepancies = 0;
        for (AuditScan s : items) {
            if ("VERIFIED".equals(s.getResult())) verified++;
            if ("MISSING".equals(s.getResult())) missing++;
            if ("DAMAGED".equals(s.getResult()) || "MULTIPLE_DISCREPANCIES".equals(s.getResult())
                    && s.getDiscrepancyDetails() != null && s.getDiscrepancyDetails().contains("DAMAGED")) damaged++;
            if (s.isDiscrepancy()) discrepancies++;
        }
        r.setTotalExpectedAssets(items.size());
        r.setTotalVerifiedAssets(verified);
        r.setTotalMissingAssets(missing);
        r.setTotalDamagedAssets(damaged);
        r.setTotalDiscrepancies(discrepancies);
    }

    private int countResult(AuditRun r, String result) {
        int n = 0;
        for (AuditScan s : scanRepository.findByAuditRun(r)) if (result.equals(s.getResult())) n++;
        return n;
    }

    private void ensureNotClosed(AuditRun r) {
        if ("COMPLETED".equals(r.getStatus()) || "COMPLETED_WITH_EXCEPTIONS".equals(r.getStatus())
                || "CANCELLED".equals(r.getStatus()))
            throw new IllegalStateException("Audit run is closed");
    }
}
