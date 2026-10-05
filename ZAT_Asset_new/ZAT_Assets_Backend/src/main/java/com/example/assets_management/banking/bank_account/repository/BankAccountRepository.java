package com.example.assets_management.banking.bank_account.repository;

import com.example.assets_management.banking.bank_account.entity.BankAccount;
import com.example.assets_management.banking.bank_account.enums.BankAccountStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BankAccountRepository extends JpaRepository<BankAccount, UUID> {
    List<BankAccount> findByCompany_IdOrderByAccountNameAsc(UUID companyId);
    Optional<BankAccount> findByCompany_IdAndAccountNumber(UUID companyId, String accountNumber);
    boolean existsByCompany_IdAndAccountNumber(UUID companyId, String accountNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from BankAccount a where a.id = :id")
    Optional<BankAccount> findByIdForUpdate(@Param("id") UUID id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<BankAccount> findByCompany_IdAndIsPrimaryTrueAndStatusOrderById(
            UUID companyId, BankAccountStatus status);
}
