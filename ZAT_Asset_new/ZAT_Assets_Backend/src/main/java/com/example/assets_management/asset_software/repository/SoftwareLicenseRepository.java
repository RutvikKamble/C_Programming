package com.example.assets_management.asset_software.repository;

import com.example.assets_management.asset_software.entity.SoftwareLicense;
import com.example.assets_management.asset_software.enums.SoftwareLicenseStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SoftwareLicenseRepository extends JpaRepository<SoftwareLicense, UUID> {
    Optional<SoftwareLicense> findByAsset_Id(UUID assetId);
    boolean existsByAsset_Id(UUID assetId);
    List<SoftwareLicense> findAllByOrderByExpiryDateAsc();
    List<SoftwareLicense> findByExpiryDateBetweenOrderByExpiryDateAsc(LocalDate from, LocalDate to);

    @Query("select s from SoftwareLicense s where " +
            "(:search is null or lower(s.name) like lower(concat('%', :search, '%')) or lower(s.publisher) like lower(concat('%', :search, '%')) or lower(s.manufacturer) like lower(concat('%', :search, '%')) or lower(s.orderNumber) like lower(concat('%', :search, '%'))) and " +
            "(:publisher is null or lower(s.publisher) = lower(:publisher)) and " +
            "(:category is null or lower(s.licenseCategory) = lower(:category)) and " +
            "(:status is null or s.status = :status) and " +
            "(:maintained is null or s.maintained = :maintained) and " +
            "(:reassignable is null or s.reassignable = :reassignable)")
    Page<SoftwareLicense> search(@Param("search") String search,
                                 @Param("publisher") String publisher,
                                 @Param("category") String category,
                                 @Param("status") SoftwareLicenseStatus status,
                                 @Param("maintained") Boolean maintained,
                                 @Param("reassignable") Boolean reassignable,
                                 Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from SoftwareLicense s where s.id = :id")
    Optional<SoftwareLicense> findByIdForUpdate(@Param("id") UUID id);
}
