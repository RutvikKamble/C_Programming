-- ============================================================
-- ZAT ASSET MANAGEMENT
-- PHASE II - OPERATIONS
-- SUPABASE POSTGRESQL
-- UPDATED: users table used instead of org_users
-- ============================================================


-- ============================================================
-- 1. WORK ORDERS / MAINTENANCE
-- ============================================================

CREATE TABLE IF NOT EXISTS work_orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    asset_id UUID NOT NULL,

    type VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'CREATED',

    assigned_vendor_id UUID NULL,
    assigned_technician UUID NULL,

    scheduled_date DATE NULL,
    cost NUMERIC(12, 2) NULL,

    completion_notes TEXT NULL,

    created_by UUID NULL,
    closed_by UUID NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    closed_at TIMESTAMPTZ NULL,

    CONSTRAINT fk_work_orders_asset
        FOREIGN KEY (asset_id)
        REFERENCES assets(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_work_orders_vendor
        FOREIGN KEY (assigned_vendor_id)
        REFERENCES vendors(id)
        ON DELETE SET NULL,

    CONSTRAINT fk_work_orders_technician
        FOREIGN KEY (assigned_technician)
        REFERENCES users(id)
        ON DELETE SET NULL,

    CONSTRAINT fk_work_orders_created_by
        FOREIGN KEY (created_by)
        REFERENCES users(id)
        ON DELETE SET NULL,

    CONSTRAINT fk_work_orders_closed_by
        FOREIGN KEY (closed_by)
        REFERENCES users(id)
        ON DELETE SET NULL,

    CONSTRAINT chk_work_orders_type
        CHECK (
            type IN (
                'CORRECTIVE',
                'PREVENTIVE',
                'INSPECTION'
            )
        ),

    CONSTRAINT chk_work_orders_status
        CHECK (
            status IN (
                'CREATED',
                'ASSIGNED',
                'IN_PROGRESS',
                'COMPLETED',
                'CLOSED'
            )
        )
);

CREATE INDEX IF NOT EXISTS idx_work_orders_asset_id
    ON work_orders(asset_id);

CREATE INDEX IF NOT EXISTS idx_work_orders_status
    ON work_orders(status);

CREATE INDEX IF NOT EXISTS idx_work_orders_scheduled_date
    ON work_orders(scheduled_date);

CREATE INDEX IF NOT EXISTS idx_work_orders_vendor
    ON work_orders(assigned_vendor_id);

CREATE INDEX IF NOT EXISTS idx_work_orders_technician
    ON work_orders(assigned_technician);


-- Existing ZAT Asset Maintenance execution table used by the application.
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
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_maintenance_asset
        FOREIGN KEY (asset_id) REFERENCES assets(id) ON DELETE CASCADE,
    CONSTRAINT chk_maintenance_type
        CHECK (maintenance_type IN ('PREVENTIVE', 'CORRECTIVE', 'PREDICTIVE', 'SCHEDULED'))
);

CREATE INDEX IF NOT EXISTS idx_maintenance_asset_id ON asset_maintenance(asset_id);
CREATE INDEX IF NOT EXISTS idx_maintenance_status ON asset_maintenance(status);
CREATE INDEX IF NOT EXISTS idx_maintenance_scheduled_date ON asset_maintenance(scheduled_date);


-- ============================================================
-- 2. PREVENTIVE MAINTENANCE SCHEDULE
-- ============================================================

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
    CONSTRAINT fk_pm_asset
        FOREIGN KEY (asset_id)
        REFERENCES assets(id)
        ON DELETE CASCADE,
    CONSTRAINT chk_pm_frequency
        CHECK (frequency_days > 0)
);

CREATE INDEX IF NOT EXISTS idx_pm_asset_id
    ON preventive_maintenance_schedules(asset_id);

CREATE INDEX IF NOT EXISTS idx_pm_next_due_date
    ON preventive_maintenance_schedules(next_due_date);
CREATE INDEX IF NOT EXISTS idx_pm_active_next_due
    ON preventive_maintenance_schedules(active, next_due_date);

