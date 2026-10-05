-- ZAT Asset Management - Insurance Management
-- Creates the insurance policy table required by the Technical Guide.

CREATE TABLE IF NOT EXISTS insurance_policies (
    id UUID PRIMARY KEY,
    asset_id UUID NOT NULL,
    provider VARCHAR(150) NOT NULL,
    policy_number VARCHAR(100) NOT NULL UNIQUE,
    coverage_details TEXT,
    premium NUMERIC(14,2),
    start_date DATE,
    expiry_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_insurance_asset
        FOREIGN KEY (asset_id) REFERENCES assets(id)
);

CREATE INDEX IF NOT EXISTS idx_insurance_asset ON insurance_policies(asset_id);
CREATE INDEX IF NOT EXISTS idx_insurance_expiry ON insurance_policies(expiry_date);
CREATE INDEX IF NOT EXISTS idx_insurance_status ON insurance_policies(status);
