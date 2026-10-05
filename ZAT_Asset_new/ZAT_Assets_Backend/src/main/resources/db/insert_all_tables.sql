-- ============================================================
-- ZAT Asset Management — Sample Data: 5 records for EACH table
-- Covers all backend entities/tables.
--
-- Dependency order is respected (parents inserted first).
-- Re-runnable: uses ON CONFLICT (id) DO NOTHING.
-- Note: asset_technician uses IDENTITY (auto id), so no id column.
-- Paste into Supabase SQL Editor, or run via psql.
-- ============================================================

BEGIN;

-- ============================================================
-- 1) companies
-- ============================================================
INSERT INTO companies (id, company_name, legal_name, gstin_tax_id, created_at, updated_at) VALUES
('feb7f5c0-e010-41a7-b3a6-b0fef543d3c8','Verzat R&D Pvt Ltd','Verzat Research & Development Private Limited','27AAAAA0000A1Z6',NOW(),NOW()),
('feb7f5c0-e010-41a7-b3a6-b0fef543d3c9','TechNova Solutions','TechNova Solutions Limited','27BBBBB0000B1Z6',NOW(),NOW()),
('feb7f5c0-e010-41a7-b3a6-b0fef543d3d0','Innovatech Systems','Innovatech Systems Pvt Ltd','27CCCCC0000C1Z6',NOW(),NOW()),
('feb7f5c0-e010-41a7-b3a6-b0fef543d3d1','Zenith Manufacturing','Zenith Manufacturing Corp','27DDDDD0000D1Z6',NOW(),NOW()),
('feb7f5c0-e010-41a7-b3a6-b0fef543d3d2','Orbit Retail Group','Orbit Retail Group Limited','27EEEEE0000E1Z6',NOW(),NOW())
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 2) branches
-- ============================================================
INSERT INTO branches (id, company_id, name, address, city, state, country) VALUES
('6e958647-1cd1-4931-8909-119d46b97119','feb7f5c0-e010-41a7-b3a6-b0fef543d3c8','Chennai Technology Center','DLF Cybercity, Manapakkam','Chennai','Tamil Nadu','India'),
('6e958647-1cd1-4931-8909-119d46b97120','feb7f5c0-e010-41a7-b3a6-b0fef543d3c8','Pune Office','Magarpatta City, Hadapsar','Pune','Maharashtra','India'),
('6e958647-1cd1-4931-8909-119d46b97121','feb7f5c0-e010-41a7-b3a6-b0fef543d3c9','Mumbai HQ','BKC, Bandra East','Mumbai','Maharashtra','India'),
('6e958647-1cd1-4931-8909-119d46b97122','feb7f5c0-e010-41a7-b3a6-b0fef543d3d0','Bengaluru R&D','Whitefield, ITPL','Bengaluru','Karnataka','India'),
('6e958647-1cd1-4931-8909-119d46b97123','feb7f5c0-e010-41a7-b3a6-b0fef543d3c8','Delhi Showroom','Connaught Place','New Delhi','Delhi','India')
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 3) departments
-- ============================================================
INSERT INTO departments (id, company_id, branch_id, name) VALUES
('56377df6-6838-42bf-b3ba-8cc2067b6516','feb7f5c0-e010-41a7-b3a6-b0fef543d3c8','6e958647-1cd1-4931-8909-119d46b97119','Engineering'),
('56377df6-6838-42bf-b3ba-8cc2067b6517','feb7f5c0-e010-41a7-b3a6-b0fef543d3c8','6e958647-1cd1-4931-8909-119d46b97119','Human Resources'),
('56377df6-6838-42bf-b3ba-8cc2067b6518','feb7f5c0-e010-41a7-b3a6-b0fef543d3c8','6e958647-1cd1-4931-8909-119d46b97119','Finance'),
('56377df6-6838-42bf-b3ba-8cc2067b6519','feb7f5c0-e010-41a7-b3a6-b0fef543d3c9','6e958647-1cd1-4931-8909-119d46b97121','Operations'),
('56377df6-6838-42bf-b3ba-8cc2067b6520','feb7f5c0-e010-41a7-b3a6-b0fef543d3d0','6e958647-1cd1-4931-8909-119d46b97122','Product Design')
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 4) asset_categories
-- ============================================================
INSERT INTO asset_categories (id, company_id, parent_id, category, depreciation_method, useful_life_months) VALUES
('1dfca099-fca1-45f2-81c6-a47ddcc6e380','feb7f5c0-e010-41a7-b3a6-b0fef543d3c8',NULL,'IT','STRAIGHT_LINE',60),
('481ba72d-e739-43fc-bf66-60f42f3940fd','feb7f5c0-e010-41a7-b3a6-b0fef543d3c8',NULL,'IT','STRAIGHT_LINE',48),
('6224082e-dab1-4f12-a3b2-7bf10030513b','feb7f5c0-e010-41a7-b3a6-b0fef543d3c8',NULL,'IT','STRAIGHT_LINE',36),
('6224082e-dab1-4f12-a3b2-7bf10030513c','feb7f5c0-e010-41a7-b3a6-b0fef543d3c8',NULL,'Furniture','DECLINING_BALANCE',60),
('6224082e-dab1-4f12-a3b2-7bf10030513d','feb7f5c0-e010-41a7-b3a6-b0fef543d3c8',NULL,'Vehicle','STRAIGHT_LINE',84)
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 5) vendors
-- ============================================================
INSERT INTO vendors (id, company_id, name, contact_email, contact_phone, address, created_at) VALUES
('2ad14246-9898-4eed-ba36-7de67c1aa8d5','feb7f5c0-e010-41a7-b3a6-b0fef543d3c8','TechSupply Co.','raj@techsupply.com','9876543210','MIDC, Pune',NOW()),
('2ad14246-9898-4eed-ba36-7de67c1aa8d6','feb7f5c0-e010-41a7-b3a6-b0fef543d3c8','Global Tech Distributors','sales@globaltech.com','9876543211','BKC, Mumbai',NOW()),
('2ad14246-9898-4eed-ba36-7de67c1aa8d7','feb7f5c0-e010-41a7-b3a6-b0fef543d3c9','Prime Office Solutions','info@primeoffice.com','9876543212','Whitefield, Bengaluru',NOW()),
('2ad14246-9898-4eed-ba36-7de67c1aa8d8','feb7f5c0-e010-41a7-b3a6-b0fef543d3d0','AutoServe Motors','parts@autoserve.com','9876543213','Motor City, Chennai',NOW()),
('2ad14246-9898-4eed-ba36-7de67c1aa8d9','feb7f5c0-e010-41a7-b3a6-b0fef543d3c8','FurniCraft Industries','hello@furnicraft.com','9876543214','Ambattur, Chennai',NOW())
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 6) users (employees)
-- ============================================================
INSERT INTO users (id, company_id, branch_id, department_id, employee_code, full_name, email, phone, password_hash, status) VALUES
('c9a2b536-4311-4d38-b12e-a579aa7f3a98','feb7f5c0-e010-41a7-b3a6-b0fef543d3c8','6e958647-1cd1-4931-8909-119d46b97119','56377df6-6838-42bf-b3ba-8cc2067b6516','EMP-001','Jane Smith','jane.smith@company.com','9876543210','changeme123','ACTIVE'),
('810d89c3-4ccb-4cbd-8f8c-5711729bbc0c','feb7f5c0-e010-41a7-b3a6-b0fef543d3c8','6e958647-1cd1-4931-8909-119d46b97119','56377df6-6838-42bf-b3ba-8cc2067b6516','EMP-002','Rahul Sharma','rahul.sharma@company.com','9876543211','changeme123','ACTIVE'),
('6ccecfd5-92ea-4e88-a596-47fded99e4c9','feb7f5c0-e010-41a7-b3a6-b0fef543d3c8','6e958647-1cd1-4931-8909-119d46b97119','56377df6-6838-42bf-b3ba-8cc2067b6516','EMP-003','Priya Patel','priya.patel@company.com','9876543212','changeme123','ACTIVE'),
('89f31f07-d3c3-488d-a6d7-975a3a267dc6','feb7f5c0-e010-41a7-b3a6-b0fef543d3c8','6e958647-1cd1-4931-8909-119d46b97119','56377df6-6838-42bf-b3ba-8cc2067b6516','EMP-004','Arjun Mehta','arjun.mehta@company.com','9876543213','changeme123','ACTIVE'),
('88965b81-e9b2-413c-a1f1-77fa99157e21','feb7f5c0-e010-41a7-b3a6-b0fef543d3c8','6e958647-1cd1-4931-8909-119d46b97119','56377df6-6838-42bf-b3ba-8cc2067b6516','EMP-005','Neha Verma','neha.verma@company.com','9876543214','changeme123','ACTIVE')
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 7) assets (5 records)
-- ============================================================
INSERT INTO assets (id, company_id, branch_id, department_id, asset_code, name, category_id, asset_type, serial_number, vendor_id, purchase_date, purchase_cost, current_value, status, current_owner_id, current_location, qr_code_value, barcode_value, created_at, updated_at) VALUES
('4774b0a5-c063-4052-a590-69f962423ae4','feb7f5c0-e010-41a7-b3a6-b0fef543d3c8','6e958647-1cd1-4931-8909-119d46b97119','56377df6-6838-42bf-b3ba-8cc2067b6516','AST-1001','Dell Latitude 5440 Laptop','6224082e-dab1-4f12-a3b2-7bf10030513b','IT_ASSET','SN-DL5440-001','2ad14246-9898-4eed-ba36-7de67c1aa8d5','2026-01-15',75000.00,70000.00,'AVAILABLE','c9a2b536-4311-4d38-b12e-a579aa7f3a98','Pune Office - 4th Floor','AST:AST-1001:4774b0a5-c063-4052-a590-69f962423ae4','AST-1001',NOW(),NOW()),
('a1b2c3d4-0001-4a1f-9c1e-000000000001','feb7f5c0-e010-41a7-b3a6-b0fef543d3c8','6e958647-1cd1-4931-8909-119d46b97119','56377df6-6838-42bf-b3ba-8cc2067b6516','AST-2001','HP EliteBook 840 G9','6224082e-dab1-4f12-a3b2-7bf10030513b','IT_ASSET','SN-HP840-1001','2ad14246-9898-4eed-ba36-7de67c1aa8d5','2026-02-10',82000.00,78000.00,'AVAILABLE','c9a2b536-4311-4d38-b12e-a579aa7f3a98','Chennai Office - Ground Floor','AST:AST-2001:a1b2c3d4-0001-4a1f-9c1e-000000000001','AST-2001',NOW(),NOW()),
('a1b2c3d4-0002-4a1f-9c1e-000000000002','feb7f5c0-e010-41a7-b3a6-b0fef543d3c8','6e958647-1cd1-4931-8909-119d46b97119','56377df6-6838-42bf-b3ba-8cc2067b6516','AST-2002','Lenovo ThinkPad X1 Carbon','6224082e-dab1-4f12-a3b2-7bf10030513b','IT_ASSET','SN-LNVX1-2002','2ad14246-9898-4eed-ba36-7de67c1aa8d5','2026-03-05',95000.00,91000.00,'ASSIGNED','c9a2b536-4311-4d38-b12e-a579aa7f3a98','Pune Office - 4th Floor','AST:AST-2002:a1b2c3d4-0002-4a1f-9c1e-000000000002','AST-2002',NOW(),NOW()),
('a1b2c3d4-0003-4a1f-9c1e-000000000003','feb7f5c0-e010-41a7-b3a6-b0fef543d3c8','6e958647-1cd1-4931-8909-119d46b97119','56377df6-6838-42bf-b3ba-8cc2067b6516','AST-2003','Apple MacBook Pro 14','6224082e-dab1-4f12-a3b2-7bf10030513b','IT_ASSET','SN-MBP14-2003','2ad14246-9898-4eed-ba36-7de67c1aa8d5','2026-04-20',145000.00,140000.00,'IN_REPAIR',NULL,'Service Center - Repair Bay 2','AST:AST-2003:a1b2c3d4-0003-4a1f-9c1e-000000000003','AST-2003',NOW(),NOW()),
('a1b2c3d4-0004-4a1f-9c1e-000000000004','feb7f5c0-e010-41a7-b3a6-b0fef543d3c8','6e958647-1cd1-4931-8909-119d46b97119','56377df6-6838-42bf-b3ba-8cc2067b6516','AST-2004','Dell 27" 4K Monitor P2723QE','6224082e-dab1-4f12-a3b2-7bf10030513b','IT_ASSET','SN-DL27-2004','2ad14246-9898-4eed-ba36-7de67c1aa8d5','2026-01-25',32000.00,30000.00,'AVAILABLE',NULL,'Chennai Office - Storage Room','AST:AST-2004:a1b2c3d4-0004-4a1f-9c1e-000000000004','AST-2004',NOW(),NOW())
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 8) asset_allocations (5 records)
-- ============================================================
INSERT INTO asset_allocations (id, asset_id, employee_id, department_id, branch_id, allocated_on, returned_on, status, acknowledged_by_employee) VALUES
('01b55538-55cb-400a-a21c-e50f188411a7','4774b0a5-c063-4052-a590-69f962423ae4','c9a2b536-4311-4d38-b12e-a579aa7f3a98','56377df6-6838-42bf-b3ba-8cc2067b6516','6e958647-1cd1-4931-8909-119d46b97119','2026-08-01',NULL,'ALLOCATED',false),
('01b55538-55cb-400a-a21c-e50f188411a8','a1b2c3d4-0001-4a1f-9c1e-000000000001','810d89c3-4ccb-4cbd-8f8c-5711729bbc0c','56377df6-6838-42bf-b3ba-8cc2067b6516','6e958647-1cd1-4931-8909-119d46b97119','2026-08-02',NULL,'ALLOCATED',true),
('01b55538-55cb-400a-a21c-e50f188411a9','a1b2c3d4-0002-4a1f-9c1e-000000000002','6ccecfd5-92ea-4e88-a596-47fded99e4c9','56377df6-6838-42bf-b3ba-8cc2067b6516','6e958647-1cd1-4931-8909-119d46b97119','2026-08-03',NULL,'ALLOCATED',true),
('01b55538-55cb-400a-a21c-e50f188411b0','a1b2c3d4-0003-4a1f-9c1e-000000000003','c9a2b536-4311-4d38-b12e-a579aa7f3a98','56377df6-6838-42bf-b3ba-8cc2067b6516','6e958647-1cd1-4931-8909-119d46b97119','2026-07-20','2026-07-25','RETURNED',true),
('01b55538-55cb-400a-a21c-e50f188411b1','a1b2c3d4-0004-4a1f-9c1e-000000000004','89f31f07-d3c3-488d-a6d7-975a3a267dc6','56377df6-6838-42bf-b3ba-8cc2067b6516','6e958647-1cd1-4931-8909-119d46b97119','2026-08-04',NULL,'ALLOCATED',false)
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 9) asset_returns (5 records)
-- ============================================================
INSERT INTO asset_returns (id, allocation_id, requested_by, reason, status, verified_by, verified_on) VALUES
('97dcab58-4bdc-4c92-9d5b-ce4783fcce88','01b55538-55cb-400a-a21c-e50f188411a7','c9a2b536-4311-4d38-b12e-a579aa7f3a98','RESIGNATION','REQUESTED',NULL,NULL),
('97dcab58-4bdc-4c92-9d5b-ce4783fcce89','01b55538-55cb-400a-a21c-e50f188411a8','810d89c3-4ccb-4cbd-8f8c-5711729bbc0c','UPGRADE','VERIFIED','c9a2b536-4311-4d38-b12e-a579aa7f3a98','2026-08-05'),
('97dcab58-4bdc-4c92-9d5b-ce4783fcce8a','01b55538-55cb-400a-a21c-e50f188411a9','6ccecfd5-92ea-4e88-a596-47fded99e4c9','DAMAGED','COMPLETED','c9a2b536-4311-4d38-b12e-a579aa7f3a98','2026-08-06'),
('97dcab58-4bdc-4c92-9d5b-ce4783fcce8b','01b55538-55cb-400a-a21c-e50f188411b0','c9a2b536-4311-4d38-b12e-a579aa7f3a98','TRANSFER','VERIFIED',NULL,NULL),
('97dcab58-4bdc-4c92-9d5b-ce4783fcce8c','01b55538-55cb-400a-a21c-e50f188411b1','89f31f07-d3c3-488d-a6d7-975a3a267dc6','RESIGNATION','COMPLETED','c9a2b536-4311-4d38-b12e-a579aa7f3a98','2026-08-07')
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 10) asset_technician (IDENTITY id — auto generated)
-- ============================================================
INSERT INTO asset_technician (technician_name, is_internal, employee_ref, specialization, phone, email, status) VALUES
('Ravi Kumar',true,'EMP-006','Hardware','9876500001','ravi.k@company.com','Active'),
('Suresh Nair',true,'EMP-007','Networking','9876500002','suresh.n@company.com','Active'),
('Anita Desai',false,NULL,'Electrical','9876500003','anita@globalservice.com','Active'),
('Vikram Singh',false,NULL,'Vehicle Mechanic','9876500004','vikram@autoserve.com','Inactive'),
('Meera Joshi',true,'EMP-008','Hardware','9876500005','meera.j@company.com','Active');

