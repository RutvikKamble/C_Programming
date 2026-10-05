-- ============================================================
-- ZAT ASSET MANAGEMENT
-- PHASE II - FULLY CORRECTED SEED / DUMMY DATA
-- SUPABASE POSTGRESQL
--
-- Uses the REAL IDs supplied from the current database:
--   assets
--   users
--   asset_models
--   vendors
--   company
--   branch
--   department
--   categories
--
-- IMPORTANT:
-- Run the Phase-II CREATE TABLE script BEFORE this file.
-- This script assumes the Phase-I tables already contain the
-- referenced records.
-- ============================================================


-- ============================================================
-- REAL DATABASE IDs USED BY THIS SEED
-- ============================================================

-- Main company:
-- feb7f5c0-e010-41a7-b3a6-b0fef543d3c8

-- Main branch:
-- 6e958647-1cd1-4931-8909-119d46b97119

-- Main department:
-- 56377df6-6838-42bf-b3ba-8cc2067b6516


-- ============================================================
-- USERS
-- ============================================================

-- Jane Smith:
-- c9a2b536-4311-4d38-b12e-a579aa7f3a98

-- Rahul Sharma:
-- 810d89c3-4ccb-4cbd-8f8c-5711729bbc0c

-- Priya Patel:
-- 6ccecfd5-92ea-4e88-a596-47fded99e4c9

-- Arjun Mehta:
-- 89f31f07-d3c3-488d-a6d7-975a3a267dc6

-- Nitin Misal:
-- c426181e-06ca-453a-a805-997ddb669e5c


-- ============================================================
-- ASSETS
-- ============================================================

-- AST-2001:
-- a1b2c3d4-0001-4a1f-9c1e-000000000001

-- AST-2002:
-- a1b2c3d4-0002-4a1f-9c1e-000000000002

-- AST-2003:
-- a1b2c3d4-0003-4a1f-9c1e-000000000003

-- AST-2004:
-- a1b2c3d4-0004-4a1f-9c1e-000000000004

-- AST-2005:
-- a1b2c3d4-0005-4a1f-9c1e-000000000005

-- AST-2006:
-- a1b2c3d4-0006-4a1f-9c1e-000000000006

-- AST-2007:
-- a1b2c3d4-0007-4a1f-9c1e-000000000007

-- AST-214:
-- d45d3721-538a-4543-b1a3-d0222e8fac47

-- AST-216:
-- 625d8077-df4b-41d0-8850-0fe2fee41dcd


-- ============================================================
-- ASSET MODELS
-- ============================================================

-- HP EliteBook 840 G9:
-- 3f6b0c9e-6d4a-4f7e-8c1b-2a5d9e1f0c02

-- Lenovo ThinkPad X1 Carbon:
-- 3f6b0c9e-6d4a-4f7e-8c1b-2a5d9e1f0c03

-- Dell 27" 4K Monitor P2723QE:
-- 3f6b0c9e-6d4a-4f7e-8c1b-2a5d9e1f0c04

-- Ergonomic Office Chair:
-- 3f6b0c9e-6d4a-4f7e-8c1b-2a5d9e1f0c05


-- ============================================================
-- VENDORS
-- ============================================================

-- TechSupply Co.
-- 2ad14246-9898-4eed-ba36-7de67c1aa8d5

-- Global Tech Distributors
-- 2ad14246-9898-4eed-ba36-7de67c1aa8d6

-- FurniCraft Industries
-- 2ad14246-9898-4eed-ba36-7de67c1aa8d9

-- Post Working Pvt Ltd
-- f3e0287f-7187-4d6f-8df6-bc3802631513

-- TechSorceTech
-- 23cd476b-5729-4f96-a783-18e7ee709df7


-- ============================================================
-- 1. WORK ORDERS
-- ============================================================

INSERT INTO work_orders (
    asset_id,
    type,
    status,
    assigned_vendor_id,
    assigned_technician,
    scheduled_date,
    cost,
    completion_notes,
    created_by,
    closed_by,
    created_at,
    closed_at
)
VALUES

-- AST-2001 / HP EliteBook
(
    'a1b2c3d4-0001-4a1f-9c1e-000000000001',
    'PREVENTIVE',
    'COMPLETED',
    '2ad14246-9898-4eed-ba36-7de67c1aa8d5',
    'c9a2b536-4311-4d38-b12e-a579aa7f3a98',
    '2026-08-15',
    150.00,
    'Routine maintenance completed successfully.',
    '810d89c3-4ccb-4cbd-8f8c-5711729bbc0c',
    '810d89c3-4ccb-4cbd-8f8c-5711729bbc0c',
    '2026-08-15 10:00:00+05:30',
    '2026-08-15 16:00:00+05:30'
),

