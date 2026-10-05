package com.example.assets_management.amc.repository;

import com.example.assets_management.amc.entity.AmcContract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AmcContractRepository extends JpaRepository<AmcContract, UUID> {

    List<AmcContract> findByAsset_IdOrderByRenewalDateAsc(UUID assetId);

    List<AmcContract> findByCategory_IdOrderByRenewalDateAsc(UUID categoryId);

    List<AmcContract> findByRenewalDateBetweenOrderByRenewalDateAsc(LocalDate from, LocalDate to);
}
