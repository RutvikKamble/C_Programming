package com.example.assets_management.banking.transaction.dto.response;

import com.example.assets_management.banking.transaction.entity.BankTransaction;
import com.example.assets_management.banking.transaction.enums.BankTransactionDirection;
import com.example.assets_management.banking.transaction.enums.BankTransactionCategory;
import com.example.assets_management.banking.transaction.enums.BankTransactionStatus;
import com.example.assets_management.banking.transaction.enums.BankTransactionType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Builder
public class TransactionResponse {
    private UUID id;
    private String transactionReference;
    private UUID bankAccountId;
    private BankTransactionType transactionType;
    private BankTransactionCategory category;
    private BankTransactionDirection direction;
    private BigDecimal amount;
    private String currency;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private LocalDate transactionDate;
    private String description;
    private String referenceNumber;
    private UUID relatedTransferId;
    private String referenceType;
    private String referenceId;
    private String externalTransactionId;
    private BankTransactionStatus status;
    private Boolean isDeleted;
    private LocalDateTime createdAt;
}