-- AST-2003 / MacBook
(
    'a1b2c3d4-0003-4a1f-9c1e-000000000003',
    'CORRECTIVE',
    'CREATED',
    '23cd476b-5729-4f96-a783-18e7ee709df7',
    NULL,
    '2026-09-01',
    0.00,
    'Screen flickering issue reported.',
    'c9a2b536-4311-4d38-b12e-a579aa7f3a98',
    NULL,
    '2026-08-12 10:00:00+05:30',
    NULL
),

-- AST-2002 / Lenovo
(
    'a1b2c3d4-0002-4a1f-9c1e-000000000002',
    'PREVENTIVE',
    'COMPLETED',
    '2ad14246-9898-4eed-ba36-7de67c1aa8d6',
    '6ccecfd5-92ea-4e88-a596-47fded99e4c9',
    '2026-08-20',
    200.00,
    'Quarterly maintenance completed.',
    '89f31f07-d3c3-488d-a6d7-975a3a267dc6',
    '89f31f07-d3c3-488d-a6d7-975a3a267dc6',
    '2026-08-20 09:00:00+05:30',
    '2026-08-20 15:00:00+05:30'
),

-- AST-2004 / Monitor
(
    'a1b2c3d4-0004-4a1f-9c1e-000000000004',
    'INSPECTION',
    'ASSIGNED',
    '2ad14246-9898-4eed-ba36-7de67c1aa8d5',
    '89f31f07-d3c3-488d-a6d7-975a3a267dc6',
    '2026-10-01',
    0.00,
    'Routine inspection based on usage.',
    'c426181e-06ca-453a-a805-997ddb669e5c',
    NULL,
    '2026-08-12 11:00:00+05:30',
    NULL
),

-- AST-2006 / Dell Latitude
(
    'a1b2c3d4-0006-4a1f-9c1e-000000000006',
    'CORRECTIVE',
    'IN_PROGRESS',
    '23cd476b-5729-4f96-a783-18e7ee709df7',
    '810d89c3-4ccb-4cbd-8f8c-5711729bbc0c',
    '2026-09-10',
    300.00,
    'Battery replacement in progress.',
    'c9a2b536-4311-4d38-b12e-a579aa7f3a98',
    NULL,
    '2026-08-12 12:00:00+05:30',
    NULL
);


-- ============================================================
-- 2. PREVENTIVE MAINTENANCE SCHEDULES
-- ============================================================

INSERT INTO preventive_maintenance_schedules (
    asset_id,
    frequency_days,
    last_done_date,
    next_due_date,
    auto_generate_work_order
)
VALUES

(
    'a1b2c3d4-0001-4a1f-9c1e-000000000001',
    90,
    '2026-08-15',
    '2026-11-15',
    TRUE
),

(
    'a1b2c3d4-0002-4a1f-9c1e-000000000002',
    90,
    '2026-08-20',
    '2026-11-20',
    TRUE
),

(
    'a1b2c3d4-0004-4a1f-9c1e-000000000004',
    180,
    NULL,
    '2026-10-01',
    TRUE
);


-- ============================================================
-- 3. WARRANTIES
-- ============================================================

INSERT INTO warranties (
    asset_id,
    start_date,
    end_date,
    service_provider,
    alert_days_before
)
VALUES

-- HP EliteBook - 36 month model warranty
(
    'a1b2c3d4-0001-4a1f-9c1e-000000000001',
    '2026-02-10',
    '2029-02-10',
    'HP',
    30
),

-- Lenovo - 48 month model warranty
(
    'a1b2c3d4-0002-4a1f-9c1e-000000000002',
    '2026-03-05',
    '2030-03-05',
    'Lenovo',
    30
),

-- MacBook - illustrative manufacturer warranty
(
    'a1b2c3d4-0003-4a1f-9c1e-000000000003',
    '2026-04-20',
    '2028-04-20',
    'Apple',
    30
),

-- Dell monitor - 24 month model warranty
(
    'a1b2c3d4-0004-4a1f-9c1e-000000000004',
    '2026-01-25',
    '2028-01-25',
    'Dell',
    30
),

-- Office chair - 12 month model warranty
(
    'a1b2c3d4-0007-4a1f-9c1e-000000000007',
    '2026-02-01',
    '2027-02-01',
    'Prime Office Solutions',
    30
);


