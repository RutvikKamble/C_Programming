package com.example.assets_management.amc.service;

import com.example.assets_management.amc.entity.AmcContract;

import java.util.List;
import java.util.UUID;

public interface AmcContractService {
    List<AmcContract> findAll();
    AmcContract findById(UUID id);
    AmcContract create(AmcContract request);
    AmcContract update(UUID id, AmcContract request);
    void delete(UUID id);
    List<AmcContract> findByAsset(UUID assetId);
    List<AmcContract> findByCategory(UUID categoryId);
    List<AmcContract> findRenewals(int days);
}