-- ============================================================
-- 11) asset_history (5 records)
-- Table may not exist yet (newer module) — create if missing.
-- ============================================================
CREATE TABLE IF NOT EXISTS asset_history (
    id UUID PRIMARY KEY,
    asset_id UUID NOT NULL REFERENCES assets(id),
    action VARCHAR(50) NOT NULL,
    field_changed VARCHAR(100),
    old_value TEXT,
    new_value TEXT,
    description TEXT,
    changed_by VARCHAR(150),
    created_at TIMESTAMP DEFAULT NOW()
);

INSERT INTO asset_history (id, asset_id, action, field_changed, old_value, new_value, description, changed_by, created_at) VALUES
('a1f00000-0001-4000-8000-000000000001','4774b0a5-c063-4052-a590-69f962423ae4','CREATED',NULL,NULL,NULL,'Asset created','system',NOW()),
('a1f00000-0002-4000-8000-000000000002','4774b0a5-c063-4052-a590-69f962423ae4','STATUS_CHANGED','status','AVAILABLE','ASSIGNED','Asset assigned to employee','system',NOW()),
('a1f00000-0003-4000-8000-000000000003','a1b2c3d4-0001-4a1f-9c1e-000000000001','CREATED',NULL,NULL,NULL,'Asset created','system',NOW()),
('a1f00000-0004-4000-8000-000000000004','a1b2c3d4-0002-4a1f-9c1e-000000000002','UPDATED','currentLocation','Pune Office','Chennai Office','Location updated','system',NOW()),
('a1f00000-0005-4000-8000-000000000005','a1b2c3d4-0003-4a1f-9c1e-000000000003','STATUS_CHANGED','status','AVAILABLE','IN_REPAIR','Sent for repair','system',NOW())
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 12) asset_tracking (5 records)
-- Table may not exist yet (newer module) — create if missing.
-- ============================================================
CREATE TABLE IF NOT EXISTS asset_tracking (
    id UUID PRIMARY KEY,
    asset_id UUID NOT NULL REFERENCES assets(id),
    action VARCHAR(60) NOT NULL,
    "user" VARCHAR(150),
    location VARCHAR(150),
    type VARCHAR(50),
    notes TEXT,
    event_time TIMESTAMP,
    created_at TIMESTAMP DEFAULT NOW()
);

