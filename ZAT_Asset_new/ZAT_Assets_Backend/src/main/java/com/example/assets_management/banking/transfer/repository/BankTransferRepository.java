package com.example.assets_management.banking.transfer.repository;

import com.example.assets_management.banking.transfer.entity.BankTransfer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BankTransferRepository extends JpaRepository<BankTransfer, UUID> {
    List<BankTransfer> findByFromAccount_IdOrToAccount_IdOrderByTransferDateDescCreatedAtDesc(UUID fromAccountId, UUID toAccountId);
}
