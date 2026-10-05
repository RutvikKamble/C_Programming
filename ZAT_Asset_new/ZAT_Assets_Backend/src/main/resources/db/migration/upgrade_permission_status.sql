-- Phase 4: Permission ACTIVE / INACTIVE status
-- ZAT Asset Management
--
-- This migration modifies the existing permissions table created in Phase 3.
-- No role, user-role, role-permission, or user-permission tables are created here.

ALTER TABLE permissions
    ADD COLUMN IF NOT EXISTS status VARCHAR(20);

UPDATE permissions
SET status = 'ACTIVE'
WHERE status IS NULL;

ALTER TABLE permissions
    ALTER COLUMN status SET DEFAULT 'ACTIVE';

ALTER TABLE permissions
    ALTER COLUMN status SET NOT NULL;

ALTER TABLE permissions
    DROP CONSTRAINT IF EXISTS chk_permissions_status;

ALTER TABLE permissions
    ADD CONSTRAINT chk_permissions_status
        CHECK (status IN ('ACTIVE', 'INACTIVE'));
