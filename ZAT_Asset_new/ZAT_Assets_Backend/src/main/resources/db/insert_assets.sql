-- ============================================================
-- ZAT Asset Management — Insert 5 sample asset records
-- Fully self-contained: ensures parent rows (company, branch,
-- department, category, vendor, employee) exist BEFORE inserting
-- the assets, so all foreign-key references resolve.
--
-- Run against PostgreSQL (Supabase pooler / local).
-- Re-runnable: uses ON CONFLICT DO NOTHING on parents and
-- fixed UUID ids on assets.
-- ============================================================

BEGIN;

-- ------------------------------------------------------------
-- 1) COMPANY
-- ------------------------------------------------------------
INSERT INTO companies (id, company_name, legal_name, gstin_tax_id, created_at, updated_at)
VALUES (
    'feb7f5c0-e010-41a7-b3a6-b0fef543d3c8',
    'Verzat R&D Pvt Ltd',
    'Verzat Research & Development Private Limited',
    '27AAAAA0000A1Z6',
    NOW(),
    NOW()
)
ON CONFLICT (id) DO NOTHING;

-- ------------------------------------------------------------
-- 2) BRANCH
-- ------------------------------------------------------------
INSERT INTO branches (id, company_id, name, address, city, state, country)
VALUES (
    '6e958647-1cd1-4931-8909-119d46b97119',
    'feb7f5c0-e010-41a7-b3a6-b0fef543d3c8',
    'Chennai Technology Center',
    'DLF Cybercity, Manapakkam',
    'Chennai',
    'Tamil Nadu',
    'India'
)
ON CONFLICT (id) DO NOTHING;

-- ------------------------------------------------------------
-- 3) DEPARTMENT
-- ------------------------------------------------------------
INSERT INTO departments (id, company_id, branch_id, name)
VALUES (
    '56377df6-6838-42bf-b3ba-8cc2067b6516',
    'feb7f5c0-e010-41a7-b3a6-b0fef543d3c8',
    '6e958647-1cd1-4931-8909-119d46b97119',
    'Engineering'
)
ON CONFLICT (id) DO NOTHING;

-- ------------------------------------------------------------
-- 4) ASSET CATEGORY
-- ------------------------------------------------------------
INSERT INTO asset_categories (id, company_id, parent_id, category, depreciation_method, useful_life_months)
VALUES (
    '6224082e-dab1-4f12-a3b2-7bf10030513b',
    'feb7f5c0-e010-41a7-b3a6-b0fef543d3c8',
    NULL,
    'IT',
    'STRAIGHT_LINE',
    36
)
ON CONFLICT (id) DO NOTHING;

-- ------------------------------------------------------------
-- 5) VENDOR
-- ------------------------------------------------------------
INSERT INTO vendors (id, company_id, name, contact_email, contact_phone, address, created_at)
VALUES (
    '2ad14246-9898-4eed-ba36-7de67c1aa8d5',
    'feb7f5c0-e010-41a7-b3a6-b0fef543d3c8',
    'TechSupply Co.',
    'raj@techsupply.com',
    '9876543210',
    'MIDC, Pune',
    NOW()
)
ON CONFLICT (id) DO NOTHING;

-- ------------------------------------------------------------
-- 6) EMPLOYEE (owner)
-- ------------------------------------------------------------
INSERT INTO users (
    id, company_id, branch_id, department_id, employee_code,
    full_name, email, phone, password_hash, status
)
VALUES (
    'c9a2b536-4311-4d38-b12e-a579aa7f3a98',
    'feb7f5c0-e010-41a7-b3a6-b0fef543d3c8',
    '6e958647-1cd1-4931-8909-119d46b97119',
    '56377df6-6838-42bf-b3ba-8cc2067b6516',
    'EMP-001',
    'Jane Smith',
    'jane.smith@company.com',
    '9876543210',
    'changeme123',
    'ACTIVE'
)
ON CONFLICT (id) DO NOTHING;

