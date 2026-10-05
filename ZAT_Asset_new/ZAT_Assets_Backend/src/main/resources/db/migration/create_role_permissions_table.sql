-- Phase 7: Role -> Permission relationship
-- ZAT Asset Management
--
-- This phase creates ONLY the role_permissions relationship.
--
-- Existing tables required:
--     roles
--     permissions
--
-- Not created in this phase:
--     user_roles
--     user_permissions
--     authentication
--     JWT
--     Spring Security
--
-- A role can have many permissions and a permission can belong to many roles.
-- The unique constraint prevents the same permission from being assigned
-- to the same role more than once.

CREATE TABLE IF NOT EXISTS role_permissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    role_id UUID NOT NULL,

    permission_id UUID NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_role_permissions_role
        FOREIGN KEY (role_id)
        REFERENCES roles (id)
        ON DELETE CASCADE,

    CONSTRAINT fk_role_permissions_permission
        FOREIGN KEY (permission_id)
        REFERENCES permissions (id)
        ON DELETE CASCADE,

    CONSTRAINT uk_role_permissions_role_permission
        UNIQUE (role_id, permission_id)
);

CREATE INDEX IF NOT EXISTS idx_role_permissions_role_id
    ON role_permissions (role_id);

CREATE INDEX IF NOT EXISTS idx_role_permissions_permission_id
    ON role_permissions (permission_id);
