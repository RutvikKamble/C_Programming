package com.example.assets_management.banking.bank_account.controller;

import com.example.assets_management.banking.bank_account.dto.request.BankAccountRequest;
import com.example.assets_management.banking.bank_account.dto.request.BankAccountStatusRequest;
import com.example.assets_management.banking.bank_account.dto.response.BankAccountResponse;
import com.example.assets_management.banking.bank_account.service.BankAccountService;
import com.example.assets_management.banking.transaction.dto.response.TransactionResponse;
import com.example.assets_management.banking.transfer.dto.response.TransferResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/banking/accounts")
@RequiredArgsConstructor
public class BankAccountController {
    private final BankAccountService service;
    @PreAuthorize("hasAuthority('Asset.Banking.Manage')")
    @PostMapping public ResponseEntity<BankAccountResponse> create(@Valid @RequestBody BankAccountRequest request){return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));}
    @PreAuthorize("hasAuthority('Asset.Banking.View')")
    @GetMapping public ResponseEntity<List<BankAccountResponse>> findAll(@RequestParam(required=false) UUID companyId){return ResponseEntity.ok(service.findAll(companyId));}
    @PreAuthorize("hasAuthority('Asset.Banking.View')")
    @GetMapping("/{id}") public ResponseEntity<BankAccountResponse> findById(@PathVariable UUID id){return ResponseEntity.ok(service.findById(id));}
    @PreAuthorize("hasAuthority('Asset.Banking.Manage')")
    @PatchMapping("/{id}") public ResponseEntity<BankAccountResponse> update(@PathVariable UUID id,@Valid @RequestBody BankAccountRequest request){return ResponseEntity.ok(service.update(id,request));}
    @PreAuthorize("hasAuthority('Asset.Banking.Manage')")
    @PatchMapping("/{id}/status") public ResponseEntity<BankAccountResponse> status(@PathVariable UUID id,@Valid @RequestBody BankAccountStatusRequest request){return ResponseEntity.ok(service.changeStatus(id,request));}
    @PreAuthorize("hasAuthority('Asset.Banking.View')")
    @GetMapping("/{id}/balance") public ResponseEntity<BigDecimal> balance(@PathVariable UUID id){return ResponseEntity.ok(service.getBalance(id));}
    @PreAuthorize("hasAuthority('Asset.Banking.View')")
    @GetMapping("/{id}/transactions") public ResponseEntity<List<TransactionResponse>> transactions(@PathVariable UUID id){return ResponseEntity.ok(service.getTransactions(id));}
    @PreAuthorize("hasAuthority('Asset.Banking.View')")
    @GetMapping("/{id}/transfers") public ResponseEntity<List<TransferResponse>> transfers(@PathVariable UUID id){return ResponseEntity.ok(service.getTransfers(id));}
}
