-- ZAT RBAC: per-user materialised permissions (permissions keyed on the user's UUID).
--
-- Model
--   role assigned to a user  -> every role permission is COPIED to user_permissions (source = 'ROLE')
--   role permissions edited  -> all users of that role are re-synced (done by the application)
--   one user customised      -> extra permission = source 'DIRECT'; removed inherited permission =
--                               the ROLE row is kept with revoked = TRUE
--   effective permission     -> user_permissions.revoked = FALSE (+ ACTIVE permission, ACTIVE role)
--
-- Run once, AFTER rbac_alignment.sql / seed_rbac_permission_catalog.sql. Safe to re-run.
-- (The application also self-heals on startup: it seeds the catalog + built-in roles and copies role
--  permissions to users that have none, so this script mainly migrates old override data.)

ALTER TABLE user_permissions ADD COLUMN IF NOT EXISTS revoked BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE user_permissions ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;
ALTER TABLE roles ADD COLUMN IF NOT EXISTS default_permissions_seeded BOOLEAN NOT NULL DEFAULT FALSE;

-- One row per (user, permission). Keep the DIRECT row if both a ROLE and a DIRECT row exist.
ALTER TABLE user_permissions DROP CONSTRAINT IF EXISTS uk_user_permissions_user_permission_source;
DELETE FROM user_permissions a USING user_permissions b
 WHERE a.user_id = b.user_id AND a.permission_id = b.permission_id AND a.id <> b.id
   AND ((a.source = 'ROLE' AND b.source = 'DIRECT') OR (a.source = b.source AND a.id > b.id));
ALTER TABLE user_permissions DROP CONSTRAINT IF EXISTS uk_user_permissions_user_permission;
ALTER TABLE user_permissions ADD CONSTRAINT uk_user_permissions_user_permission UNIQUE (user_id, permission_id);
CREATE INDEX IF NOT EXISTS idx_user_permissions_user ON user_permissions(user_id);
CREATE INDEX IF NOT EXISTS idx_user_permissions_permission ON user_permissions(permission_id);

-- Copy the role permissions onto every user that already has a role.
INSERT INTO user_permissions (id, user_id, permission_id, source, revoked, created_at)
SELECT gen_random_uuid(), u.id, rp.permission_id, 'ROLE', FALSE, CURRENT_TIMESTAMP
  FROM users u JOIN role_permissions rp ON rp.role_id = u.role_id
 WHERE u.role_id IS NOT NULL
ON CONFLICT (user_id, permission_id) DO NOTHING;

-- Migrate the former GRANT / DENY overrides (table is kept untouched for audit / rollback).
DO $$
BEGIN
  IF to_regclass('public.user_permission_overrides') IS NOT NULL THEN
    INSERT INTO user_permissions (id, user_id, permission_id, source, revoked, created_at)
    SELECT gen_random_uuid(), o.user_id, o.permission_id, 'DIRECT', FALSE, COALESCE(o.created_at, CURRENT_TIMESTAMP)
      FROM user_permission_overrides o WHERE o.effect = 'GRANT'
    ON CONFLICT (user_id, permission_id) DO NOTHING;

    UPDATE user_permissions up SET revoked = TRUE, updated_at = CURRENT_TIMESTAMP
      FROM user_permission_overrides o
     WHERE o.effect = 'DENY' AND up.user_id = o.user_id
       AND up.permission_id = o.permission_id AND up.source = 'ROLE';
  END IF;
END $$;
