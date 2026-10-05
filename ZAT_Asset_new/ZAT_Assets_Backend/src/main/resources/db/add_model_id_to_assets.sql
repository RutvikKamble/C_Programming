-- ============================================================
-- Migration: Add model_id column to assets table
-- This adds the foreign key relationship to asset_models table
-- ============================================================

-- Add model_id column as nullable initially to allow existing assets without a model
ALTER TABLE assets ADD COLUMN model_id UUID REFERENCES asset_models(id);

-- Create index for better query performance
CREATE INDEX idx_assets_model_id ON assets(model_id);

-- Note: After migrating existing data to have valid model_id values, 
-- you can make it non-nullable with:
-- ALTER TABLE assets ALTER COLUMN model_id SET NOT NULL;
