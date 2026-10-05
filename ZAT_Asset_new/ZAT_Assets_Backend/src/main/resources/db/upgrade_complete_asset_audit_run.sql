-- Complete Asset Audit Run migration for ZAT Asset Management.
-- Safe to run on PostgreSQL. Existing asset_audit/asset_audits data is preserved.

CREATE TABLE IF NOT EXISTS audit_run (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    company_id UUID NULL,
    branch_id UUID NULL,
    category_id UUID NULL,
    audited_by VARCHAR(150),
    audit_date DATE,
    status VARCHAR(30) NOT NULL DEFAULT 'OPEN',
    started_at TIMESTAMP,
    closed_at TIMESTAMP,
    total_expected_assets INTEGER NOT NULL DEFAULT 0,
    total_verified_assets INTEGER NOT NULL DEFAULT 0,
    total_missing_assets INTEGER NOT NULL DEFAULT 0,
    total_damaged_assets INTEGER NOT NULL DEFAULT 0,
    total_discrepancies INTEGER NOT NULL DEFAULT 0,
    notes TEXT
);

ALTER TABLE audit_run ADD COLUMN IF NOT EXISTS company_id UUID;
ALTER TABLE audit_run ADD COLUMN IF NOT EXISTS branch_id UUID;
ALTER TABLE audit_run ADD COLUMN IF NOT EXISTS category_id UUID;
ALTER TABLE audit_run ADD COLUMN IF NOT EXISTS audit_date DATE;
ALTER TABLE audit_run ADD COLUMN IF NOT EXISTS total_expected_assets INTEGER NOT NULL DEFAULT 0;
ALTER TABLE audit_run ADD COLUMN IF NOT EXISTS total_verified_assets INTEGER NOT NULL DEFAULT 0;
ALTER TABLE audit_run ADD COLUMN IF NOT EXISTS total_missing_assets INTEGER NOT NULL DEFAULT 0;
ALTER TABLE audit_run ADD COLUMN IF NOT EXISTS total_damaged_assets INTEGER NOT NULL DEFAULT 0;
ALTER TABLE audit_run ADD COLUMN IF NOT EXISTS total_discrepancies INTEGER NOT NULL DEFAULT 0;
ALTER TABLE audit_run ADD COLUMN IF NOT EXISTS notes TEXT;

CREATE INDEX IF NOT EXISTS idx_audit_run_company ON audit_run(company_id);
CREATE INDEX IF NOT EXISTS idx_audit_run_branch ON audit_run(branch_id);
CREATE INDEX IF NOT EXISTS idx_audit_run_category ON audit_run(category_id);
CREATE INDEX IF NOT EXISTS idx_audit_run_status ON audit_run(status);

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_audit_run_company') THEN
        ALTER TABLE audit_run ADD CONSTRAINT fk_audit_run_company FOREIGN KEY (company_id) REFERENCES companies(id);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_audit_run_branch') THEN
        ALTER TABLE audit_run ADD CONSTRAINT fk_audit_run_branch FOREIGN KEY (branch_id) REFERENCES branches(id);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_audit_run_category') THEN
        ALTER TABLE audit_run ADD CONSTRAINT fk_audit_run_category FOREIGN KEY (category_id) REFERENCES asset_categories(id);
    END IF;
END $$;

CREATE TABLE IF NOT EXISTS audit_scan (
    id BIGSERIAL PRIMARY KEY,
    audit_run_id BIGINT NOT NULL,
    asset_id UUID NOT NULL,
    scanned_code VARCHAR(100),
    expected_location VARCHAR(150),
    found_location VARCHAR(150),
    expected_owner_id UUID,
    actual_owner_id UUID,
    expected_status VARCHAR(40),
    actual_condition VARCHAR(50),
    result VARCHAR(40) DEFAULT 'PENDING',
    discrepancy BOOLEAN NOT NULL DEFAULT FALSE,
    discrepancy_details TEXT,
    verified_at TIMESTAMP
);

ALTER TABLE audit_scan ADD COLUMN IF NOT EXISTS expected_owner_id UUID;
ALTER TABLE audit_scan ADD COLUMN IF NOT EXISTS actual_owner_id UUID;
ALTER TABLE audit_scan ADD COLUMN IF NOT EXISTS expected_status VARCHAR(40);
ALTER TABLE audit_scan ADD COLUMN IF NOT EXISTS actual_condition VARCHAR(50);
ALTER TABLE audit_scan ADD COLUMN IF NOT EXISTS result VARCHAR(40) DEFAULT 'PENDING';
ALTER TABLE audit_scan ADD COLUMN IF NOT EXISTS discrepancy BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE audit_scan ADD COLUMN IF NOT EXISTS discrepancy_details TEXT;
ALTER TABLE audit_scan ADD COLUMN IF NOT EXISTS verified_at TIMESTAMP;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_audit_scan_run') THEN
        ALTER TABLE audit_scan ADD CONSTRAINT fk_audit_scan_run FOREIGN KEY (audit_run_id) REFERENCES audit_run(id) ON DELETE CASCADE;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_audit_scan_asset') THEN
        ALTER TABLE audit_scan ADD CONSTRAINT fk_audit_scan_asset FOREIGN KEY (asset_id) REFERENCES assets(id) ON DELETE CASCADE;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_audit_scan_expected_owner') THEN
        ALTER TABLE audit_scan ADD CONSTRAINT fk_audit_scan_expected_owner FOREIGN KEY (expected_owner_id) REFERENCES users(id);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_audit_scan_actual_owner') THEN
        ALTER TABLE audit_scan ADD CONSTRAINT fk_audit_scan_actual_owner FOREIGN KEY (actual_owner_id) REFERENCES users(id);
    END IF;
END $$;

CREATE UNIQUE INDEX IF NOT EXISTS uk_audit_scan_run_asset ON audit_scan(audit_run_id, asset_id);
CREATE INDEX IF NOT EXISTS idx_audit_scan_run ON audit_scan(audit_run_id);
CREATE INDEX IF NOT EXISTS idx_audit_scan_asset ON audit_scan(asset_id);
CREATE INDEX IF NOT EXISTS idx_audit_scan_result ON audit_scan(result);
CREATE INDEX IF NOT EXISTS idx_audit_scan_discrepancy ON audit_scan(discrepancy);

-- Existing asset_audit remains untouched as the legacy/individual audit record API.