-- ============================================================
-- 4. ASSET DOCUMENTS
-- ============================================================

INSERT INTO asset_documents (
    asset_id,
    drive_file_id,
    doc_type
)
VALUES

(
    'a1b2c3d4-0001-4a1f-9c1e-000000000001',
    'DRIVE-DEMO-INVOICE-AST-2001',
    'INVOICE'
),

(
    'a1b2c3d4-0001-4a1f-9c1e-000000000001',
    'DRIVE-DEMO-WARRANTY-AST-2001',
    'WARRANTY_CARD'
),

(
    'a1b2c3d4-0003-4a1f-9c1e-000000000003',
    'DRIVE-DEMO-SERVICE-AST-2003',
    'SERVICE_REPORT'
),

(
    'a1b2c3d4-0004-4a1f-9c1e-000000000004',
    'DRIVE-DEMO-INVOICE-AST-2004',
    'INVOICE'
),

(
    'a1b2c3d4-0007-4a1f-9c1e-000000000007',
    'DRIVE-DEMO-INSURANCE-AST-2007',
    'INSURANCE_PAPER'
);


-- ============================================================
-- 5. ASSET REQUESTS
-- ============================================================
--
-- Uses actual users.
-- model_id uses actual asset model IDs.
-- org_id uses the supplied main company ID because the existing
-- Phase-II schema calls this column org_id.
--
-- NOTE:
-- If asset_requests.org_id has an FK to a separate organizations
-- table rather than the company table, replace this value with the
-- corresponding organization UUID before execution.
-- ============================================================

INSERT INTO asset_requests (
    org_id,
    requested_by,
    model_id,
    reason,
    status,
    dept_head_approved_by,
    dept_head_approved_at,
    asset_mgr_approved_by,
    asset_mgr_approved_at,
    fulfilled_asset_id,
    created_at
)
VALUES

-- New HP laptop request
(
    'feb7f5c0-e010-41a7-b3a6-b0fef543d3c8',
    'c9a2b536-4311-4d38-b12e-a579aa7f3a98',
    '3f6b0c9e-6d4a-4f7e-8c1b-2a5d9e1f0c02',
    'Need new laptop for development work.',
    'PENDING_DEPT_HEAD',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    '2026-08-01 10:00:00+05:30'
),

-- Lenovo request
(
    'feb7f5c0-e010-41a7-b3a6-b0fef543d3c8',
    '810d89c3-4ccb-4cbd-8f8c-5711729bbc0c',
    '3f6b0c9e-6d4a-4f7e-8c1b-2a5d9e1f0c03',
    'Laptop required for employee workstation replacement.',
    'PENDING_DEPT_HEAD',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    '2026-08-05 10:00:00+05:30'
),

-- Monitor request approved by department head
(
    'feb7f5c0-e010-41a7-b3a6-b0fef543d3c8',
    '6ccecfd5-92ea-4e88-a596-47fded99e4c9',
    '3f6b0c9e-6d4a-4f7e-8c1b-2a5d9e1f0c04',
    'Additional monitor required for dual-screen setup.',
    'PENDING_ASSET_MGR',
    '89f31f07-d3c3-488d-a6d7-975a3a267dc6',
    '2026-08-10 11:00:00+05:30',
    NULL,
    NULL,
    NULL,
    '2026-08-10 09:00:00+05:30'
),

-- Office chair request fully approved
(
    'feb7f5c0-e010-41a7-b3a6-b0fef543d3c8',
    '89f31f07-d3c3-488d-a6d7-975a3a267dc6',
    '3f6b0c9e-6d4a-4f7e-8c1b-2a5d9e1f0c05',
    'Ergonomic chair required for workstation.',
    'APPROVED',
    'c426181e-06ca-453a-a805-997ddb669e5c',
    '2026-08-12 10:00:00+05:30',
    'c426181e-06ca-453a-a805-997ddb669e5c',
    '2026-08-12 14:00:00+05:30',
    NULL,
    '2026-08-12 08:30:00+05:30'
),

-- Rejected repair/replacement request
(
    'feb7f5c0-e010-41a7-b3a6-b0fef543d3c8',
    'c426181e-06ca-453a-a805-997ddb669e5c',
    '3f6b0c9e-6d4a-4f7e-8c1b-2a5d9e1f0c02',
    'Existing asset requires replacement due to repeated performance issues.',
    'REJECTED',
    'c9a2b536-4311-4d38-b12e-a579aa7f3a98',
    '2026-08-11 10:00:00+05:30',
    '810d89c3-4ccb-4cbd-8f8c-5711729bbc0c',
    '2026-08-11 15:00:00+05:30',
    NULL,
    '2026-08-11 09:00:00+05:30'
);


