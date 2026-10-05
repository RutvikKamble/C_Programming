-- ============================================================
-- ZAT Asset Management — RECOVERY SCRIPT (ALL RECORDS)
-- Restores ALL data that was lost when the app previously ran
-- with spring.jpa.hibernate.ddl-auto=create (which drops and
-- recreates tables on every startup).
--
-- Data sources (project root seed exports):
--   _cat.json, _employees.json, _allocations.json, _returns.json
--
-- Covered records:
--   companies, branches, departments, asset_categories, vendors,
--   users, assets (original + 5 samples), asset_allocations,
--   asset_returns
--
-- Now that ddl-auto is "none", this data will persist.
-- Re-runnable: uses ON CONFLICT (id) DO NOTHING.
-- Paste into Supabase SQL Editor, or run via psql.
-- ============================================================

BEGIN;

-- ------------------------------------------------------------
-- 1) COMPANY
-- ------------------------------------------------------------
INSERT INTO companies (id, company_name, legal_name, gstin_tax_id, created_at, updated_at)
VALUES (
    'feb7f5c0-e010-41a7-b3a6-b0fef543d3c8',
    'Verzat R&D Pvt Ltd Updated',
    'Verzat Research & Development Private Limited Updated',
    '27AAAAA0000A1Z6',
    '2026-08-04T16:55:20.735942',
    '2026-08-05T23:51:53.534176'
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
-- 4) ASSET CATEGORIES (3 from _cat.json)
-- ------------------------------------------------------------
INSERT INTO asset_categories (id, company_id, parent_id, category, depreciation_method, useful_life_months)
VALUES
('1dfca099-fca1-45f2-81c6-a47ddcc6e380', 'feb7f5c0-e010-41a7-b3a6-b0fef543d3c8', NULL, 'IT', 'STRAIGHT_LINE', 60),
('481ba72d-e739-43fc-bf66-60f42f3940fd', 'feb7f5c0-e010-41a7-b3a6-b0fef543d3c8', NULL, 'IT', 'STRAIGHT_LINE', 48),
('6224082e-dab1-4f12-a3b2-7bf10030513b', 'feb7f5c0-e010-41a7-b3a6-b0fef543d3c8', NULL, 'IT', 'STRAIGHT_LINE', 36)
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
    '2026-08-04T19:40:58.09036'
)
ON CONFLICT (id) DO NOTHING;

-- ------------------------------------------------------------
-- 6) EMPLOYEES (5 from _employees.json)
-- ------------------------------------------------------------
INSERT INTO users (
    id, company_id, branch_id, department_id, employee_code,
    full_name, email, phone, password_hash, status
) VALUES
('c9a2b536-4311-4d38-b12e-a579aa7f3a98', 'feb7f5c0-e010-41a7-b3a6-b0fef543d3c8', '6e958647-1cd1-4931-8909-119d46b97119', '56377df6-6838-42bf-b3ba-8cc2067b6516', 'EMP-001', 'Jane Smith',   'jane.smith@company.com',   '9876543210', 'changeme123', 'ACTIVE'),
('810d89c3-4ccb-4cbd-8f8c-5711729bbc0c', 'feb7f5c0-e010-41a7-b3a6-b0fef543d3c8', '6e958647-1cd1-4931-8909-119d46b97119', '56377df6-6838-42bf-b3ba-8cc2067b6516', 'EMP-002', 'Rahul Sharma', 'rahul.sharma@company.com', '9876543211', 'changeme123', 'ACTIVE'),
('6ccecfd5-92ea-4e88-a596-47fded99e4c9', 'feb7f5c0-e010-41a7-b3a6-b0fef543d3c8', '6e958647-1cd1-4931-8909-119d46b97119', '56377df6-6838-42bf-b3ba-8cc2067b6516', 'EMP-003', 'Priya Patel',  'priya.patel@company.com',  '9876543212', 'changeme123', 'ACTIVE'),
('89f31f07-d3c3-488d-a6d7-975a3a267dc6', 'feb7f5c0-e010-41a7-b3a6-b0fef543d3c8', '6e958647-1cd1-4931-8909-119d46b97119', '56377df6-6838-42bf-b3ba-8cc2067b6516', 'EMP-004', 'Arjun Mehta', 'arjun.mehta@company.com',  '9876543213', 'changeme123', 'ACTIVE'),
('88965b81-e9b2-413c-a1f1-77fa99157e21', 'feb7f5c0-e010-41a7-b3a6-b0fef543d3c8', '6e958647-1cd1-4931-8909-119d46b97119', '56377df6-6838-42bf-b3ba-8cc2067b6516', 'EMP-005', 'Neha Verma',  'neha.verma@company.com',  '9876543214', 'changeme123', 'ACTIVE')
ON CONFLICT (id) DO NOTHING;

