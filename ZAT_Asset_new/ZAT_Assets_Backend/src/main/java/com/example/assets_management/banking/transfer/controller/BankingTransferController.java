package com.example.assets_management.banking.transfer.controller;

import com.example.assets_management.banking.transfer.dto.request.TransferRequest;
import com.example.assets_management.banking.transfer.dto.response.TransferResponse;
import com.example.assets_management.banking.transfer.service.BankingTransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/banking/transfers")
@RequiredArgsConstructor
public class BankingTransferController {
    private final BankingTransferService service;
    @PreAuthorize("hasAuthority('Asset.Banking.Manage')")
    @PostMapping public ResponseEntity<TransferResponse> transfer(@Valid @RequestBody TransferRequest request){return ResponseEntity.status(HttpStatus.CREATED).body(service.transfer(request));}
    @PreAuthorize("hasAuthority('Asset.Banking.View')")
    @GetMapping public ResponseEntity<List<TransferResponse>> findAll(){return ResponseEntity.ok(service.findAll());}
    @PreAuthorize("hasAuthority('Asset.Banking.View')")
    @GetMapping("/{id}") public ResponseEntity<TransferResponse> findById(@PathVariable UUID id){return ResponseEntity.ok(service.findById(id));}
    @PreAuthorize("hasAuthority('Asset.Banking.View')")
    @GetMapping("/account/{accountId}") public ResponseEntity<List<TransferResponse>> findByAccount(@PathVariable UUID accountId){return ResponseEntity.ok(service.findByAccount(accountId));}
    @PreAuthorize("hasAuthority('Asset.Banking.Manage')")
    @PostMapping("/{id}/reverse") public ResponseEntity<TransferResponse> reverse(@PathVariable UUID id){return ResponseEntity.ok(service.reverse(id));}
}
