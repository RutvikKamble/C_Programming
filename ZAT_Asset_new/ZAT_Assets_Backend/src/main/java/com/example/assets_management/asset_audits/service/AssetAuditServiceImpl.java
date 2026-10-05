package com.example.assets_management.asset_audits.service;

import com.example.assets_management.asset_audits.entity.AssetAudit;
import com.example.assets_management.asset_audits.repository.AssetAuditRepository;
import com.example.assets_management.exception.ResourceNotFoundException;
import com.example.assets_management.asset_history.enums.AssetHistoryEventType;
import com.example.assets_management.asset_history.service.AssetHistoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AssetAuditServiceImpl implements AssetAuditService {

    private final AssetAuditRepository auditRepository;
    private final AssetHistoryService historyService;

    public AssetAuditServiceImpl(AssetAuditRepository auditRepository, AssetHistoryService historyService) {
        this.auditRepository = auditRepository;
        this.historyService = historyService;
    }

    @Override
    @Transactional
    public AssetAudit createAudit(AssetAudit audit) {
        AssetAudit saved = auditRepository.save(audit);
        if (saved.getAsset() != null) {
            historyService.recordEvent(saved.getAsset(), AssetHistoryEventType.AUDIT_VERIFIED,
                    "asset_audit", null, String.valueOf(saved.getId()),
                    "Individual asset audit record created", saved.getAuditedBy());
        }
        return saved;
    }

    @Override
    @Transactional
    public AssetAudit updateAudit(Long id, AssetAudit audit) {
        AssetAudit existing = auditRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AssetAudit not found with id: " + id));

        existing.setAsset(audit.getAsset());
        existing.setAuditDate(audit.getAuditDate());
        existing.setAuditedBy(audit.getAuditedBy());
        existing.setLocationVerified(audit.getLocationVerified());
        existing.setCondition(audit.getCondition());
        existing.setDiscrepancyNotes(audit.getDiscrepancyNotes());
        existing.setStatus(audit.getStatus());

        AssetAudit saved = auditRepository.save(existing);
        if (saved.getAsset() != null) {
            historyService.recordEvent(saved.getAsset(), AssetHistoryEventType.AUDIT_DISCREPANCY,
                    "asset_audit", null, String.valueOf(saved.getId()),
                    "Individual asset audit record updated", saved.getAuditedBy());
        }
        return saved;
    }

    @Override
    @Transactional
    public void deleteAudit(Long id) {
        AssetAudit existing = auditRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AssetAudit not found with id: " + id));
        if (existing.getAsset() != null) {
            historyService.recordEvent(
                    existing.getAsset().getId(),
                    "AUDIT_RECORD_DELETED",
                    "asset_audit",
                    String.valueOf(id),
                    null,
                    "Individual asset audit record deleted",
                    "SYSTEM");
        }
        auditRepository.delete(existing);
    }

    @Override
    public AssetAudit getAuditById(Long id) {
        return auditRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AssetAudit not found with id: " + id));
    }

    @Override
    public List<AssetAudit> getAllAudits() {
        return auditRepository.findAll();
    }

    @Override
    public List<AssetAudit> getAuditsByAssetId(UUID assetId) {
        return auditRepository.findByAsset_Id(assetId);
    }
}
