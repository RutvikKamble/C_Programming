package com.example.assets_management.banking.transaction.service;

import com.example.assets_management.banking.transaction.dto.request.TransactionRequest;
import com.example.assets_management.banking.transaction.dto.response.TransactionResponse;
import com.example.assets_management.asset_requests.entity.AssetRequest;

import java.util.List;
import java.util.UUID;

public interface BankingTransactionService {
    TransactionResponse credit(TransactionRequest request);
    TransactionResponse debit(TransactionRequest request);
    List<TransactionResponse> findAll();
    TransactionResponse findById(UUID id);
    List<TransactionResponse> findByAccount(UUID accountId);
    TransactionResponse reverse(UUID id);
    void processAssetPurchase(AssetRequest request);
}

/*
package com.example.assets_management.banking.transaction.service;

import com.example.assets_management.banking.transaction.dto.request.TransactionRequest;
import com.example.assets_management.banking.transaction.dto.response.TransactionResponse;
import com.example.assets_management.asset_master.entity.Asset;

import java.util.List;
import java.util.UUID;

public interface BankingTransactionService {
    TransactionResponse credit(TransactionRequest request);
    TransactionResponse debit(TransactionRequest request);
    List<TransactionResponse> findAll();
    TransactionResponse findById(UUID id);
    List<TransactionResponse> findByAccount(UUID accountId);
    TransactionResponse reverse(UUID id);
    void processAssetPurchase(Asset asset);
}
*/