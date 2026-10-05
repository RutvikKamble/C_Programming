-- ZAT Asset Management - Disposal Management
-- Creates the controlled asset disposal workflow record.

CREATE TABLE IF NOT EXISTS disposal_records (
    id UUID PRIMARY KEY,
    asset_id UUID NOT NULL,
    reason VARCHAR(30) NOT NULL,
    reviewed_by UUID,
    approved_by UUID,
    finalized_by UUID,
    status VARCHAR(30) NOT NULL,
    disposal_date DATE,
    disposal_value NUMERIC(14,2),
    disposal_method VARCHAR(40),
    rejection_reason TEXT,
    remarks TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_disposal_asset
        FOREIGN KEY (asset_id) REFERENCES assets(id),

    CONSTRAINT fk_disposal_reviewed_by
        FOREIGN KEY (reviewed_by) REFERENCES users(id),

    CONSTRAINT fk_disposal_approved_by
        FOREIGN KEY (approved_by) REFERENCES users(id),

    CONSTRAINT fk_disposal_finalized_by
        FOREIGN KEY (finalized_by) REFERENCES users(id),

    CONSTRAINT chk_disposal_reason
        CHECK (reason IN ('END_OF_LIFE', 'DAMAGED', 'OBSOLETE', 'SOLD')),

    CONSTRAINT chk_disposal_status
        CHECK (status IN ('REVIEW_PENDING', 'APPROVED', 'REJECTED', 'DISPOSED')),

    CONSTRAINT chk_disposal_value
        CHECK (disposal_value IS NULL OR disposal_value >= 0)
);

CREATE INDEX IF NOT EXISTS idx_disposal_asset
    ON disposal_records(asset_id);

CREATE INDEX IF NOT EXISTS idx_disposal_status
    ON disposal_records(status);
