package com.example.assets_management.banking.transfer.entity;

import com.example.assets_management.banking.bank_account.entity.BankAccount;
import com.example.assets_management.banking.transaction.entity.BankTransaction;
import com.example.assets_management.banking.transfer.enums.BankTransferStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bank_transfers", uniqueConstraints = {
        @UniqueConstraint(name = "uk_bank_transfer_reference", columnNames = "reference_number")
})
@Getter
@Setter
@NoArgsConstructor
public class BankTransfer {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "transfer_reference", unique = true, nullable = false, length = 100)
    private String transferReference;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "from_account_id", nullable = false)
    private BankAccount fromAccount;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "to_account_id", nullable = false)
    private BankAccount toAccount;

    @Column(name = "transfer_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal transferAmount;

    @Column(name = "currency", nullable = false, length = 10)
    private String currency = "INR";

    @Column(name = "transfer_date", nullable = false)
    private LocalDate transferDate;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "reference_number", nullable = false, length = 100)
    private String referenceNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private BankTransferStatus status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_transaction_id", unique = true)
    private BankTransaction sourceTransaction;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_transaction_id", unique = true)
    private BankTransaction destinationTransaction;

    @Column(name = "external_transfer_id", length = 100)
    private String externalTransferId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;
}
