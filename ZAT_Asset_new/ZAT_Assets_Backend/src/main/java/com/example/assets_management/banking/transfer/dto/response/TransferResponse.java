package com.example.assets_management.banking.transfer.dto.response;

import com.example.assets_management.banking.transfer.entity.BankTransfer;
import com.example.assets_management.banking.transfer.enums.BankTransferStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Builder
public class TransferResponse {
    private UUID id;
    private String transferReference;
    private UUID sourceAccountId;
    private UUID destinationAccountId;
    @JsonProperty("transfer_amount") private BigDecimal transferAmount;
    private String currency;
    private LocalDate transferDate;
    private String description;
    private String referenceNumber;
    private BankTransferStatus status;
    private UUID sourceTransactionId;
    private UUID destinationTransactionId;
    private String externalTransferId;
    private LocalDateTime createdAt;
}
