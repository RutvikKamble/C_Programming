package com.example.assets_management.banking.bank_account.dto.request;

import com.example.assets_management.banking.bank_account.enums.BankAccountStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class BankAccountStatusRequest {
    @NotNull(message = "status is required") private BankAccountStatus status;
}
