-- ============================================================
-- ZAT ASSET MANAGEMENT - PHASE II SCHEMA
-- Creates all child tables (maintenance, warranty, documents, etc.)
-- All foreign keys reference assets(id) of type UUID.
-- ============================================================

-- 1. ASSET MAINTENANCE TABLE
CREATE TABLE IF NOT EXISTS asset_maintenance (
    id BIGSERIAL PRIMARY KEY,

    asset_id UUID NOT NULL,

    maintenance_type VARCHAR(50) NOT NULL,
    scheduled_date DATE NULL,
    completed_date DATE NULL,
    performed_by VARCHAR(255) NULL,
    cost DECIMAL(12, 2) NULL,
    status VARCHAR(50) NULL,
    remarks TEXT NULL,
    next_due_date DATE NULL,
    preventive_maintenance_schedule_id UUID NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_maintenance_asset
        FOREIGN KEY (asset_id)
        REFERENCES assets(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_maintenance_type
        CHECK (maintenance_type IN ('PREVENTIVE', 'CORRECTIVE', 'PREDICTIVE', 'SCHEDULED'))
);

CREATE INDEX IF NOT EXISTS idx_maintenance_asset_id ON asset_maintenance(asset_id);
CREATE INDEX IF NOT EXISTS idx_maintenance_status ON asset_maintenance(status);
CREATE INDEX IF NOT EXISTS idx_maintenance_scheduled_date ON asset_maintenance(scheduled_date);

-- 1B. PREVENTIVE MAINTENANCE SCHEDULE
CREATE TABLE IF NOT EXISTS preventive_maintenance_schedules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    asset_id UUID NOT NULL,
    schedule_name VARCHAR(150) NOT NULL DEFAULT 'Preventive Maintenance',
    frequency_days INTEGER NOT NULL,
    last_done_date DATE NULL,
    next_due_date DATE NOT NULL,
    auto_generate_work_order BOOLEAN NOT NULL DEFAULT FALSE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    remarks TEXT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_pm_asset FOREIGN KEY (asset_id) REFERENCES assets(id) ON DELETE CASCADE,
    CONSTRAINT chk_pm_frequency CHECK (frequency_days > 0)
);

DO 894
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_maintenance_pm_schedule') THEN
        ALTER TABLE asset_maintenance ADD CONSTRAINT fk_maintenance_pm_schedule
            FOREIGN KEY (preventive_maintenance_schedule_id) REFERENCES preventive_maintenance_schedules(id) ON DELETE SET NULL;
    END IF;
END 894;

CREATE INDEX IF NOT EXISTS idx_pm_asset_id ON preventive_maintenance_schedules(asset_id);
CREATE INDEX IF NOT EXISTS idx_pm_next_due_date ON preventive_maintenance_schedules(next_due_date);
CREATE INDEX IF NOT EXISTS idx_pm_active_next_due ON preventive_maintenance_schedules(active, next_due_date);
CREATE INDEX IF NOT EXISTS idx_maintenance_pm_schedule ON asset_maintenance(preventive_maintenance_schedule_id);
CREATE UNIQUE INDEX IF NOT EXISTS ux_pm_generated_maintenance_cycle
    ON asset_maintenance(preventive_maintenance_schedule_id, scheduled_date)
    WHERE preventive_maintenance_schedule_id IS NOT NULL AND scheduled_date IS NOT NULL;

