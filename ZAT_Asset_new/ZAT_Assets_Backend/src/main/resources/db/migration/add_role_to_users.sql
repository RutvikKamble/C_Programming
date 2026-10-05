-- Phase 8: Role -> User UUID relationship
-- ZAT Asset Management
--
-- This phase establishes one primary Role assignment for each existing
-- users/Employee record.
--
-- Relationship:
--     users.role_id -> roles.id
--
-- The column is intentionally nullable during this phase because existing
-- users may not yet have a role assigned.
--
-- Permission synchronization from Role -> User UUID is implemented later.
-- No user_permissions table is created in this phase.

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS role_id UUID;

ALTER TABLE users
    DROP CONSTRAINT IF EXISTS fk_users_role;

ALTER TABLE users
    ADD CONSTRAINT fk_users_role
        FOREIGN KEY (role_id)
        REFERENCES roles (id)
        ON DELETE SET NULL;

CREATE INDEX IF NOT EXISTS idx_users_role_id
    ON users (role_id);
