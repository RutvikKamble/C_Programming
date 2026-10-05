package com.example.assets_management.banking.bank_account.service;

import com.example.assets_management.banking.bank_account.dto.request.BankAccountRequest;
import com.example.assets_management.banking.bank_account.dto.request.BankAccountStatusRequest;
import com.example.assets_management.banking.bank_account.dto.response.BankAccountResponse;
import com.example.assets_management.banking.transaction.dto.response.TransactionResponse;
import com.example.assets_management.banking.transfer.dto.response.TransferResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface BankAccountService {
    BankAccountResponse create(BankAccountRequest request);
    List<BankAccountResponse> findAll(UUID companyId);
    BankAccountResponse findById(UUID id);
    BankAccountResponse update(UUID id, BankAccountRequest request);
    BankAccountResponse changeStatus(UUID id, BankAccountStatusRequest request);
    BigDecimal getBalance(UUID id);
    List<TransactionResponse> getTransactions(UUID id);
    List<TransferResponse> getTransfers(UUID id);
}
