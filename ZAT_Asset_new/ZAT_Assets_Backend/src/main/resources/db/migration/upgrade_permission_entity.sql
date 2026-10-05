-- Phase 3: Permission entity/table
-- ZAT Asset Management
--
-- Mandatory permission format:
--     application.module.action
--
-- Example:
--     asset_management.asset.create
--
-- Phase 4 will add ACTIVE/INACTIVE status.
-- No role, user, role-permission, or user-permission tables are created here.

CREATE TABLE IF NOT EXISTS permissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    permission_key VARCHAR(150) NOT NULL,

    application VARCHAR(100) NOT NULL,

    module VARCHAR(100) NOT NULL,

    action VARCHAR(100) NOT NULL,

    description VARCHAR(255),

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_permissions_permission_key
        UNIQUE (permission_key),

    CONSTRAINT chk_permissions_permission_key_format
        CHECK (
            permission_key ~ '^[a-z0-9_]+\.[a-z0-9_]+\.[a-z0-9_]+$'
        )
);

CREATE INDEX IF NOT EXISTS idx_permissions_application
    ON permissions (application);

CREATE INDEX IF NOT EXISTS idx_permissions_module
    ON permissions (module);

CREATE INDEX IF NOT EXISTS idx_permissions_application_module
    ON permissions (application, module);