-- ------------------------------------------------------------
-- 7) ORIGINAL ASSET (Dell Latitude 5440, AST-1001)
-- ------------------------------------------------------------
INSERT INTO assets (
    id, company_id, branch_id, department_id, asset_code, name, category_id,
    asset_type, serial_number, vendor_id, purchase_date, purchase_cost,
    current_value, status, current_owner_id, current_location,
    qr_code_value, barcode_value, created_at, updated_at
) VALUES (
    '4774b0a5-c063-4052-a590-69f962423ae4',
    'feb7f5c0-e010-41a7-b3a6-b0fef543d3c8',
    '6e958647-1cd1-4931-8909-119d46b97119',
    '56377df6-6838-42bf-b3ba-8cc2067b6516',
    'AST-1001',
    'Dell Latitude 5440 Laptop',
    '6224082e-dab1-4f12-a3b2-7bf10030513b',
    'IT_ASSET',
    'SN-DL5440-001',
    '2ad14246-9898-4eed-ba36-7de67c1aa8d5',
    '2026-01-15',
    75000.00,
    70000.00,
    'AVAILABLE',
    'c9a2b536-4311-4d38-b12e-a579aa7f3a98',
    'Pune Office - 4th Floor',
    NULL,
    NULL,
    '2026-08-04T19:50:08.992165',
    '2026-08-04T19:50:08.992194'
)
ON CONFLICT (id) DO NOTHING;

-- ------------------------------------------------------------
-- 8) ORIGINAL ALLOCATION (AST-1001 -> Jane Smith)
-- ------------------------------------------------------------
INSERT INTO asset_allocations (
    id, asset_id, employee_id, department_id, branch_id,
    allocated_on, returned_on, status, acknowledged_by_employee
) VALUES (
    '01b55538-55cb-400a-a21c-e50f188411a7',
    '4774b0a5-c063-4052-a590-69f962423ae4',
    'c9a2b536-4311-4d38-b12e-a579aa7f3a98',
    '56377df6-6838-42bf-b3ba-8cc2067b6516',
    '6e958647-1cd1-4931-8909-119d46b97119',
    '2026-08-01',
    NULL,
    'ALLOCATED',
    false
)
ON CONFLICT (id) DO NOTHING;

-- ------------------------------------------------------------
-- 9) ORIGINAL RETURN REQUEST (from _returns.json)
--    reason = RESIGNATION, status = REQUESTED
-- ------------------------------------------------------------
INSERT INTO asset_returns (
    id, allocation_id, requested_by, reason, status, verified_by, verified_on
) VALUES (
    '97dcab58-4bdc-4c92-9d5b-ce4783fcce88',
    '01b55538-55cb-400a-a21c-e50f188411a7',
    'c9a2b536-4311-4d38-b12e-a579aa7f3a98',
    'RESIGNATION',
    'REQUESTED',
    NULL,
    NULL
)
ON CONFLICT (id) DO NOTHING;

