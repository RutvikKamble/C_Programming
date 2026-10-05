package com.example.assets_management.banking.transaction.dto.request;

import com.example.assets_management.banking.transaction.enums.BankTransactionCategory;
import com.example.assets_management.banking.transaction.enums.BankTransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor
public class TransactionRequest {
    @NotNull(message="bankAccountId is required") private UUID bankAccountId;
    @NotNull(message="transactionType is required") private BankTransactionType transactionType;
    private BankTransactionCategory category = BankTransactionCategory.OTHER;
    @NotNull(message="amount is required") @DecimalMin(value="0.01", inclusive=true, message="amount must be greater than zero") private BigDecimal amount;
    @Size(max=10) private String currency = "INR";
    private LocalDate transactionDate;
    @Size(max=255) private String description;
    @Size(max=100) private String referenceNumber;
    @Size(max=40) private String referenceType;
    @Size(max=100) private String referenceId;
    @Size(max=100) private String externalTransactionId;
}
