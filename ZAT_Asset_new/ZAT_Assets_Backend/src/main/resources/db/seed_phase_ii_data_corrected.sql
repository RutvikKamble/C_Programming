-- ============================================================
-- ZAT ASSET MANAGEMENT - PHASE II SEED DATA
-- Dynamically uses existing asset IDs from the assets table
-- Requires at least 5 assets (adjust LIMIT if necessary)
-- ============================================================

-- -----------------------------------------------------------------
-- 1. ASSET MAINTENANCE
-- -----------------------------------------------------------------
WITH asset_list AS (
    SELECT id, ROW_NUMBER() OVER (ORDER BY id) AS rn
    FROM assets
    LIMIT 5
)
INSERT INTO asset_maintenance (asset_id, maintenance_type, scheduled_date, completed_date, performed_by, cost, status, remarks, next_due_date)
SELECT
    (SELECT id FROM asset_list WHERE rn = 1),
    'PREVENTIVE', '2024-01-15'::date, '2024-01-15'::date, 'John Doe', 150.00, 'COMPLETED', 'Regular maintenance check', '2024-07-15'::date
UNION ALL
SELECT
    (SELECT id FROM asset_list WHERE rn = 1),
    'CORRECTIVE', '2024-03-20'::date, '2024-03-22'::date, 'Jane Smith', 450.00, 'COMPLETED', 'Fixed display issue', '2024-09-20'::date
UNION ALL
SELECT
    (SELECT id FROM asset_list WHERE rn = 2),
    'PREVENTIVE', '2024-02-10'::date, NULL, NULL, NULL, 'SCHEDULED', 'Quarterly maintenance', '2024-05-10'::date
UNION ALL
SELECT
    (SELECT id FROM asset_list WHERE rn = 3),
    'PREDICTIVE', '2024-04-01'::date, NULL, NULL, NULL, 'IN_PROGRESS', 'Predictive analysis', '2024-10-01'::date
UNION ALL
SELECT
    (SELECT id FROM asset_list WHERE rn = 4),
    'SCHEDULED', '2024-05-15'::date, NULL, NULL, NULL, 'SCHEDULED', 'Annual inspection', '2025-05-15'::date;

-- -----------------------------------------------------------------
-- 2. ASSET WARRANTY (with RETURNING for coverage details)
-- -----------------------------------------------------------------
WITH asset_list AS (
    SELECT id, ROW_NUMBER() OVER (ORDER BY id) AS rn
    FROM assets
    LIMIT 5
),
inserted_warranties AS (
    INSERT INTO asset_warranty (asset_id, warranty_type, provider, start_date, end_date, status)
    SELECT
        (SELECT id FROM asset_list WHERE rn = 1),
        'MANUFACTURER', 'Dell Inc', '2023-01-01'::date, '2025-01-01'::date, 'ACTIVE'
    UNION ALL
    SELECT
        (SELECT id FROM asset_list WHERE rn = 2),
        'EXTENDED', 'TechCare Solutions', '2023-06-01'::date, '2025-06-01'::date, 'ACTIVE'
    UNION ALL
    SELECT
        (SELECT id FROM asset_list WHERE rn = 3),
        'AMC', 'Global IT Services', '2023-03-01'::date, '2024-03-01'::date, 'EXPIRED'
    UNION ALL
    SELECT
        (SELECT id FROM asset_list WHERE rn = 4),
        'MANUFACTURER', 'HP', '2023-08-01'::date, '2024-08-01'::date, 'ACTIVE'
    UNION ALL
    SELECT
        (SELECT id FROM asset_list WHERE rn = 5),
        'EXTENDED', 'ServicePro', '2023-04-01'::date, '2025-04-01'::date, 'ACTIVE'
    RETURNING id, provider
)
-- 3. WARRANTY COVERAGE DETAILS
INSERT INTO asset_warranty_coverage_details (warranty_id, detail)
SELECT
    w.id,
    d.detail
FROM inserted_warranties w
CROSS JOIN LATERAL (
    VALUES
        ('Hardware replacement'),
        ('On-site support')
) AS d(detail)
WHERE w.provider = 'Dell Inc'
UNION ALL
SELECT w.id, 'Extended hardware coverage'
FROM inserted_warranties w WHERE w.provider = 'TechCare Solutions'
UNION ALL
SELECT w.id, '24/7 phone support'
FROM inserted_warranties w WHERE w.provider = 'TechCare Solutions'
UNION ALL
SELECT w.id, 'Quarterly maintenance visits'
FROM inserted_warranties w WHERE w.provider = 'Global IT Services'
UNION ALL
SELECT w.id, 'Parts and labor'
FROM inserted_warranties w WHERE w.provider = 'Global IT Services'
UNION ALL
SELECT w.id, 'Standard warranty coverage'
FROM inserted_warranties w WHERE w.provider = 'HP'
UNION ALL
SELECT w.id, 'Accidental damage protection'
FROM inserted_warranties w WHERE w.provider = 'ServicePro'
UNION ALL
SELECT w.id, 'Theft protection'
FROM inserted_warranties w WHERE w.provider = 'ServicePro';

