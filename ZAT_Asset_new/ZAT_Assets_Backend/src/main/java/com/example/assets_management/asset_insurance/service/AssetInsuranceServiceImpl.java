package com.example.assets_management.asset_insurance.service;

import com.example.assets_management.asset_insurance.dto.request.InsuranceCreateRequest;
import com.example.assets_management.asset_insurance.dto.request.InsuranceUpdateRequest;
import com.example.assets_management.asset_insurance.dto.response.InsuranceResponse;
import com.example.assets_management.asset_insurance.entity.AssetInsurance;
import com.example.assets_management.asset_insurance.enums.InsuranceStatus;
import com.example.assets_management.asset_insurance.repository.AssetInsuranceRepository;
import com.example.assets_management.asset_master.entity.Asset;
import com.example.assets_management.asset_master.repository.AssetRepository;
import com.example.assets_management.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssetInsuranceServiceImpl implements AssetInsuranceService {

    private final AssetInsuranceRepository insuranceRepository;
    private final AssetRepository assetRepository;

    @Override
    @Transactional
    public InsuranceResponse create(InsuranceCreateRequest request) {
        Asset asset = assetRepository.findActiveById(request.assetId())
                .orElseThrow(() -> new ResourceNotFoundException("Active asset not found: " + request.assetId()));

        validateDates(request.startDate(), request.expiryDate());
        String policyNumber = request.policyNumber().trim();
        if (insuranceRepository.existsByPolicyNumberIgnoreCase(policyNumber)) {
            throw new IllegalStateException("Insurance policy number already exists: " + policyNumber);
        }

        AssetInsurance insurance = new AssetInsurance();
        insurance.setAsset(asset);
        insurance.setProvider(request.provider().trim());
        insurance.setPolicyNumber(policyNumber);
        insurance.setCoverageDetails(trimToNull(request.coverageDetails()));
        insurance.setPremium(request.premium());
        insurance.setStartDate(request.startDate());
        insurance.setExpiryDate(request.expiryDate());
        insurance.setStatus(resolveStatus(request.status(), request.expiryDate()));

        return toResponse(insuranceRepository.save(insurance));
    }

    @Override
    @Transactional
    public InsuranceResponse update(UUID id, InsuranceUpdateRequest request) {
        AssetInsurance existing = getEntity(id);
        validateDates(request.startDate(), request.expiryDate());

        String policyNumber = request.policyNumber().trim();
        if (insuranceRepository.existsByPolicyNumberIgnoreCaseAndIdNot(policyNumber, id)) {
            throw new IllegalStateException("Insurance policy number already exists: " + policyNumber);
        }

        existing.setProvider(request.provider().trim());
        existing.setPolicyNumber(policyNumber);
        existing.setCoverageDetails(trimToNull(request.coverageDetails()));
        existing.setPremium(request.premium());
        existing.setStartDate(request.startDate());
        existing.setExpiryDate(request.expiryDate());
        existing.setStatus(resolveStatus(request.status(), request.expiryDate()));

        return toResponse(insuranceRepository.save(existing));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!insuranceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Insurance policy not found: " + id);
        }
        insuranceRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public InsuranceResponse findById(UUID id) {
        return toResponse(getEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InsuranceResponse> findAll(InsuranceStatus status) {
        List<AssetInsurance> records = status == null
                ? insuranceRepository.findAll()
                : insuranceRepository.findByStatusOrderByExpiryDateAsc(status);
        return records.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InsuranceResponse> findByAssetId(UUID assetId) {
        if (!assetRepository.existsById(assetId)) {
            throw new ResourceNotFoundException("Asset not found: " + assetId);
        }
        return insuranceRepository.findByAsset_IdOrderByExpiryDateAsc(assetId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InsuranceResponse> findExpiringSoon(int days) {
        if (days < 0) {
            throw new IllegalArgumentException("days must be zero or greater");
        }
        LocalDate today = LocalDate.now();
        return insuranceRepository.findByExpiryDateBetweenOrderByExpiryDateAsc(today, today.plusDays(days))
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    private AssetInsurance getEntity(UUID id) {
        return insuranceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Insurance policy not found: " + id));
    }

    private InsuranceStatus resolveStatus(InsuranceStatus requested, LocalDate expiryDate) {
        if (requested == InsuranceStatus.CANCELLED) {
            return InsuranceStatus.CANCELLED;
        }
        if (expiryDate.isBefore(LocalDate.now())) {
            return InsuranceStatus.EXPIRED;
        }
        return InsuranceStatus.ACTIVE;
    }

    private void validateDates(LocalDate startDate, LocalDate expiryDate) {
        if (startDate != null && expiryDate.isBefore(startDate)) {
            throw new IllegalArgumentException("expiryDate cannot be before startDate");
        }
    }

    private String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private InsuranceResponse toResponse(AssetInsurance insurance) {
        Asset asset = insurance.getAsset();
        return new InsuranceResponse(
                insurance.getId(),
                asset != null ? asset.getId() : null,
                asset != null ? asset.getAssetCode() : null,
                asset != null ? asset.getName() : null,
                insurance.getProvider(),
                insurance.getPolicyNumber(),
                insurance.getCoverageDetails(),
                insurance.getPremium(),
                insurance.getStartDate(),
                insurance.getExpiryDate(),
                insurance.getStatus(),
                insurance.getCreatedAt(),
                insurance.getUpdatedAt()
        );
    }
}
