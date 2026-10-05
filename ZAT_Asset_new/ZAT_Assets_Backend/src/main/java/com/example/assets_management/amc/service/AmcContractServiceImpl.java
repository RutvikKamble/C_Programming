package com.example.assets_management.amc.service;

import com.example.assets_management.amc.entity.AmcContract;
import com.example.assets_management.amc.repository.AmcContractRepository;
import com.example.assets_management.asset_category.entity.AssetCategory;
import com.example.assets_management.asset_category.repository.AssetCategoryRepository;
import com.example.assets_management.asset_master.entity.Asset;
import com.example.assets_management.asset_master.repository.AssetRepository;
import com.example.assets_management.exception.ResourceNotFoundException;
import com.example.assets_management.asset_history.enums.AssetHistoryEventType;
import com.example.assets_management.asset_history.service.AssetHistoryService;
import com.example.assets_management.vendor.entity.Vendor;
import com.example.assets_management.vendor.repository.VendorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class AmcContractServiceImpl implements AmcContractService {

    private final AmcContractRepository repository;
    private final AssetRepository assetRepository;
    private final AssetCategoryRepository categoryRepository;
    private final VendorRepository vendorRepository;
    private final AssetHistoryService historyService;

    public AmcContractServiceImpl(AmcContractRepository repository,
                                  AssetRepository assetRepository,
                                  AssetCategoryRepository categoryRepository,
                                  VendorRepository vendorRepository,
                                  AssetHistoryService historyService) {
        this.repository = repository;
        this.assetRepository = assetRepository;
        this.categoryRepository = categoryRepository;
        this.vendorRepository = vendorRepository;
        this.historyService = historyService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AmcContract> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public AmcContract findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AMC contract not found: " + id));
    }

    @Override
    @Transactional
    public AmcContract create(AmcContract request) {
        validateContract(request);

        AmcContract contract = new AmcContract();
        applyReferencesAndFields(contract, request);
        AmcContract saved = repository.save(contract);
        if (saved.getAsset() != null) {
            historyService.recordEvent(saved.getAsset(), AssetHistoryEventType.AMC_CREATED, "amcContractId",
                    null, saved.getId().toString(), "AMC contract created", "SYSTEM");
        }
        return saved;
    }

    @Override
    @Transactional
    public AmcContract update(UUID id, AmcContract request) {
        AmcContract existing = findById(id);
        validateContract(request);
        applyReferencesAndFields(existing, request);
        AmcContract saved = repository.save(existing);
        if (saved.getAsset() != null) {
            historyService.recordEvent(saved.getAsset(), AssetHistoryEventType.AMC_UPDATED, "amcContractId",
                    null, saved.getId().toString(), "AMC contract updated", "SYSTEM");
        }
        return saved;
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        AmcContract existing = findById(id);
        if (existing.getAsset() != null) {
            historyService.recordEvent(existing.getAsset(), AssetHistoryEventType.AMC_DELETED, "amcContractId",
                    id.toString(), null, "AMC contract deleted", "SYSTEM");
        }
        repository.delete(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AmcContract> findByAsset(UUID assetId) {
        if (!assetRepository.existsById(assetId)) {
            throw new ResourceNotFoundException("Asset not found: " + assetId);
        }
        return repository.findByAsset_IdOrderByRenewalDateAsc(assetId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AmcContract> findByCategory(UUID categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Asset Category not found: " + categoryId);
        }
        return repository.findByCategory_IdOrderByRenewalDateAsc(categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AmcContract> findRenewals(int days) {
        if (days < 0 || days > 3650) {
            throw new IllegalArgumentException("days must be between 0 and 3650");
        }
        LocalDate today = LocalDate.now();
        return repository.findByRenewalDateBetweenOrderByRenewalDateAsc(today, today.plusDays(days));
    }

    private void validateContract(AmcContract request) {
        if (request == null) {
            throw new IllegalArgumentException("AMC contract request is required");
        }
        boolean hasAsset = request.getAsset() != null && request.getAsset().getId() != null;
        boolean hasCategory = request.getCategory() != null && request.getCategory().getId() != null;

        if (hasAsset == hasCategory) {
            throw new IllegalArgumentException("AMC contract must cover exactly one asset or one asset category");
        }
        if (request.getVendor() == null || request.getVendor().getId() == null) {
            throw new IllegalArgumentException("AMC contract vendor is required");
        }
        if (request.getContractValue() == null || request.getContractValue().signum() < 0) {
            throw new IllegalArgumentException("contractValue must be zero or greater");
        }
        if (request.getStartDate() == null || request.getRenewalDate() == null) {
            throw new IllegalArgumentException("startDate and renewalDate are required");
        }
        if (request.getRenewalDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("renewalDate cannot be before startDate");
        }
    }

    private void applyReferencesAndFields(AmcContract target, AmcContract request) {
        if (request.getAsset() != null && request.getAsset().getId() != null) {
            Asset asset = assetRepository.findById(request.getAsset().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Asset not found: " + request.getAsset().getId()));
            target.setAsset(asset);
            target.setCategory(null);
        } else {
            AssetCategory category = categoryRepository.findById(request.getCategory().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Asset Category not found: " + request.getCategory().getId()));
            target.setCategory(category);
            target.setAsset(null);
        }

        Vendor vendor = vendorRepository.findById(request.getVendor().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found: " + request.getVendor().getId()));

        target.setVendor(vendor);
        target.setContractValue(request.getContractValue());
        target.setStartDate(request.getStartDate());
        target.setRenewalDate(request.getRenewalDate());
    }
}
