-- ZAT Software Asset Management schema.
-- This extends the original software license model with Snipe-IT-like license metadata,
-- while retaining ZAT's Asset -> SoftwareLicense -> LicenseAllocation architecture.

CREATE TABLE IF NOT EXISTS software_licenses (
    id UUID PRIMARY KEY,
    asset_id UUID NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL DEFAULT 'Software License',
    publisher VARCHAR(100) NOT NULL,
    manufacturer VARCHAR(100),
    license_category VARCHAR(100),
    supplier_id UUID,
    license_key VARCHAR(1000) NOT NULL,
    seat_count INTEGER NOT NULL CHECK (seat_count > 0),
    seats_used INTEGER NOT NULL DEFAULT 0 CHECK (seats_used >= 0),
    expiry_date DATE NOT NULL,
    purchase_date DATE,
    purchase_cost NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (purchase_cost >= 0),
    renewal_cost NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (renewal_cost >= 0),
    order_number VARCHAR(100),
    purchase_order VARCHAR(100),
    license_contact_name VARCHAR(150),
    license_contact_email VARCHAR(150),
    notes TEXT,
    reassignable BOOLEAN NOT NULL DEFAULT TRUE,
    maintained BOOLEAN NOT NULL DEFAULT TRUE,
    termination_date DATE,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_software_license_asset FOREIGN KEY (asset_id) REFERENCES assets(id),
    CONSTRAINT fk_software_license_supplier FOREIGN KEY (supplier_id) REFERENCES vendors(id),
    CONSTRAINT chk_software_license_seats CHECK (seats_used <= seat_count),
    CONSTRAINT chk_software_license_status CHECK (status IN ('ACTIVE','EXPIRED','CANCELLED')),
    CONSTRAINT chk_software_license_dates CHECK (termination_date IS NULL OR expiry_date IS NULL OR termination_date >= expiry_date)
);

CREATE TABLE IF NOT EXISTS license_allocations (
    id UUID PRIMARY KEY,
    software_license_id UUID NOT NULL,
    employee_id UUID NOT NULL,
    allocated_date DATE NOT NULL,
    released_date DATE,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP,
    CONSTRAINT fk_license_allocation_license FOREIGN KEY (software_license_id) REFERENCES software_licenses(id),
    CONSTRAINT fk_license_allocation_employee FOREIGN KEY (employee_id) REFERENCES users(id),
    CONSTRAINT chk_license_allocation_status CHECK (status IN ('ACTIVE','RELEASED')),
    CONSTRAINT chk_license_allocation_dates CHECK (released_date IS NULL OR released_date >= allocated_date)
);

-- Safe migration for databases created with the earlier ZAT Software Asset schema.
ALTER TABLE software_licenses ADD COLUMN IF NOT EXISTS name VARCHAR(150);
UPDATE software_licenses SET name = 'Software License' WHERE name IS NULL;
ALTER TABLE software_licenses ALTER COLUMN name SET DEFAULT 'Software License';
ALTER TABLE software_licenses ALTER COLUMN name SET NOT NULL;
ALTER TABLE software_licenses ADD COLUMN IF NOT EXISTS manufacturer VARCHAR(100);
ALTER TABLE software_licenses ADD COLUMN IF NOT EXISTS license_category VARCHAR(100);
ALTER TABLE software_licenses ADD COLUMN IF NOT EXISTS supplier_id UUID;
ALTER TABLE software_licenses ADD COLUMN IF NOT EXISTS purchase_date DATE;
ALTER TABLE software_licenses ADD COLUMN IF NOT EXISTS purchase_cost NUMERIC(12,2) NOT NULL DEFAULT 0;
ALTER TABLE software_licenses ADD COLUMN IF NOT EXISTS order_number VARCHAR(100);
ALTER TABLE software_licenses ADD COLUMN IF NOT EXISTS purchase_order VARCHAR(100);
ALTER TABLE software_licenses ADD COLUMN IF NOT EXISTS license_contact_name VARCHAR(150);
ALTER TABLE software_licenses ADD COLUMN IF NOT EXISTS license_contact_email VARCHAR(150);
ALTER TABLE software_licenses ADD COLUMN IF NOT EXISTS notes TEXT;
ALTER TABLE software_licenses ADD COLUMN IF NOT EXISTS reassignable BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE software_licenses ADD COLUMN IF NOT EXISTS maintained BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE software_licenses ADD COLUMN IF NOT EXISTS termination_date DATE;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_software_license_supplier') THEN
        ALTER TABLE software_licenses ADD CONSTRAINT fk_software_license_supplier FOREIGN KEY (supplier_id) REFERENCES vendors(id);
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_software_license_publisher ON software_licenses(publisher);
CREATE INDEX IF NOT EXISTS idx_software_license_category ON software_licenses(license_category);
CREATE INDEX IF NOT EXISTS idx_software_license_supplier ON software_licenses(supplier_id);
CREATE INDEX IF NOT EXISTS idx_software_license_expiry ON software_licenses(expiry_date);
CREATE INDEX IF NOT EXISTS idx_license_alloc_license ON license_allocations(software_license_id);
CREATE INDEX IF NOT EXISTS idx_license_alloc_employee ON license_allocations(employee_id);

CREATE UNIQUE INDEX IF NOT EXISTS uk_license_active_employee
    ON license_allocations(software_license_id, employee_id)
    WHERE status = 'ACTIVE';