-- -----------------------------------------------------------------
-- 4. ASSET DOCUMENTS
-- -----------------------------------------------------------------
WITH asset_list AS (
    SELECT id, ROW_NUMBER() OVER (ORDER BY id) AS rn
    FROM assets
    LIMIT 5
)
INSERT INTO asset_document (asset_id, document_type, document_name, file_path, uploaded_by, uploaded_date, expiry_date)
SELECT
    (SELECT id FROM asset_list WHERE rn = 1),
    'INVOICE', 'Dell Laptop Invoice', '/zat-drive/docs/invoices/dell-laptop-inv-001.pdf', 'Admin User', '2023-01-15'::date, NULL
UNION ALL
SELECT
    (SELECT id FROM asset_list WHERE rn = 1),
    'WARRANTY_CARD', 'Dell Warranty Card', '/zat-drive/docs/warranties/dell-warranty-001.pdf', 'Admin User', '2023-01-15'::date, '2025-01-15'::date
UNION ALL
SELECT
    (SELECT id FROM asset_list WHERE rn = 2),
    'INSURANCE', 'Equipment Insurance Policy', '/zat-drive/docs/insurance/monitor-insurance-001.pdf', 'Admin User', '2023-06-20'::date, '2024-06-20'::date
UNION ALL
SELECT
    (SELECT id FROM asset_list WHERE rn = 3),
    'MANUAL', 'Server User Manual', '/zat-drive/docs/manuals/server-manual-001.pdf', 'Admin User', '2023-03-10'::date, NULL
UNION ALL
SELECT
    (SELECT id FROM asset_list WHERE rn = 4),
    'AMC_CONTRACT', 'Printer AMC Agreement', '/zat-drive/docs/amc/printer-amc-001.pdf', 'Admin User', '2023-08-05'::date, '2024-08-05'::date
UNION ALL
SELECT
    (SELECT id FROM asset_list WHERE rn = 5),
    'INVOICE', 'Network Switch Invoice', '/zat-drive/docs/invoices/switch-inv-001.pdf', 'Admin User', '2023-04-12'::date, NULL;

-- -----------------------------------------------------------------
-- 5. ASSET REQUESTS
-- -----------------------------------------------------------------
INSERT INTO asset_request (asset_id, request_type, requested_category, employee_id, employee_name, department, request_date, priority, status, approved_by, remarks) VALUES
(NULL, 'NEW_ASSET', 'Laptop', 'EMP001', 'Alice Johnson', 'Engineering', '2024-01-10'::date, 'HIGH', 'ACCEPT', 'Manager John', 'Required for new project'),
(NULL, 'NEW_ASSET', 'Monitor', 'EMP002', 'Bob Smith', 'Design', '2024-02-15'::date, 'MEDIUM', 'SEND', NULL, 'Secondary monitor for design work'),
((SELECT id FROM (SELECT id, ROW_NUMBER() OVER (ORDER BY id) AS rn FROM assets LIMIT 5) a WHERE rn = 3),
 'REPAIR', NULL, 'EMP003', 'Carol White', 'IT', '2024-03-20'::date, 'HIGH', 'ACCEPT', 'Asset Manager Jane', 'Server cooling issue'),
((SELECT id FROM (SELECT id, ROW_NUMBER() OVER (ORDER BY id) AS rn FROM assets LIMIT 5) a WHERE rn = 4),
 'REPLACEMENT', NULL, 'EMP004', 'David Brown', 'HR', '2024-04-05'::date, 'MEDIUM', 'REJECT', 'Manager John', 'Budget constraints'),
(NULL, 'NEW_ASSET', 'Tablet', 'EMP005', 'Eva Green', 'Sales', '2024-05-01'::date, 'LOW', 'SEND', NULL, 'For client presentations');

-- -----------------------------------------------------------------
-- 6. ASSET AUDITS
-- -----------------------------------------------------------------
WITH asset_list AS (
    SELECT id, ROW_NUMBER() OVER (ORDER BY id) AS rn
    FROM assets
    LIMIT 5
)
INSERT INTO asset_audit (asset_id, audit_date, audited_by, location_verified, condition, discrepancy_notes, status)
SELECT
    (SELECT id FROM asset_list WHERE rn = 1),
    '2024-01-20'::date, ' Auditor Mike', 'Engineering Floor 2', 'Good', NULL, 'Verified'
UNION ALL
SELECT
    (SELECT id FROM asset_list WHERE rn = 2),
    '2024-02-15'::date, 'Auditor Sarah', 'Design Room 3', 'Good', NULL, 'Verified'
UNION ALL
SELECT
    (SELECT id FROM asset_list WHERE rn = 3),
    '2024-03-10'::date, 'Auditor Tom', 'Server Room A', 'NeedsRepair', 'Cooling system showing wear', 'DiscrepancyFound'
UNION ALL
SELECT
    (SELECT id FROM asset_list WHERE rn = 4),
    '2024-04-05'::date, 'Auditor Lisa', 'HR Department', 'Good', NULL, 'Verified'
UNION ALL
SELECT
    (SELECT id FROM asset_list WHERE rn = 5),
    '2024-05-01'::date, 'Auditor Kevin', 'IT Storage', 'Damaged', 'Minor physical damage to case', 'DiscrepancyFound';

-- ============================================================
-- END OF SEED FILE
-- ============================================================