-- 2. ASSET WARRANTY TABLE
CREATE TABLE IF NOT EXISTS asset_warranty (
    id BIGSERIAL PRIMARY KEY,

    asset_id UUID NOT NULL,

    warranty_type VARCHAR(50) NOT NULL,
    provider VARCHAR(255) NULL,
    start_date DATE NULL,
    end_date DATE NULL,
    status VARCHAR(50) NOT NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_warranty_asset
        FOREIGN KEY (asset_id)
        REFERENCES assets(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_warranty_type
        CHECK (warranty_type IN ('MANUFACTURER', 'EXTENDED', 'AMC')),

    CONSTRAINT chk_warranty_status
        CHECK (status IN ('ACTIVE', 'EXPIRED', 'EXTENDED')),

    CONSTRAINT chk_warranty_dates
        CHECK (end_date >= start_date)
);

CREATE INDEX IF NOT EXISTS idx_warranty_asset_id ON asset_warranty(asset_id);
CREATE INDEX IF NOT EXISTS idx_warranty_status ON asset_warranty(status);
CREATE INDEX IF NOT EXISTS idx_warranty_end_date ON asset_warranty(end_date);

-- 3. ASSET WARRANTY COVERAGE DETAILS TABLE
CREATE TABLE IF NOT EXISTS asset_warranty_coverage_details (
    warranty_id BIGINT NOT NULL,
    detail VARCHAR(500) NOT NULL,

    CONSTRAINT fk_coverage_warranty
        FOREIGN KEY (warranty_id)
        REFERENCES asset_warranty(id)
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_coverage_warranty_id ON asset_warranty_coverage_details(warranty_id);

-- 4. ASSET DOCUMENT TABLE
CREATE TABLE IF NOT EXISTS asset_document (
    id BIGSERIAL PRIMARY KEY,

    asset_id UUID NULL,

    document_type VARCHAR(50) NOT NULL,
    document_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    uploaded_by VARCHAR(255) NULL,
    uploaded_date DATE NULL,
    expiry_date DATE NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_document_asset
        FOREIGN KEY (asset_id)
        REFERENCES assets(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_document_type
        CHECK (document_type IN ('INVOICE', 'WARRANTY_CARD', 'INSURANCE', 'MANUAL', 'AMC_CONTRACT', 'OTHER'))
);

CREATE INDEX IF NOT EXISTS idx_document_asset_id ON asset_document(asset_id);
CREATE INDEX IF NOT EXISTS idx_document_type ON asset_document(document_type);
CREATE INDEX IF NOT EXISTS idx_document_expiry_date ON asset_document(expiry_date);

-- 5. ASSET REQUEST TABLE
CREATE TABLE IF NOT EXISTS asset_request (
    id BIGSERIAL PRIMARY KEY,

    asset_id UUID NULL,

    request_type VARCHAR(50) NOT NULL,
    requested_category VARCHAR(255) NULL,
    employee_id VARCHAR(100) NOT NULL,
    employee_name VARCHAR(255) NULL,
    department VARCHAR(255) NULL,
    request_date DATE NULL,
    priority VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    approved_by VARCHAR(255) NULL,
    remarks TEXT NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_request_asset
        FOREIGN KEY (asset_id)
        REFERENCES assets(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_request_type
        CHECK (request_type IN ('NEW_ASSET', 'REPAIR', 'REPLACEMENT')),

    CONSTRAINT chk_request_priority
        CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH')),

    CONSTRAINT chk_request_status
        CHECK (status IN ('DRAFT', 'SEND', 'ACCEPT', 'REJECT'))
);

CREATE INDEX IF NOT EXISTS idx_request_asset_id ON asset_request(asset_id);
CREATE INDEX IF NOT EXISTS idx_request_status ON asset_request(status);
CREATE INDEX IF NOT EXISTS idx_request_employee_id ON asset_request(employee_id);

-- 6. ASSET AUDIT TABLE
CREATE TABLE IF NOT EXISTS asset_audit (
    id BIGSERIAL PRIMARY KEY,

    asset_id UUID NOT NULL,

    audit_date DATE NULL,
    audited_by VARCHAR(255) NULL,
    location_verified VARCHAR(255) NULL,
    condition VARCHAR(50) NULL,
    discrepancy_notes TEXT NULL,
    status VARCHAR(50) NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_audit_asset
        FOREIGN KEY (asset_id)
        REFERENCES assets(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_audit_condition
        CHECK (condition IN ('Good', 'Damaged', 'Missing', 'NeedsRepair')),

    CONSTRAINT chk_audit_status
        CHECK (status IN ('Verified', 'DiscrepancyFound'))
);

CREATE INDEX IF NOT EXISTS idx_audit_asset_id ON asset_audit(asset_id);
CREATE INDEX IF NOT EXISTS idx_audit_status ON asset_audit(status);
CREATE INDEX IF NOT EXISTS idx_audit_date ON asset_audit(audit_date);

-- ============================================================
-- END OF SCHEMA FILE
-- ============================================================