ALTER TABLE asset_maintenance
    ADD COLUMN IF NOT EXISTS preventive_maintenance_schedule_id UUID NULL;

DO 706
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_maintenance_pm_schedule') THEN
        ALTER TABLE asset_maintenance
            ADD CONSTRAINT fk_maintenance_pm_schedule
            FOREIGN KEY (preventive_maintenance_schedule_id)
            REFERENCES preventive_maintenance_schedules(id)
            ON DELETE SET NULL;
    END IF;
END 706;

CREATE INDEX IF NOT EXISTS idx_maintenance_pm_schedule
    ON asset_maintenance(preventive_maintenance_schedule_id);

CREATE UNIQUE INDEX IF NOT EXISTS ux_pm_generated_maintenance_cycle
    ON asset_maintenance(preventive_maintenance_schedule_id, scheduled_date)
    WHERE preventive_maintenance_schedule_id IS NOT NULL AND scheduled_date IS NOT NULL;


-- ============================================================
-- 3. WARRANTIES
-- ============================================================

CREATE TABLE IF NOT EXISTS warranties (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    asset_id UUID NOT NULL,

    start_date DATE NOT NULL,
    end_date DATE NOT NULL,

    service_provider VARCHAR(255) NULL,

    alert_days_before INTEGER NOT NULL DEFAULT 30,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_warranties_asset
        FOREIGN KEY (asset_id)
        REFERENCES assets(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_warranty_dates
        CHECK (end_date >= start_date),

    CONSTRAINT chk_warranty_alert_days
        CHECK (alert_days_before >= 0)
);

CREATE INDEX IF NOT EXISTS idx_warranties_asset_id
    ON warranties(asset_id);

CREATE INDEX IF NOT EXISTS idx_warranties_end_date
    ON warranties(end_date);


-- ============================================================
-- 4. ASSET DOCUMENTS
-- ============================================================

CREATE TABLE IF NOT EXISTS asset_documents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    asset_id UUID NOT NULL,

    drive_file_id VARCHAR(255) NOT NULL,

    doc_type VARCHAR(50) NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_asset_documents_asset
        FOREIGN KEY (asset_id)
        REFERENCES assets(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_asset_document_type
        CHECK (
            doc_type IN (
                'INVOICE',
                'WARRANTY_CARD',
                'SERVICE_REPORT',
                'INSURANCE_PAPER'
            )
        )
);

CREATE INDEX IF NOT EXISTS idx_asset_documents_asset_id
    ON asset_documents(asset_id);

CREATE INDEX IF NOT EXISTS idx_asset_documents_doc_type
    ON asset_documents(doc_type);


-- ============================================================
-- 5. ASSET REQUESTS
-- ============================================================

CREATE TABLE IF NOT EXISTS asset_requests (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    org_id UUID NOT NULL,

    requested_by UUID NOT NULL,

    model_id UUID NULL,

    reason TEXT NOT NULL,

    status VARCHAR(40) NOT NULL DEFAULT 'PENDING_DEPT_HEAD',

    dept_head_approved_by UUID NULL,
    dept_head_approved_at TIMESTAMPTZ NULL,

    asset_mgr_approved_by UUID NULL,
    asset_mgr_approved_at TIMESTAMPTZ NULL,

    fulfilled_asset_id UUID NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_asset_requests_requested_by
        FOREIGN KEY (requested_by)
        REFERENCES users(id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_asset_requests_dept_head
        FOREIGN KEY (dept_head_approved_by)
        REFERENCES users(id)
        ON DELETE SET NULL,

    CONSTRAINT fk_asset_requests_asset_manager
        FOREIGN KEY (asset_mgr_approved_by)
        REFERENCES users(id)
        ON DELETE SET NULL,

    CONSTRAINT fk_asset_requests_model
        FOREIGN KEY (model_id)
        REFERENCES asset_models(id)
        ON DELETE SET NULL,

    CONSTRAINT fk_asset_requests_fulfilled_asset
        FOREIGN KEY (fulfilled_asset_id)
        REFERENCES assets(id)
        ON DELETE SET NULL,

    CONSTRAINT chk_asset_request_status
        CHECK (
            status IN (
                'PENDING_DEPT_HEAD',
                'PENDING_ASSET_MGR',
                'APPROVED',
                'REJECTED',
                'FULFILLED'
            )
        )
);

CREATE INDEX IF NOT EXISTS idx_asset_requests_org_id
    ON asset_requests(org_id);

CREATE INDEX IF NOT EXISTS idx_asset_requests_requested_by
    ON asset_requests(requested_by);

CREATE INDEX IF NOT EXISTS idx_asset_requests_model_id
    ON asset_requests(model_id);

CREATE INDEX IF NOT EXISTS idx_asset_requests_status
    ON asset_requests(status);

CREATE INDEX IF NOT EXISTS idx_asset_requests_fulfilled_asset
    ON asset_requests(fulfilled_asset_id);


-- ============================================================
-- 6. ASSET AUDITS
-- ============================================================

CREATE TABLE IF NOT EXISTS asset_audits (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    org_id UUID NOT NULL,

    branch_id UUID NOT NULL,

    conducted_by UUID NOT NULL,

    audit_date DATE NOT NULL DEFAULT CURRENT_DATE,

    system_count INTEGER NOT NULL DEFAULT 0,
    physical_count INTEGER NOT NULL DEFAULT 0,

    discrepancy_notes TEXT NULL,

    status VARCHAR(30) NOT NULL DEFAULT 'IN_PROGRESS',

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_asset_audits_conducted_by
        FOREIGN KEY (conducted_by)
        REFERENCES users(id)
        ON DELETE RESTRICT,

    CONSTRAINT chk_asset_audit_status
        CHECK (
            status IN (
                'IN_PROGRESS',
                'COMPLETED'
            )
        ),

    CONSTRAINT chk_asset_audit_counts
        CHECK (
            system_count >= 0
            AND physical_count >= 0
        )
);

CREATE INDEX IF NOT EXISTS idx_asset_audits_org_id
    ON asset_audits(org_id);

CREATE INDEX IF NOT EXISTS idx_asset_audits_branch_id
    ON asset_audits(branch_id);

CREATE INDEX IF NOT EXISTS idx_asset_audits_conducted_by
    ON asset_audits(conducted_by);

CREATE INDEX IF NOT EXISTS idx_asset_audits_status
    ON asset_audits(status);

CREATE INDEX IF NOT EXISTS idx_asset_audits_date
    ON asset_audits(audit_date);


-- ============================================================
-- 7. ASSET AUDIT ITEMS
-- ============================================================

CREATE TABLE IF NOT EXISTS asset_audit_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    audit_id UUID NOT NULL,

    asset_id UUID NOT NULL,

    expected_location VARCHAR(255) NULL,

    found BOOLEAN NOT NULL DEFAULT FALSE,

    found_location VARCHAR(255) NULL,

    scanned_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_audit_items_audit
        FOREIGN KEY (audit_id)
        REFERENCES asset_audits(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_audit_items_asset
        FOREIGN KEY (asset_id)
        REFERENCES assets(id)
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_audit_items_audit_id
    ON asset_audit_items(audit_id);

CREATE INDEX IF NOT EXISTS idx_audit_items_asset_id
    ON asset_audit_items(asset_id);

CREATE INDEX IF NOT EXISTS idx_audit_items_found
    ON asset_audit_items(found);


-- ============================================================
-- 8. UPDATED_AT TRIGGER FUNCTION
-- ============================================================

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


-- ============================================================
-- 9. UPDATED_AT TRIGGERS
-- ============================================================

DROP TRIGGER IF EXISTS trg_pm_updated_at
ON preventive_maintenance_schedules;

CREATE TRIGGER trg_pm_updated_at
BEFORE UPDATE ON preventive_maintenance_schedules
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();


DROP TRIGGER IF EXISTS trg_warranties_updated_at
ON warranties;

CREATE TRIGGER trg_warranties_updated_at
BEFORE UPDATE ON warranties
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();


-- ============================================================
-- END OF PHASE II SCHEMA
-- ============================================================
