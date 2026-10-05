-- ZAT Asset Management - Module 16: Asset Depreciation
-- Safe to run on an existing database.

ALTER TABLE assets ADD COLUMN IF NOT EXISTS depreciation_method VARCHAR(30);
ALTER TABLE assets ADD COLUMN IF NOT EXISTS useful_life_months INTEGER;
ALTER TABLE assets ADD COLUMN IF NOT EXISTS salvage_value DECIMAL(14,2) DEFAULT 0;
ALTER TABLE assets ADD COLUMN IF NOT EXISTS depreciation_start_date DATE;
ALTER TABLE assets ADD COLUMN IF NOT EXISTS depreciation_end_date DATE;
ALTER TABLE assets ADD COLUMN IF NOT EXISTS depreciation_status VARCHAR(30);

UPDATE assets
SET current_value = purchase_cost
WHERE current_value IS NULL AND purchase_cost IS NOT NULL;

UPDATE assets a
SET depreciation_method = c.depreciation_method,
    useful_life_months = c.useful_life_months,
    salvage_value = COALESCE(a.salvage_value, 0),
    depreciation_start_date = COALESCE(a.depreciation_start_date, a.purchase_date),
    depreciation_status = COALESCE(a.depreciation_status,
        CASE WHEN c.depreciation_method = 'NONE' THEN 'STOPPED' ELSE 'NOT_STARTED' END)
FROM asset_categories c
WHERE a.category_id = c.id
  AND a.depreciation_method IS NULL;

CREATE TABLE IF NOT EXISTS depreciation_records (
    id UUID PRIMARY KEY,
    asset_id UUID NOT NULL REFERENCES assets(id),
    period_month DATE NOT NULL,
    opening_value DECIMAL(14,2) NOT NULL,
    depreciation_amount DECIMAL(14,2) NOT NULL,
    accumulated_depreciation DECIMAL(14,2) NOT NULL DEFAULT 0,
    closing_value DECIMAL(14,2) NOT NULL,
    depreciation_method VARCHAR(30) NOT NULL,
    depreciation_status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_depreciation_asset_period UNIQUE (asset_id, period_month)
);

CREATE INDEX IF NOT EXISTS idx_depreciation_asset_period
    ON depreciation_records(asset_id, period_month);
CREATE INDEX IF NOT EXISTS idx_depreciation_period
    ON depreciation_records(period_month);
CREATE INDEX IF NOT EXISTS idx_assets_depreciation_status
    ON assets(depreciation_status);
