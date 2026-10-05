package com.example.assets_management.banking.bank_account.dto.request;

import com.example.assets_management.banking.bank_account.enums.BankAccountHolderType;
import com.example.assets_management.banking.bank_account.enums.BankAccountType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor
public class BankAccountRequest {
    @NotNull(message = "companyId is required") private UUID companyId;
    @NotBlank(message = "accountName is required") @Size(max=120) private String accountName;
    @NotBlank(message = "accountNumber is required")
    @Pattern(regexp = "^[0-9]{9,18}$", message = "accountNumber must contain 9 to 18 digits only")
    private String accountNumber;
    @NotNull(message = "accountType is required") private BankAccountType accountType;
    @NotNull(message = "accountHolderType is required") private BankAccountHolderType accountHolderType;
    private UUID employeeId;
    private UUID vendorId;
    @Size(max=80) private String bankName;
    @Size(max=120) private String branchName;
    @Pattern(regexp = "^[A-Z]{4}0[A-Z0-9]{6}$", message = "ifscCode must be a valid 11-character IFSC code")
    private String ifscCode;
    @Size(max=10) private String currency = "INR";
    @NotNull(message = "openingBalance is required") @DecimalMin(value="0.00", inclusive=true, message="openingBalance cannot be negative") private BigDecimal openingBalance = BigDecimal.ZERO;
    private Boolean isPrimary = false;
    @Size(max=100) private String externalAccountId;
    @Size(max=30) private String source = "INTERNAL";
    private LocalDate openedAt;
}
