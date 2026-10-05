package com.example.assets_management.asset_insurance.repository;

import com.example.assets_management.asset_insurance.entity.AssetInsurance;
import com.example.assets_management.asset_insurance.enums.InsuranceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AssetInsuranceRepository extends JpaRepository<AssetInsurance, UUID> {

    List<AssetInsurance> findByAsset_IdOrderByExpiryDateAsc(UUID assetId);

    List<AssetInsurance> findByExpiryDateBetweenOrderByExpiryDateAsc(LocalDate from, LocalDate to);

    List<AssetInsurance> findByStatusOrderByExpiryDateAsc(InsuranceStatus status);

    boolean existsByPolicyNumberIgnoreCase(String policyNumber);

    boolean existsByPolicyNumberIgnoreCaseAndIdNot(String policyNumber, UUID id);
}
