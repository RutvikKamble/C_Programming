package com.example.assets_management.banking.transaction.repository;

import com.example.assets_management.banking.transaction.entity.BankTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BankTransactionRepository extends JpaRepository<BankTransaction, UUID> {
    List<BankTransaction> findByBankAccount_IdAndIsDeletedFalseOrderByTransactionDateDescCreatedAtDesc(UUID accountId);
    List<BankTransaction> findAllByIsDeletedFalseOrderByTransactionDateDescCreatedAtDesc();

    boolean existsByReferenceTypeAndReferenceIdAndCategoryAndStatusAndIsDeletedFalse(
            String referenceType, String referenceId,
            com.example.assets_management.banking.transaction.enums.BankTransactionCategory category,
            com.example.assets_management.banking.transaction.enums.BankTransactionStatus status);

}
