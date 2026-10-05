-- ============================================================
-- Migration: Add warranty expiry date to assets
-- This matches the warranty_expiry field mapped by Asset.java.
-- ============================================================

ALTER TABLE assets
    ADD COLUMN IF NOT EXISTS warranty_expiry DATE;