-- ============================================================
-- 6. ASSET AUDITS
-- ============================================================

INSERT INTO asset_audits (
    org_id,
    branch_id,
    conducted_by,
    audit_date,
    system_count,
    physical_count,
    discrepancy_notes,
    status
)
VALUES

(
    'feb7f5c0-e010-41a7-b3a6-b0fef543d3c8',
    '6e958647-1cd1-4931-8909-119d46b97119',
    'c9a2b536-4311-4d38-b12e-a579aa7f3a98',
    '2026-08-01',
    1,
    1,
    NULL,
    'COMPLETED'
),

(
    'feb7f5c0-e010-41a7-b3a6-b0fef543d3c8',
    '6e958647-1cd1-4931-8909-119d46b97119',
    '810d89c3-4ccb-4cbd-8f8c-5711729bbc0c',
    '2026-08-02',
    1,
    1,
    NULL,
    'COMPLETED'
),

(
    'feb7f5c0-e010-41a7-b3a6-b0fef543d3c8',
    '6e958647-1cd1-4931-8909-119d46b97119',
    '6ccecfd5-92ea-4e88-a596-47fded99e4c9',
    '2026-08-03',
    1,
    1,
    'Minor scratches on screen.',
    'COMPLETED'
),

(
    'feb7f5c0-e010-41a7-b3a6-b0fef543d3c8',
    '6e958647-1cd1-4931-8909-119d46b97119',
    '89f31f07-d3c3-488d-a6d7-975a3a267dc6',
    '2026-08-04',
    1,
    1,
    NULL,
    'COMPLETED'
),

(
    'feb7f5c0-e010-41a7-b3a6-b0fef543d3c8',
    '6e958647-1cd1-4931-8909-119d46b97119',
    'c426181e-06ca-453a-a805-997ddb669e5c',
    '2026-08-05',
    1,
    0,
    'Missing power cable; replacement required.',
    'COMPLETED'
);


-- ============================================================
-- 7. ASSET AUDIT ITEMS
-- ============================================================
--
-- Uses audit IDs created above by matching audit_date and
-- conducted_by. This avoids assuming UUID values for audits.
-- ============================================================

INSERT INTO asset_audit_items (
    audit_id,
    asset_id,
    expected_location,
    found,
    found_location,
    scanned_at
)
SELECT
    aa.id,
    v.asset_id,
    v.expected_location,
    v.found,
    v.found_location,
    v.scanned_at
FROM (
    VALUES
    (
        '2026-08-01'::DATE,
        'c9a2b536-4311-4d38-b12e-a579aa7f3a98'::UUID,
        'a1b2c3d4-0001-4a1f-9c1e-000000000001'::UUID,
        'Chennai Office - Ground Floor',
        TRUE,
        'Chennai Office - Ground Floor',
        '2026-08-01 11:00:00+05:30'::TIMESTAMPTZ
    ),
    (
        '2026-08-02'::DATE,
        '810d89c3-4ccb-4cbd-8f8c-5711729bbc0c'::UUID,
        'a1b2c3d4-0002-4a1f-9c1e-000000000002'::UUID,
        'Pune Office - 4th Floor',
        TRUE,
        'Pune Office - 4th Floor',
        '2026-08-02 11:00:00+05:30'::TIMESTAMPTZ
    ),
    (
        '2026-08-03'::DATE,
        '6ccecfd5-92ea-4e88-a596-47fded99e4c9'::UUID,
        'a1b2c3d4-0004-4a1f-9c1e-000000000004'::UUID,
        'Chennai Technology Center',
        TRUE,
        'Chennai Technology Center',
        '2026-08-03 11:00:00+05:30'::TIMESTAMPTZ
    ),
    (
        '2026-08-04'::DATE,
        '89f31f07-d3c3-488d-a6d7-975a3a267dc6'::UUID,
        'a1b2c3d4-0007-4a1f-9c1e-000000000007'::UUID,
        'Pune Office - 3rd Floor',
        TRUE,
        'Pune Office - 3rd Floor',
        '2026-08-04 11:00:00+05:30'::TIMESTAMPTZ
    ),
    (
        '2026-08-05'::DATE,
        'c426181e-06ca-453a-a805-997ddb669e5c'::UUID,
        'a1b2c3d4-0005-4a1f-9c1e-000000000005'::UUID,
        'Chennai Technology Center',
        FALSE,
        NULL,
        '2026-08-05 11:00:00+05:30'::TIMESTAMPTZ
    )
) AS v(
    audit_date,
    conducted_by,
    asset_id,
    expected_location,
    found,
    found_location,
    scanned_at
)
JOIN asset_audits aa
    ON aa.audit_date = v.audit_date
   AND aa.conducted_by = v.conducted_by
   AND aa.org_id = 'feb7f5c0-e010-41a7-b3a6-b0fef543d3c8';


