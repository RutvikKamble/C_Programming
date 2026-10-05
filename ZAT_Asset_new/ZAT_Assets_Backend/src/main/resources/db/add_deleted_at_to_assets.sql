-- ============================================================
-- Migration: Add deleted_at column to assets table for soft delete
-- This enables soft delete functionality to preserve audit trail
-- ============================================================

-- Add deleted_at column as nullable to allow existing assets without deletion timestamp
ALTER TABLE assets ADD COLUMN deleted_at TIMESTAMP;

-- Create index for better query performance on filtering deleted assets
CREATE INDEX idx_assets_deleted_at ON assets(deleted_at);
