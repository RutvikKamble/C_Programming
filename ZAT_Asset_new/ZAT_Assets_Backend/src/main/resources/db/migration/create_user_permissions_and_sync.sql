-- Phase 9: Role permissions -> User UUID permission synchronization
-- ZAT Asset Management
--
-- Purpose:
--     When a role is assigned to a user, every permission currently assigned
--     to that role is copied to user_permissions for that user's UUID.
--
-- Also keeps ROLE-derived user permissions synchronized when a role's
-- permissions are added or removed.
--
-- DIRECT user permissions are reserved for a later phase and are never
-- deleted by the ROLE synchronization logic.
--
-- Required existing tables:
--     users
--     roles
--     permissions
--     role_permissions

CREATE TABLE IF NOT EXISTS user_permissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    user_id UUID NOT NULL,

    permission_id UUID NOT NULL,

    source VARCHAR(20) NOT NULL DEFAULT 'ROLE',

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_user_permissions_user
        FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE,

    CONSTRAINT fk_user_permissions_permission
        FOREIGN KEY (permission_id)
        REFERENCES permissions (id)
        ON DELETE CASCADE,

    CONSTRAINT chk_user_permissions_source
        CHECK (source IN ('ROLE', 'DIRECT')),

    CONSTRAINT uk_user_permissions_user_permission_source
        UNIQUE (user_id, permission_id, source)
);

CREATE INDEX IF NOT EXISTS idx_user_permissions_user_id
    ON user_permissions (user_id);

CREATE INDEX IF NOT EXISTS idx_user_permissions_permission_id
    ON user_permissions (permission_id);

CREATE INDEX IF NOT EXISTS idx_user_permissions_user_source
    ON user_permissions (user_id, source);


-- Synchronize ROLE-derived permissions after a user's role changes.
CREATE OR REPLACE FUNCTION sync_user_role_permissions()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    -- Remove permissions inherited from the previous role only.
    -- DIRECT permissions are intentionally preserved.
    DELETE FROM user_permissions
    WHERE user_id = NEW.id
      AND source = 'ROLE';

    -- If the user has no role, there is nothing to inherit.
    IF NEW.role_id IS NULL THEN
        RETURN NEW;
    END IF;

    -- Copy all permissions assigned to the new role.
    INSERT INTO user_permissions (
        user_id,
        permission_id,
        source
    )
    SELECT
        NEW.id,
        rp.permission_id,
        'ROLE'
    FROM role_permissions rp
    WHERE rp.role_id = NEW.role_id
    ON CONFLICT (user_id, permission_id, source) DO NOTHING;

    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS trg_sync_user_role_permissions
    ON users;

CREATE TRIGGER trg_sync_user_role_permissions
AFTER INSERT OR UPDATE OF role_id
ON users
FOR EACH ROW
EXECUTE FUNCTION sync_user_role_permissions();


-- Synchronize all users currently assigned to a role whenever a permission
-- is added to that role.
CREATE OR REPLACE FUNCTION sync_role_permission_insert()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO user_permissions (
        user_id,
        permission_id,
        source
    )
    SELECT
        u.id,
        NEW.permission_id,
        'ROLE'
    FROM users u
    WHERE u.role_id = NEW.role_id
    ON CONFLICT (user_id, permission_id, source) DO NOTHING;

    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS trg_sync_role_permission_insert
    ON role_permissions;

CREATE TRIGGER trg_sync_role_permission_insert
AFTER INSERT
ON role_permissions
FOR EACH ROW
EXECUTE FUNCTION sync_role_permission_insert();


-- Remove only ROLE-derived copies when a permission is removed from a role.
-- Any DIRECT assignment remains untouched.
CREATE OR REPLACE FUNCTION sync_role_permission_delete()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    DELETE FROM user_permissions up
    USING users u
    WHERE up.user_id = u.id
      AND u.role_id = OLD.role_id
      AND up.permission_id = OLD.permission_id
      AND up.source = 'ROLE';

    RETURN OLD;
END;
$$;

DROP TRIGGER IF EXISTS trg_sync_role_permission_delete
    ON role_permissions;

CREATE TRIGGER trg_sync_role_permission_delete
AFTER DELETE
ON role_permissions
FOR EACH ROW
EXECUTE FUNCTION sync_role_permission_delete();


-- Backfill ROLE-derived permissions for users who already have a role.
-- This is important because Phase 8 may already have assigned role_id values
-- before Phase 9 is installed.
INSERT INTO user_permissions (
    user_id,
    permission_id,
    source
)
SELECT
    u.id,
    rp.permission_id,
    'ROLE'
FROM users u
JOIN role_permissions rp
    ON rp.role_id = u.role_id
WHERE u.role_id IS NOT NULL
ON CONFLICT (user_id, permission_id, source) DO NOTHING;