-- ============================================================
-- 8. VERIFICATION QUERIES
-- ============================================================

-- Work orders
SELECT
    wo.id,
    a.asset_code,
    a.name AS asset_name,
    wo.type,
    wo.status,
    wo.cost,
    wo.scheduled_date
FROM work_orders wo
JOIN assets a
    ON a.id = wo.asset_id
ORDER BY wo.created_at;


-- Preventive maintenance
SELECT
    pm.id,
    a.asset_code,
    a.name AS asset_name,
    pm.frequency_days,
    pm.last_done_date,
    pm.next_due_date
FROM preventive_maintenance_schedules pm
JOIN assets a
    ON a.id = pm.asset_id
ORDER BY pm.next_due_date;


-- Warranties
SELECT
    w.id,
    a.asset_code,
    a.name AS asset_name,
    w.start_date,
    w.end_date,
    w.service_provider
FROM warranties w
JOIN assets a
    ON a.id = w.asset_id
ORDER BY w.end_date;


-- Asset requests
SELECT
    ar.id,
    u.full_name AS requested_by,
    am.name AS requested_model,
    ar.status,
    ar.reason,
    ar.created_at
FROM asset_requests ar
JOIN users u
    ON u.id = ar.requested_by
LEFT JOIN asset_models am
    ON am.id = ar.model_id
ORDER BY ar.created_at;


-- Asset audits
SELECT
    aa.id,
    aa.audit_date,
    u.full_name AS conducted_by,
    aa.system_count,
    aa.physical_count,
    aa.status
FROM asset_audits aa
JOIN users u
    ON u.id = aa.conducted_by
ORDER BY aa.audit_date;


-- ============================================================
-- END OF PHASE II SEED DATA
-- ============================================================

-- ============================================================
-- ASSET AUDIT ITEMS
-- Uses the actual asset_audits IDs from the database
-- ============================================================

INSERT INTO asset_audit_items (
    audit_id,
    asset_id,
    expected_location,
    found,
    found_location,
    scanned_at
)
VALUES

-- Jane Smith - 2026-08-01
(
    'fa45bca0-4843-4cf2-9dbb-4c0495eee41b',
    'a1b2c3d4-0001-4a1f-9c1e-000000000001',
    'Chennai Office - Ground Floor',
    TRUE,
    'Chennai Office - Ground Floor',
    '2026-08-01 11:00:00+05:30'
),

-- Rahul Sharma - 2026-08-02
(
    '4dd77cb7-7887-429e-b37b-82c77a171535',
    'a1b2c3d4-0002-4a1f-9c1e-000000000002',
    'Pune Office - 4th Floor',
    TRUE,
    'Pune Office - 4th Floor',
    '2026-08-02 11:00:00+05:30'
),

-- Priya Patel - 2026-08-03
(
    '2bda0d60-a1da-4f36-9ab5-80af10034f0a',
    'a1b2c3d4-0004-4a1f-9c1e-000000000004',
    'Chennai Technology Center',
    TRUE,
    'Chennai Technology Center',
    '2026-08-03 11:00:00+05:30'
),

-- Arjun Mehta - 2026-08-04
(
    'ea70ad4c-23d4-4462-8684-7be796d35432',
    'a1b2c3d4-0007-4a1f-9c1e-000000000007',
    'Pune Office - 3rd Floor',
    TRUE,
    'Pune Office - 3rd Floor',
    '2026-08-04 11:00:00+05:30'
),

-- Nitin Misal - 2026-08-05
-- Physical count = 0, therefore asset was NOT found
(
    'a5128d7f-81af-497d-8596-db46217f24cd',
    'a1b2c3d4-0005-4a1f-9c1e-000000000005',
    'Chennai Technology Center',
    FALSE,
    NULL,
    '2026-08-05 11:00:00+05:30'
);