INSERT INTO asset_tracking (id, asset_id, action, "user", location, type, notes, event_time, created_at) VALUES
('b2f00000-0001-4000-8000-000000000001','4774b0a5-c063-4052-a590-69f962423ae4','CHECKED_OUT','jane.smith@company.com','Pune Office - 4th Floor','ASSIGNMENT','Assigned to Jane',NOW(),NOW()),
('b2f00000-0002-4000-8000-000000000002','4774b0a5-c063-4052-a590-69f962423ae4','MOVED','system','Chennai Office','MOVEMENT','Moved to Chennai',NOW(),NOW()),
('b2f00000-0003-4000-8000-000000000003','a1b2c3d4-0001-4a1f-9c1e-000000000001','CHECKED_IN','rahul.sharma@company.com','Chennai Office - Ground Floor','ASSIGNMENT','Checked in',NOW(),NOW()),
('b2f00000-0004-4000-8000-000000000004','a1b2c3d4-0003-4a1f-9c1e-000000000003','LOCATION_CHANGED','system','Service Center - Repair Bay 2','MAINTENANCE','In repair',NOW(),NOW()),
('b2f00000-0005-4000-8000-000000000005','a1b2c3d4-0004-4a1f-9c1e-000000000004','CHECKED_IN',NULL,'Chennai Office - Storage Room','MOVEMENT','Stored in storage',NOW(),NOW())
ON CONFLICT (id) DO NOTHING;

COMMIT;
