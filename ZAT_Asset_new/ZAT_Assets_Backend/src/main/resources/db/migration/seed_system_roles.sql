-- Phase 6: Seed the five ZAT system-defined roles
-- ZAT Asset Management
--
-- This migration creates ONLY the five predefined system roles.
-- No permissions are assigned in this phase.
-- Role -> Permission is implemented in a later phase.
--
-- The INSERT is idempotent by role name, so running this migration
-- multiple times will not create duplicate system roles.
--
-- Fixed UUIDs are intentionally used for system roles so their identities
-- remain stable across environments and future permission assignments.

INSERT INTO roles (
    id,
    name,
    description,
    type,
    status,
    system_defined
)
VALUES
(
    '10000000-0000-0000-0000-000000000001',
    'Super Admin',
    'System-defined role with full administrative access. Permissions are assigned in a later RBAC phase.',
    'SYSTEM',
    'ACTIVE',
    TRUE
),
(
    '10000000-0000-0000-0000-000000000002',
    'Admin',
    'System-defined administrative role. Permissions are assigned in a later RBAC phase.',
    'SYSTEM',
    'ACTIVE',
    TRUE
),
(
    '10000000-0000-0000-0000-000000000003',
    'Asset Manager',
    'System-defined asset management role. Permissions are assigned in a later RBAC phase.',
    'SYSTEM',
    'ACTIVE',
    TRUE
),
(
    '10000000-0000-0000-0000-000000000004',
    'Department Head',
    'System-defined department management role. Permissions are assigned in a later RBAC phase.',
    'SYSTEM',
    'ACTIVE',
    TRUE
),
(
    '10000000-0000-0000-0000-000000000005',
    'Employee',
    'System-defined employee role. Permissions are assigned in a later RBAC phase.',
    'SYSTEM',
    'ACTIVE',
    TRUE
)
ON CONFLICT (name) DO NOTHING;
