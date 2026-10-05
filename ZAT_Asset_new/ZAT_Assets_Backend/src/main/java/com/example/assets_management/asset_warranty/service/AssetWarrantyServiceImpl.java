package com.example.assets_management.asset_warranty.service;

import com.example.assets_management.asset_warranty.entity.AssetWarranty;
import com.example.assets_management.asset_warranty.repository.AssetWarrantyRepository;
import com.example.assets_management.exception.ResourceNotFoundException;
import com.example.assets_management.asset_history.enums.AssetHistoryEventType;
import com.example.assets_management.asset_history.service.AssetHistoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class AssetWarrantyServiceImpl implements AssetWarrantyService {

    private final AssetWarrantyRepository warrantyRepository;
    private final AssetHistoryService historyService;

    public AssetWarrantyServiceImpl(AssetWarrantyRepository warrantyRepository, AssetHistoryService historyService) {
        this.warrantyRepository = warrantyRepository;
        this.historyService = historyService;
    }

    @Override
    @Transactional
    public AssetWarranty createWarranty(AssetWarranty warranty) {
        AssetWarranty saved = warrantyRepository.save(warranty);
        if (saved.getAsset() != null) {
            historyService.recordEvent(saved.getAsset(), AssetHistoryEventType.WARRANTY_CREATED, "warrantyId",
                    null, String.valueOf(saved.getId()), "Warranty created", "SYSTEM");
        }
        return saved;
    }

    @Override
    @Transactional
    public AssetWarranty updateWarranty(Long id, AssetWarranty warranty) {
        AssetWarranty existing = warrantyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AssetWarranty not found with id: " + id));

        existing.setAsset(warranty.getAsset());
        existing.setWarrantyType(warranty.getWarrantyType());
        existing.setProvider(warranty.getProvider());
        existing.setStartDate(warranty.getStartDate());
        // endDate is not updatable (column definition), so we don't set it here
        existing.setCoverageDetails(warranty.getCoverageDetails());
        existing.setStatus(warranty.getStatus());

        AssetWarranty saved = warrantyRepository.save(existing);
        if (saved.getAsset() != null) {
            historyService.recordEvent(saved.getAsset(), AssetHistoryEventType.WARRANTY_UPDATED, "warrantyId",
                    String.valueOf(saved.getId()), String.valueOf(saved.getId()), "Warranty updated", "SYSTEM");
        }
        return saved;
    }

    @Override
    @Transactional
    public void deleteWarranty(Long id) {
        AssetWarranty existing = getWarrantyById(id);
        if (existing.getAsset() != null) {
            historyService.recordEvent(existing.getAsset(), AssetHistoryEventType.WARRANTY_DELETED, "warrantyId",
                    String.valueOf(id), null, "Warranty deleted", "SYSTEM");
        }
        warrantyRepository.delete(existing);
    }

    @Override
    public AssetWarranty getWarrantyById(Long id) {
        return warrantyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AssetWarranty not found with id: " + id));
    }

    @Override
    public List<AssetWarranty> getAllWarranty() {
        return warrantyRepository.findAll();
    }

    @Override
    public List<AssetWarranty> getWarrantyByAssetId(UUID assetId) {
        return warrantyRepository.findByAsset_Id(assetId);
    }

    @Override
    public List<AssetWarranty> getExpiringSoonWarranties() {
        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysLater = today.plusDays(30);
        return warrantyRepository.findByEndDateBetween(today, thirtyDaysLater);
    }
}