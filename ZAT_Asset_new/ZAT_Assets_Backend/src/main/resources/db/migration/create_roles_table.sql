-- Phase 5: Role entity/table
-- ZAT Asset Management
--
-- This phase creates only the roles foundation.
-- No role-permission, user-role, or user-permission relationship tables
-- are created here.
--
-- System roles are seeded in Phase 6.

CREATE TABLE IF NOT EXISTS roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    name VARCHAR(100) NOT NULL,

    description VARCHAR(255),

    type VARCHAR(20) NOT NULL DEFAULT 'CUSTOM',

    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

    system_defined BOOLEAN NOT NULL DEFAULT FALSE,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_roles_name
        UNIQUE (name),

    CONSTRAINT chk_roles_type
        CHECK (type IN ('SYSTEM', 'CUSTOM')),

    CONSTRAINT chk_roles_status
        CHECK (status IN ('ACTIVE', 'INACTIVE')),

    CONSTRAINT chk_roles_system_defined
        CHECK (
            (type = 'SYSTEM' AND system_defined = TRUE)
            OR
            (type = 'CUSTOM' AND system_defined = FALSE)
        )
);

CREATE INDEX IF NOT EXISTS idx_roles_type
    ON roles (type);

CREATE INDEX IF NOT EXISTS idx_roles_status
    ON roles (status);

CREATE INDEX IF NOT EXISTS idx_roles_system_defined
    ON roles (system_defined);
