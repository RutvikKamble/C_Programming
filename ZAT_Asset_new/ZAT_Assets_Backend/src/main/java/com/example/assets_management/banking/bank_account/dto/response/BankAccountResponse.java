package com.example.assets_management.banking.bank_account.dto.response;

import com.example.assets_management.banking.bank_account.enums.BankAccountHolderType;
import com.example.assets_management.banking.bank_account.enums.BankAccountStatus;
import com.example.assets_management.banking.bank_account.enums.BankAccountType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Builder
public class BankAccountResponse {
    private UUID id;
    private UUID companyId;
    private String accountNumber;
    private String accountName;
    private BankAccountType accountType;
    private BankAccountHolderType accountHolderType;
    private UUID employeeId;
    private UUID vendorId;
    private String bankName;
    private String branchName;
    private String ifscCode;
    private String currency;
    private BigDecimal openingBalance;
    private BigDecimal currentBalance;
    private BankAccountStatus status;
    private Boolean isPrimary;
    private String externalAccountId;
    private String source;
    private LocalDate openedAt;
    private LocalDate closedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
