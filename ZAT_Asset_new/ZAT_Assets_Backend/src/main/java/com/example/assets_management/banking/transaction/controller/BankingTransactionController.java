package com.example.assets_management.banking.transaction.controller;

import com.example.assets_management.banking.transaction.dto.request.TransactionRequest;
import com.example.assets_management.banking.transaction.dto.response.TransactionResponse;
import com.example.assets_management.banking.transaction.service.BankingTransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/banking/transactions")
@RequiredArgsConstructor
public class BankingTransactionController {
    private final BankingTransactionService service;
    @PreAuthorize("hasAuthority('Asset.Banking.Manage')")
    @PostMapping("/credit") public ResponseEntity<TransactionResponse> credit(@Valid @RequestBody TransactionRequest request){return ResponseEntity.status(HttpStatus.CREATED).body(service.credit(request));}
    @PreAuthorize("hasAuthority('Asset.Banking.Manage')")
    @PostMapping("/debit") public ResponseEntity<TransactionResponse> debit(@Valid @RequestBody TransactionRequest request){return ResponseEntity.status(HttpStatus.CREATED).body(service.debit(request));}
    @PreAuthorize("hasAuthority('Asset.Banking.View')")
    @GetMapping public ResponseEntity<List<TransactionResponse>> findAll(){return ResponseEntity.ok(service.findAll());}
    @PreAuthorize("hasAuthority('Asset.Banking.View')")
    @GetMapping("/{id}") public ResponseEntity<TransactionResponse> findById(@PathVariable UUID id){return ResponseEntity.ok(service.findById(id));}
    @PreAuthorize("hasAuthority('Asset.Banking.View')")
    @GetMapping("/account/{accountId}") public ResponseEntity<List<TransactionResponse>> findByAccount(@PathVariable UUID accountId){return ResponseEntity.ok(service.findByAccount(accountId));}
    @PreAuthorize("hasAuthority('Asset.Banking.Manage')")
    @PostMapping("/{id}/reverse") public ResponseEntity<TransactionResponse> reverse(@PathVariable UUID id){return ResponseEntity.ok(service.reverse(id));}
}
