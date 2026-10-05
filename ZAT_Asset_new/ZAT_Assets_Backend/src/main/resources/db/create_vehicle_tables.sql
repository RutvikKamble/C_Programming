-- ZAT Asset Management - Vehicle Management
-- Vehicle is a specialization of an existing Asset whose asset_type is VEHICLE_ASSET.

CREATE TABLE IF NOT EXISTS vehicle_details (
    id UUID PRIMARY KEY,
    asset_id UUID NOT NULL UNIQUE,
    registration_number VARCHAR(30) NOT NULL UNIQUE,
    insurance_policy_id UUID,
    fuel_type VARCHAR(20) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_vehicle_asset
        FOREIGN KEY (asset_id) REFERENCES assets(id),
    CONSTRAINT fk_vehicle_insurance
        FOREIGN KEY (insurance_policy_id) REFERENCES insurance_policies(id)
);

CREATE INDEX IF NOT EXISTS idx_vehicle_asset ON vehicle_details(asset_id);
CREATE INDEX IF NOT EXISTS idx_vehicle_registration ON vehicle_details(registration_number);

CREATE TABLE IF NOT EXISTS vehicle_service_logs (
    id UUID PRIMARY KEY,
    vehicle_detail_id UUID NOT NULL,
    service_date DATE NOT NULL,
    odometer_reading NUMERIC(14,2) NOT NULL,
    cost NUMERIC(14,2) NOT NULL,
    vendor_id UUID,
    remarks TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_vehicle_service_vehicle
        FOREIGN KEY (vehicle_detail_id) REFERENCES vehicle_details(id),
    CONSTRAINT fk_vehicle_service_vendor
        FOREIGN KEY (vendor_id) REFERENCES vendors(id)
);

CREATE INDEX IF NOT EXISTS idx_vehicle_service_vehicle ON vehicle_service_logs(vehicle_detail_id);
CREATE INDEX IF NOT EXISTS idx_vehicle_service_date ON vehicle_service_logs(service_date);
CREATE INDEX IF NOT EXISTS idx_vehicle_service_odometer ON vehicle_service_logs(odometer_reading);

CREATE TABLE IF NOT EXISTS vehicle_fuel_logs (
    id UUID PRIMARY KEY,
    vehicle_detail_id UUID NOT NULL,
    fuel_date DATE NOT NULL,
    amount NUMERIC(14,2) NOT NULL,
    cost NUMERIC(14,2) NOT NULL,
    remarks TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_vehicle_fuel_vehicle
        FOREIGN KEY (vehicle_detail_id) REFERENCES vehicle_details(id)
);

CREATE INDEX IF NOT EXISTS idx_vehicle_fuel_vehicle ON vehicle_fuel_logs(vehicle_detail_id);
CREATE INDEX IF NOT EXISTS idx_vehicle_fuel_date ON vehicle_fuel_logs(fuel_date);
