package com.example.assets_management.asset_disposal.service;

import com.example.assets_management.asset_disposal.dto.request.DisposalFinalizeRequest;
import com.example.assets_management.asset_disposal.dto.request.DisposalReviewRequest;
import com.example.assets_management.asset_disposal.dto.response.DisposalResponse;
import com.example.assets_management.asset_disposal.entity.AssetDisposal;
import com.example.assets_management.asset_disposal.enums.DisposalStatus;
import com.example.assets_management.asset_disposal.repository.AssetDisposalRepository;
import com.example.assets_management.asset_history.dto.request.AssetHistoryRequest;
import com.example.assets_management.asset_history.service.AssetHistoryService;
import com.example.assets_management.asset_master.entity.Asset;
import com.example.assets_management.asset_master.enums.AssetStatus;
import com.example.assets_management.asset_master.repository.AssetRepository;
import com.example.assets_management.employee.entity.Employee;
import com.example.assets_management.employee.repository.EmployeeRepository;
import com.example.assets_management.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssetDisposalServiceImpl implements AssetDisposalService {

    private static final List<DisposalStatus> OPEN_STATUSES = List.of(
            DisposalStatus.REVIEW_PENDING,
            DisposalStatus.APPROVED
    );

    private final AssetDisposalRepository disposalRepository;
    private final AssetRepository assetRepository;
    private final EmployeeRepository employeeRepository;
    private final AssetHistoryService assetHistoryService;

    @Override
    @Transactional
    public DisposalResponse createReview(UUID assetId, DisposalReviewRequest request) {
        Asset asset = assetRepository.findActiveById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Active asset not found: " + assetId));

        if (asset.getStatus() == AssetStatus.CLOSED) {
            throw new IllegalStateException("Asset is already closed/disposed and cannot be submitted for disposal");
        }

        if (asset.getStatus() == AssetStatus.ASSIGNED) {
            throw new IllegalStateException("Assigned asset must be returned/unassigned before disposal review");
        }

        if (disposalRepository.existsByAsset_IdAndStatusIn(assetId, OPEN_STATUSES)) {
            throw new IllegalStateException("An active disposal workflow already exists for this asset");
        }

        Employee reviewedBy = employeeRepository.findById(request.reviewedById())
                .orElseThrow(() -> new ResourceNotFoundException("Disposal reviewer/requester not found: " + request.reviewedById()));

        AssetDisposal disposal = new AssetDisposal();
        disposal.setAsset(asset);
        disposal.setReason(request.reason().trim().toUpperCase(Locale.ROOT));
        disposal.setReviewedBy(reviewedBy);
        disposal.setStatus(DisposalStatus.REVIEW_PENDING);
        disposal.setRemarks(request.remarks());

        AssetDisposal saved = disposalRepository.save(disposal);

        recordHistory(
                asset,
                "DISPOSAL_REVIEW_REQUESTED",
                "disposalStatus",
                null,
                DisposalStatus.REVIEW_PENDING.name(),
                "Disposal review raised for reason " + saved.getReason()
                        + " by " + reviewedBy.getFullName(),
                reviewedBy
        );

        return toResponse(saved);
    }

    @Override
    @Transactional
    public DisposalResponse approve(UUID disposalId, UUID approvedById, String remarks) {
        AssetDisposal disposal = getDisposal(disposalId);

        if (disposal.getStatus() != DisposalStatus.REVIEW_PENDING) {
            throw new IllegalStateException("Only REVIEW_PENDING disposal records can be approved");
        }

        Employee approver = employeeRepository.findById(approvedById)
                .orElseThrow(() -> new ResourceNotFoundException("Disposal approver not found: " + approvedById));

        if (disposal.getReviewedBy() != null
                && disposal.getReviewedBy().getId().equals(approver.getId())) {
            throw new IllegalStateException("The person who raised the disposal review cannot approve the same disposal");
        }

        Asset asset = assetRepository.findActiveById(disposal.getAsset().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Active asset not found: " + disposal.getAsset().getId()));

        if (asset.getStatus() == AssetStatus.CLOSED) {
            throw new IllegalStateException("Asset is already closed/disposed");
        }

        disposal.setApprovedBy(approver);
        disposal.setStatus(DisposalStatus.APPROVED);
        if (remarks != null && !remarks.isBlank()) {
            disposal.setRemarks(remarks.trim());
        }

        AssetDisposal saved = disposalRepository.save(disposal);

        recordHistory(
                asset,
                "DISPOSAL_APPROVED",
                "disposalStatus",
                DisposalStatus.REVIEW_PENDING.name(),
                DisposalStatus.APPROVED.name(),
                "Disposal approved by " + approver.getFullName(),
                approver
        );

        return toResponse(saved);
    }

    @Override
    @Transactional
    public DisposalResponse reject(UUID disposalId, UUID rejectedById, String rejectionReason) {
        AssetDisposal disposal = getDisposal(disposalId);

        if (disposal.getStatus() != DisposalStatus.REVIEW_PENDING) {
            throw new IllegalStateException("Only REVIEW_PENDING disposal records can be rejected");
        }

        if (rejectionReason == null || rejectionReason.isBlank()) {
            throw new IllegalArgumentException("rejectionReason is required");
        }

        Employee rejector = employeeRepository.findById(rejectedById)
                .orElseThrow(() -> new ResourceNotFoundException("Disposal reviewer not found: " + rejectedById));

        disposal.setStatus(DisposalStatus.REJECTED);
        disposal.setRejectionReason(rejectionReason.trim());
        disposal.setRemarks(rejectionReason.trim());

        AssetDisposal saved = disposalRepository.save(disposal);

        recordHistory(
                disposal.getAsset(),
                "DISPOSAL_REJECTED",
                "disposalStatus",
                DisposalStatus.REVIEW_PENDING.name(),
                DisposalStatus.REJECTED.name(),
                "Disposal rejected by " + rejector.getFullName() + ": " + rejectionReason.trim(),
                rejector
        );

        return toResponse(saved);
    }

    @Override
    @Transactional
    public DisposalResponse finalizeDisposal(UUID disposalId, DisposalFinalizeRequest request) {
        AssetDisposal disposal = getDisposal(disposalId);

        if (disposal.getStatus() != DisposalStatus.APPROVED) {
            throw new IllegalStateException("Only APPROVED disposal records can be finalized");
        }

        Asset asset = assetRepository.findActiveById(disposal.getAsset().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Active asset not found: " + disposal.getAsset().getId()));

        if (asset.getStatus() == AssetStatus.CLOSED) {
            throw new IllegalStateException("Asset is already closed/disposed");
        }

        Employee finalizer = employeeRepository.findById(request.finalizedById())
                .orElseThrow(() -> new ResourceNotFoundException("Disposal finalizer not found: " + request.finalizedById()));

        LocalDate disposalDate = request.disposalDate();
        if (disposalDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("disposalDate cannot be in the future");
        }

        BigDecimal disposalValue = request.disposalValue() == null
                ? BigDecimal.ZERO
                : request.disposalValue();

        if (disposalValue.signum() < 0) {
            throw new IllegalArgumentException("disposalValue must be zero or greater");
        }

        AssetStatus oldStatus = asset.getStatus();

        disposal.setFinalizedBy(finalizer);
        disposal.setDisposalDate(disposalDate);
        disposal.setDisposalValue(disposalValue);
        disposal.setDisposalMethod(request.disposalMethod().trim().toUpperCase(Locale.ROOT));
        disposal.setStatus(DisposalStatus.DISPOSED);
        if (request.remarks() != null && !request.remarks().isBlank()) {
            disposal.setRemarks(request.remarks().trim());
        }

        // CLOSED is the existing terminal status used by this project and
        // already treated as disposed by the current Dashboard/Transfer logic.
        asset.setStatus(AssetStatus.CLOSED);
        asset.setCurrentOwner(null);
        asset.setCurrentLocation(null);

        AssetDisposal saved = disposalRepository.save(disposal);
        assetRepository.save(asset);

        recordHistory(
                asset,
                "ASSET_DECOMMISSIONED",
                "status",
                oldStatus != null ? oldStatus.name() : null,
                AssetStatus.CLOSED.name(),
                "Asset decommissioned as part of disposal workflow",
                finalizer
        );

        recordHistory(
                asset,
                "ASSET_DISPOSED",
                "disposalMethod",
                null,
                saved.getDisposalMethod(),
                "Asset disposed using " + saved.getDisposalMethod()
                        + " with disposal value " + saved.getDisposalValue(),
                finalizer
        );

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public DisposalResponse findById(UUID disposalId) {
        return toResponse(getDisposal(disposalId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DisposalResponse> findAll(DisposalStatus status) {
        List<AssetDisposal> records = status == null
                ? disposalRepository.findAllByOrderByCreatedAtDesc()
                : disposalRepository.findByStatusOrderByCreatedAtDesc(status);

        return records.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DisposalResponse> findByAssetId(UUID assetId) {
        if (!assetRepository.existsById(assetId)) {
            throw new ResourceNotFoundException("Asset not found: " + assetId);
        }
        return disposalRepository.findByAsset_IdOrderByCreatedAtDesc(assetId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private AssetDisposal getDisposal(UUID disposalId) {
        return disposalRepository.findById(disposalId)
                .orElseThrow(() -> new ResourceNotFoundException("Disposal record not found: " + disposalId));
    }

    private DisposalResponse toResponse(AssetDisposal disposal) {
        Asset asset = disposal.getAsset();
        Employee reviewedBy = disposal.getReviewedBy();
        Employee approvedBy = disposal.getApprovedBy();
        Employee finalizedBy = disposal.getFinalizedBy();

        return new DisposalResponse(
                disposal.getId(),
                asset != null ? asset.getId() : null,
                asset != null ? asset.getAssetCode() : null,
                asset != null ? asset.getName() : null,
                disposal.getStatus(),
                disposal.getReason(),
                reviewedBy != null ? reviewedBy.getId() : null,
                reviewedBy != null ? reviewedBy.getFullName() : null,
                approvedBy != null ? approvedBy.getId() : null,
                approvedBy != null ? approvedBy.getFullName() : null,
                finalizedBy != null ? finalizedBy.getId() : null,
                finalizedBy != null ? finalizedBy.getFullName() : null,
                disposal.getDisposalDate(),
                disposal.getDisposalValue(),
                disposal.getDisposalMethod(),
                disposal.getRejectionReason(),
                disposal.getRemarks(),
                disposal.getCreatedAt(),
                disposal.getUpdatedAt()
        );
    }

    private void recordHistory(Asset asset,
                               String action,
                               String fieldChanged,
                               String oldValue,
                               String newValue,
                               String description,
                               Employee performedBy) {
        String changedBy = performedBy != null && performedBy.getFullName() != null
                ? performedBy.getFullName()
                : "SYSTEM";

        assetHistoryService.create(new AssetHistoryRequest(
                asset.getId(),
                action,
                fieldChanged,
                oldValue,
                newValue,
                description,
                changedBy
        ));
    }
}