-- ------------------------------------------------------------
-- 7) ASSETS (5 records)
-- ------------------------------------------------------------
INSERT INTO assets (
    id,
    company_id,
    branch_id,
    department_id,
    asset_code,
    name,
    category_id,
    asset_type,
    serial_number,
    vendor_id,
    purchase_date,
    purchase_cost,
    current_value,
    status,
    current_owner_id,
    current_location,
    qr_code_value,
    barcode_value,
    created_at,
    updated_at
) VALUES
(
    'a1b2c3d4-0001-4a1f-9c1e-000000000001',
    'feb7f5c0-e010-41a7-b3a6-b0fef543d3c8',
    '6e958647-1cd1-4931-8909-119d46b97119',
    '56377df6-6838-42bf-b3ba-8cc2067b6516',
    'AST-2001',
    'HP EliteBook 840 G9',
    '6224082e-dab1-4f12-a3b2-7bf10030513b',
    'IT_ASSET',
    'SN-HP840-1001',
    '2ad14246-9898-4eed-ba36-7de67c1aa8d5',
    '2026-02-10',
    82000.00,
    78000.00,
    'AVAILABLE',
    'c9a2b536-4311-4d38-b12e-a579aa7f3a98',
    'Chennai Office - Ground Floor',
    'AST:AST-2001:a1b2c3d4-0001-4a1f-9c1e-000000000001',
    'AST-2001',
    NOW(),
    NOW()
),
(
    'a1b2c3d4-0002-4a1f-9c1e-000000000002',
    'feb7f5c0-e010-41a7-b3a6-b0fef543d3c8',
    '6e958647-1cd1-4931-8909-119d46b97119',
    '56377df6-6838-42bf-b3ba-8cc2067b6516',
    'AST-2002',
    'Lenovo ThinkPad X1 Carbon',
    '6224082e-dab1-4f12-a3b2-7bf10030513b',
    'IT_ASSET',
    'SN-LNVX1-2002',
    '2ad14246-9898-4eed-ba36-7de67c1aa8d5',
    '2026-03-05',
    95000.00,
    91000.00,
    'ASSIGNED',
    'c9a2b536-4311-4d38-b12e-a579aa7f3a98',
    'Pune Office - 4th Floor',
    'AST:AST-2002:a1b2c3d4-0002-4a1f-9c1e-000000000002',
    'AST-2002',
    NOW(),
    NOW()
),
(
    'a1b2c3d4-0003-4a1f-9c1e-000000000003',
    'feb7f5c0-e010-41a7-b3a6-b0fef543d3c8',
    '6e958647-1cd1-4931-8909-119d46b97119',
    '56377df6-6838-42bf-b3ba-8cc2067b6516',
    'AST-2003',
    'Apple MacBook Pro 14',
    '6224082e-dab1-4f12-a3b2-7bf10030513b',
    'IT_ASSET',
    'SN-MBP14-2003',
    '2ad14246-9898-4eed-ba36-7de67c1aa8d5',
    '2026-04-20',
    145000.00,
    140000.00,
    'IN_REPAIR',
    NULL,
    'Service Center - Repair Bay 2',
    'AST:AST-2003:a1b2c3d4-0003-4a1f-9c1e-000000000003',
    'AST-2003',
    NOW(),
    NOW()
),
(
    'a1b2c3d4-0004-4a1f-9c1e-000000000004',
    'feb7f5c0-e010-41a7-b3a6-b0fef543d3c8',
    '6e958647-1cd1-4931-8909-119d46b97119',
    '56377df6-6838-42bf-b3ba-8cc2067b6516',
    'AST-2004',
    'Dell 27" 4K Monitor P2723QE',
    '6224082e-dab1-4f12-a3b2-7bf10030513b',
    'IT_ASSET',
    'SN-DL27-2004',
    '2ad14246-9898-4eed-ba36-7de67c1aa8d5',
    '2026-01-25',
    32000.00,
    30000.00,
    'AVAILABLE',
    NULL,
    'Chennai Office - Storage Room',
    'AST:AST-2004:a1b2c3d4-0004-4a1f-9c1e-000000000004',
    'AST-2004',
    NOW(),
    NOW()
),
(
    'a1b2c3d4-0005-4a1f-9c1e-000000000005',
    'feb7f5c0-e010-41a7-b3a6-b0fef543d3c8',
    '6e958647-1cd1-4931-8909-119d46b97119',
    '56377df6-6838-42bf-b3ba-8cc2067b6516',
    'AST-2005',
    'Canon IR-ADV C5560 Printer',
    '6224082e-dab1-4f12-a3b2-7bf10030513b',
    'EQUIPMENT_ASSET',
    'SN-CAN5560-2005',
    '2ad14246-9898-4eed-ba36-7de67c1aa8d5',
    '2025-11-15',
    185000.00,
    160000.00,
    'AVAILABLE',
    NULL,
    'Chennai Office - Admin Block',
    'AST:AST-2005:a1b2c3d4-0005-4a1f-9c1e-000000000005',
    'AST-2005',
    NOW(),
    NOW()
);

COMMIT;
