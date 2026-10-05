package com.example.assets_management.banking.transfer.service;

import com.example.assets_management.banking.transfer.dto.request.TransferRequest;
import com.example.assets_management.banking.transfer.dto.response.TransferResponse;

import java.util.List;
import java.util.UUID;

public interface BankingTransferService {
    TransferResponse transfer(TransferRequest request);
    List<TransferResponse> findAll();
    TransferResponse findById(UUID id);
    List<TransferResponse> findByAccount(UUID accountId);
    TransferResponse reverse(UUID id);
}
