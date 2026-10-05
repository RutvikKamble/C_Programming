-- ZAT Asset History integration upgrade
-- PostgreSQL migration. Existing asset_history rows are preserved.
-- History is append-only: application code should only INSERT and SELECT.

CREATE TABLE IF NOT EXISTS asset_history (
    id UUID PRIMARY KEY,
    asset_id UUID NOT NULL REFERENCES assets(id),
    action VARCHAR(50) NOT NULL,
    field_changed VARCHAR(100),
    old_value TEXT,
    new_value TEXT,
    description TEXT,
    changed_by VARCHAR(150),
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_asset_history_asset_created
    ON asset_history(asset_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_asset_history_action
    ON asset_history(action);
CREATE INDEX IF NOT EXISTS idx_asset_history_created_at
    ON asset_history(created_at DESC);

CREATE OR REPLACE FUNCTION prevent_asset_history_mutation()
RETURNS TRIGGER AS $$
BEGIN
    RAISE EXCEPTION 'asset_history is append-only; UPDATE and DELETE are not allowed';
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_asset_history_immutable ON asset_history;
CREATE TRIGGER trg_asset_history_immutable
BEFORE UPDATE OR DELETE ON asset_history
FOR EACH ROW EXECUTE FUNCTION prevent_asset_history_mutation();
