package com.example.assets_management.banking.transfer.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class TransferRequest {
    @NotNull(message="sourceAccountId is required") private UUID sourceAccountId;
    @NotNull(message="destinationAccountId is required") private UUID destinationAccountId;
    @JsonProperty("transfer_amount")
    @NotNull(message="transfer_amount is required")
    @DecimalMin(value="0.01", inclusive=true, message="transfer_amount must be greater than zero")
    private BigDecimal transferAmount;
    @Size(max=10) private String currency = "INR";
    private LocalDate transferDate;
    @Size(max=255) private String description;
    @Size(max=100) private String referenceNumber;
}
