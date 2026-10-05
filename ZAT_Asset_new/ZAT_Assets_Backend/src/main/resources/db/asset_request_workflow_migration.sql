-- Asset Request workflow migration.
-- Run once against an existing database. Hibernate ddl-auto=update can add
-- columns, but it does not safely replace the old request CHECK constraints.

ALTER TABLE asset_request
    ALTER COLUMN employee_id DROP NOT NULL;

ALTER TABLE asset_request
    ADD COLUMN IF NOT EXISTS requested_by_id UUID,
    ADD COLUMN IF NOT EXISTS source_company_id UUID,
    ADD COLUMN IF NOT EXISTS source_branch_id UUID,
    ADD COLUMN IF NOT EXISTS destination_company_id UUID,
    ADD COLUMN IF NOT EXISTS destination_branch_id UUID,
    ADD COLUMN IF NOT EXISTS vendor_id UUID,
    ADD COLUMN IF NOT EXISTS purchase_cost NUMERIC(14,2),
    ADD COLUMN IF NOT EXISTS invoice_number VARCHAR(100),
    ADD COLUMN IF NOT EXISTS invoice_amount NUMERIC(14,2),
    ADD COLUMN IF NOT EXISTS quantity INTEGER,
    ADD COLUMN IF NOT EXISTS received_asset_id UUID,
    ADD COLUMN IF NOT EXISTS approved_date TIMESTAMP,
    ADD COLUMN IF NOT EXISTS received_by VARCHAR(255),
    ADD COLUMN IF NOT EXISTS received_date TIMESTAMP,
    ADD COLUMN IF NOT EXISTS paid_by VARCHAR(255),
    ADD COLUMN IF NOT EXISTS paid_date TIMESTAMP,
    ADD COLUMN IF NOT EXISTS rejection_reason VARCHAR(255),
    ADD COLUMN IF NOT EXISTS version BIGINT,
    ADD COLUMN IF NOT EXISTS asset_template_name VARCHAR(150),
    ADD COLUMN IF NOT EXISTS asset_template_code VARCHAR(30),
    ADD COLUMN IF NOT EXISTS asset_template_serial_number VARCHAR(100),
    ADD COLUMN IF NOT EXISTS asset_template_type VARCHAR(50),
    ADD COLUMN IF NOT EXISTS asset_template_company_id UUID,
    ADD COLUMN IF NOT EXISTS asset_template_branch_id UUID,
    ADD COLUMN IF NOT EXISTS asset_template_department_id UUID,
    ADD COLUMN IF NOT EXISTS asset_template_category_id UUID,
    ADD COLUMN IF NOT EXISTS asset_template_model_id UUID,
    ADD COLUMN IF NOT EXISTS asset_template_current_value NUMERIC(14,2),
    ADD COLUMN IF NOT EXISTS asset_template_purchase_date DATE,
    ADD COLUMN IF NOT EXISTS asset_template_warranty_expiry DATE,
    ADD COLUMN IF NOT EXISTS asset_template_status VARCHAR(50),
    ADD COLUMN IF NOT EXISTS asset_template_location VARCHAR(150);

ALTER TABLE asset_request DROP CONSTRAINT IF EXISTS chk_request_type;
ALTER TABLE asset_request DROP CONSTRAINT IF EXISTS chk_request_status;

ALTER TABLE asset_request
    ADD CONSTRAINT chk_asset_request_type
    CHECK (request_type IN (
        'CREATE','PURCHASE','BRANCH_TRANSFER','HEAD_OFFICE_TRANSFER',
        'NEWEST','NEW_ASSET','REPAIR','REPLACEMENT'
    ));

ALTER TABLE asset_request
    ADD CONSTRAINT chk_asset_request_status
    CHECK (status IN (
        'DRAFT','SENT','RECEIVED','APPROVED','REJECTED','RECEIVED_ASSET',
        'PAY_BILL','TRANSFERRED','COMPLETED','CANCELLED',
        'SEND','PENDING_MANAGER','ACCEPT','REJECT','FULFILLED'
    ));

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_asset_request_requested_by') THEN
        ALTER TABLE asset_request ADD CONSTRAINT fk_asset_request_requested_by
            FOREIGN KEY (requested_by_id) REFERENCES users(id);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_asset_request_source_company') THEN
        ALTER TABLE asset_request ADD CONSTRAINT fk_asset_request_source_company
            FOREIGN KEY (source_company_id) REFERENCES companies(id);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_asset_request_source_branch') THEN
        ALTER TABLE asset_request ADD CONSTRAINT fk_asset_request_source_branch
            FOREIGN KEY (source_branch_id) REFERENCES branches(id);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_asset_request_destination_company') THEN
        ALTER TABLE asset_request ADD CONSTRAINT fk_asset_request_destination_company
            FOREIGN KEY (destination_company_id) REFERENCES companies(id);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_asset_request_destination_branch') THEN
        ALTER TABLE asset_request ADD CONSTRAINT fk_asset_request_destination_branch
            FOREIGN KEY (destination_branch_id) REFERENCES branches(id);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_asset_request_vendor') THEN
        ALTER TABLE asset_request ADD CONSTRAINT fk_asset_request_vendor
            FOREIGN KEY (vendor_id) REFERENCES vendors(id);
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_asset_request_requested_by ON asset_request(requested_by_id);
CREATE INDEX IF NOT EXISTS idx_asset_request_source_branch ON asset_request(source_branch_id);
CREATE INDEX IF NOT EXISTS idx_asset_request_destination_branch ON asset_request(destination_branch_id);
CREATE INDEX IF NOT EXISTS idx_asset_request_received_asset ON asset_request(received_asset_id);
CREATE INDEX IF NOT EXISTS idx_asset_request_type_status ON asset_request(request_type, status);


-- Stores every Asset Master created from a CREATE/PURCHASE request.
CREATE TABLE IF NOT EXISTS asset_request_assets (
    asset_request_id BIGINT NOT NULL,
    asset_id UUID NOT NULL,
    CONSTRAINT pk_asset_request_assets PRIMARY KEY (asset_request_id, asset_id),
    CONSTRAINT fk_asset_request_assets_request FOREIGN KEY (asset_request_id) REFERENCES asset_request(id) ON DELETE CASCADE,
    CONSTRAINT fk_asset_request_assets_asset FOREIGN KEY (asset_id) REFERENCES assets(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_asset_request_assets_asset_id ON asset_request_assets(asset_id);