-- ------------------------------------------------------------
-- 10) FIVE SAMPLE ASSETS (from insert_assets.sql)
-- ------------------------------------------------------------
INSERT INTO assets (
    id, company_id, branch_id, department_id, asset_code, name, category_id,
    asset_type, serial_number, vendor_id, purchase_date, purchase_cost,
    current_value, status, current_owner_id, current_location,
    qr_code_value, barcode_value, created_at, updated_at
) VALUES
(
    'a1b2c3d4-0001-4a1f-9c1e-000000000001', 'feb7f5c0-e010-41a7-b3a6-b0fef543d3c8', '6e958647-1cd1-4931-8909-119d46b97119', '56377df6-6838-42bf-b3ba-8cc2067b6516',
    'AST-2001', 'HP EliteBook 840 G9', '6224082e-dab1-4f12-a3b2-7bf10030513b', 'IT_ASSET', 'SN-HP840-1001', '2ad14246-9898-4eed-ba36-7de67c1aa8d5',
    '2026-02-10', 82000.00, 78000.00, 'AVAILABLE', 'c9a2b536-4311-4d38-b12e-a579aa7f3a98', 'Chennai Office - Ground Floor',
    'AST:AST-2001:a1b2c3d4-0001-4a1f-9c1e-000000000001', 'AST-2001', NOW(), NOW()
),
(
    'a1b2c3d4-0002-4a1f-9c1e-000000000002', 'feb7f5c0-e010-41a7-b3a6-b0fef543d3c8', '6e958647-1cd1-4931-8909-119d46b97119', '56377df6-6838-42bf-b3ba-8cc2067b6516',
    'AST-2002', 'Lenovo ThinkPad X1 Carbon', '6224082e-dab1-4f12-a3b2-7bf10030513b', 'IT_ASSET', 'SN-LNVX1-2002', '2ad14246-9898-4eed-ba36-7de67c1aa8d5',
    '2026-03-05', 95000.00, 91000.00, 'ASSIGNED', 'c9a2b536-4311-4d38-b12e-a579aa7f3a98', 'Pune Office - 4th Floor',
    'AST:AST-2002:a1b2c3d4-0002-4a1f-9c1e-000000000002', 'AST-2002', NOW(), NOW()
),
(
    'a1b2c3d4-0003-4a1f-9c1e-000000000003', 'feb7f5c0-e010-41a7-b3a6-b0fef543d3c8', '6e958647-1cd1-4931-8909-119d46b97119', '56377df6-6838-42bf-b3ba-8cc2067b6516',
    'AST-2003', 'Apple MacBook Pro 14', '6224082e-dab1-4f12-a3b2-7bf10030513b', 'IT_ASSET', 'SN-MBP14-2003', '2ad14246-9898-4eed-ba36-7de67c1aa8d5',
    '2026-04-20', 145000.00, 140000.00, 'IN_REPAIR', NULL, 'Service Center - Repair Bay 2',
    'AST:AST-2003:a1b2c3d4-0003-4a1f-9c1e-000000000003', 'AST-2003', NOW(), NOW()
),
(
    'a1b2c3d4-0004-4a1f-9c1e-000000000004', 'feb7f5c0-e010-41a7-b3a6-b0fef543d3c8', '6e958647-1cd1-4931-8909-119d46b97119', '56377df6-6838-42bf-b3ba-8cc2067b6516',
    'AST-2004', 'Dell 27" 4K Monitor P2723QE', '6224082e-dab1-4f12-a3b2-7bf10030513b', 'IT_ASSET', 'SN-DL27-2004', '2ad14246-9898-4eed-ba36-7de67c1aa8d5',
    '2026-01-25', 32000.00, 30000.00, 'AVAILABLE', NULL, 'Chennai Office - Storage Room',
    'AST:AST-2004:a1b2c3d4-0004-4a1f-9c1e-000000000004', 'AST-2004', NOW(), NOW()
),
(
    'a1b2c3d4-0005-4a1f-9c1e-000000000005', 'feb7f5c0-e010-41a7-b3a6-b0fef543d3c8', '6e958647-1cd1-4931-8909-119d46b97119', '56377df6-6838-42bf-b3ba-8cc2067b6516',
    'AST-2005', 'Canon IR-ADV C5560 Printer', '6224082e-dab1-4f12-a3b2-7bf10030513b', 'EQUIPMENT_ASSET', 'SN-CAN5560-2005', '2ad14246-9898-4eed-ba36-7de67c1aa8d5',
    '2025-11-15', 185000.00, 160000.00, 'AVAILABLE', NULL, 'Chennai Office - Admin Block',
    'AST:AST-2005:a1b2c3d4-0005-4a1f-9c1e-000000000005', 'AST-2005', NOW(), NOW()
)
ON CONFLICT (id) DO NOTHING;

COMMIT;

