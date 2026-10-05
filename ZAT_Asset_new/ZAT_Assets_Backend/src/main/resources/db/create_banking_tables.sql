-- ZAT Banking module schema. Hibernate ddl-auto=update can also create these tables during development.
CREATE TABLE IF NOT EXISTS bank_accounts (
    id UUID PRIMARY KEY,
    company_id UUID NOT NULL REFERENCES companies(id),
    account_number VARCHAR(40) NOT NULL,
    account_name VARCHAR(120) NOT NULL,
    account_type VARCHAR(20) NOT NULL,
    account_holder_type VARCHAR(20) NOT NULL,
    employee_id UUID NULL REFERENCES users(id),
    vendor_id UUID NULL REFERENCES vendors(id),
    bank_name VARCHAR(80),
    branch_name VARCHAR(120),
    ifsc_code VARCHAR(20),
    currency VARCHAR(10) NOT NULL DEFAULT 'INR',
    opening_balance NUMERIC(19,2) NOT NULL DEFAULT 0,
    current_balance NUMERIC(19,2) NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    external_account_id VARCHAR(100),
    source VARCHAR(30) NOT NULL DEFAULT 'INTERNAL',
    opened_at DATE,
    closed_at DATE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT uk_bank_account_company_number UNIQUE (company_id, account_number),
    CONSTRAINT ck_bank_account_holder CHECK (
      (account_holder_type='COMPANY' AND employee_id IS NULL AND vendor_id IS NULL) OR
      (account_holder_type='EMPLOYEE' AND employee_id IS NOT NULL AND vendor_id IS NULL) OR
      (account_holder_type='VENDOR' AND employee_id IS NULL AND vendor_id IS NOT NULL)
    )
);

CREATE TABLE IF NOT EXISTS bank_transfers (
    id UUID PRIMARY KEY,
    transfer_reference VARCHAR(100) NOT NULL UNIQUE,
    from_account_id UUID NOT NULL REFERENCES bank_accounts(id),
    to_account_id UUID NOT NULL REFERENCES bank_accounts(id),
    transfer_amount NUMERIC(19,2) NOT NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'INR',
    transfer_date DATE NOT NULL,
    description VARCHAR(255),
    reference_number VARCHAR(100) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL,
    source_transaction_id UUID NULL,
    destination_transaction_id UUID NULL,
    external_transfer_id VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_bank_transfer_amount_positive CHECK (transfer_amount > 0),
    CONSTRAINT ck_bank_transfer_different_accounts CHECK (from_account_id <> to_account_id)
);

CREATE TABLE IF NOT EXISTS bank_transactions (
    id UUID PRIMARY KEY,
    transaction_reference VARCHAR(100) NOT NULL UNIQUE,
    bank_account_id UUID NOT NULL REFERENCES bank_accounts(id),
    transaction_type VARCHAR(30) NOT NULL,
    category VARCHAR(30) NOT NULL DEFAULT 'OTHER',
    direction VARCHAR(10) NOT NULL,
    amount NUMERIC(19,2) NOT NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'INR',
    balance_before NUMERIC(19,2) NOT NULL,
    balance_after NUMERIC(19,2) NOT NULL,
    transaction_date DATE NOT NULL,
    description VARCHAR(255),
    reference_number VARCHAR(100) NOT NULL UNIQUE,
    related_transfer_id UUID NULL REFERENCES bank_transfers(id),
    reference_type VARCHAR(40),
    reference_id VARCHAR(100),
    external_transaction_id VARCHAR(100),
    status VARCHAR(20) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_bank_transaction_amount_positive CHECK (amount > 0),
    CONSTRAINT ck_bank_transaction_direction CHECK (direction IN ('CREDIT','DEBIT'))
);

DO $$ BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_bank_transfer_source_txn') THEN
    ALTER TABLE bank_transfers ADD CONSTRAINT fk_bank_transfer_source_txn FOREIGN KEY (source_transaction_id) REFERENCES bank_transactions(id);
  END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_bank_transfer_destination_txn') THEN
    ALTER TABLE bank_transfers ADD CONSTRAINT fk_bank_transfer_destination_txn FOREIGN KEY (destination_transaction_id) REFERENCES bank_transactions(id);
  END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_bank_accounts_company ON bank_accounts(company_id);
CREATE INDEX IF NOT EXISTS idx_bank_transactions_account_date ON bank_transactions(bank_account_id, transaction_date);
CREATE INDEX IF NOT EXISTS idx_bank_transactions_reference ON bank_transactions(reference_type, reference_id);
CREATE INDEX IF NOT EXISTS idx_bank_transfers_source ON bank_transfers(from_account_id, transfer_date);
CREATE INDEX IF NOT EXISTS idx_bank_transfers_destination ON bank_transfers(to_account_id, transfer_date);
