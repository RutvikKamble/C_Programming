# ZAT Asset Management Backend

Spring Boot 3.5.2 / Java 17 / PostgreSQL backend for the completed core + Phase II asset-management modules.

## Scope of this build
Completed modules and their supporting workflows are included: Company, Branch, Department, Employee, Vendor, Technician, Asset Master, Asset Categories, Asset Model, Allocation, Return, Tracking, QR/Barcode, Maintenance, Preventive Maintenance scheduling, Warranty, AMC Contracts, persistent maintenance/warranty/AMC alerts, Documents, Requests, Audits, Asset History and Dashboard.

No new Phase III/IV remaining modules are included in this build.

## Run
1. Install Java 17+ and PostgreSQL.
2. Set DB_URL, DB_USERNAME and DB_PASSWORD, or use the local defaults except the password.
3. Run `mvn spring-boot:run` or the Maven wrapper.
4. Default API base: `http://localhost:8080/api`.
5. Hibernate uses `ddl-auto=update` for development.

## Key workflow additions
- Return: REQUESTED -> VERIFIED -> COMPLETED; deallocation is guarded by completed return.
- Allocation: only AVAILABLE assets can be allocated; lifecycle history is recorded.
- Maintenance: scheduled/in-progress maintenance moves assets to IN_REPAIR and completion returns them to AVAILABLE.
- Preventive schedules: recurring due dates and due/overdue queries.
- Alerts: persistent warranty expiry, AMC renewal, preventive-maintenance and overdue-maintenance alerts with scheduled generation.
- AMC contracts: asset/category coverage, vendor, contract value, dates, CRUD and renewal queue.
- Documents: multipart upload and local/self-hosted download.
- Requests: Department Head approval -> Asset Manager approval -> ACCEPT -> fulfillment/allocation.
- QR/barcode and existing CRUD/import/export APIs remain available.
- Software Asset Management: software licenses, encrypted license keys, seat tracking, employee allocations/releases, allocation history and expiry alerts.

## Postman
Import `ZAT_Asset_Management_Completed_Modules_ALL_APIs.postman_collection.json` and the environment file.

## Vehicle Management

Vehicle Management is implemented as a specialization of an existing `Asset` with `assetType = VEHICLE_ASSET`.

### Tables
- `vehicle_details` (one-to-one with `assets`)
- `vehicle_service_logs` (many-to-one with `vehicle_details`)
- `vehicle_fuel_logs` (many-to-one with `vehicle_details`)

Existing Insurance Policy and Vendor modules are reused.

See `VEHICLE_MANAGEMENT_IMPLEMENTATION.md` and `src/main/resources/db/create_vehicle_tables.sql` for details.

## Postman
Import `ZAT_Asset_Management_Completed_Modules_ALL_APIs.postman_collection.json` and the environment file.

## Software Asset Management
Software is managed as an `Asset` with `assetType=SOFTWARE_ASSET`, then extended by one `software_licenses` record. License seats are allocated to existing employees through `license_allocations` and released instead of physically returning the asset.

### Required environment variable
Set `SOFTWARE_LICENSE_ENCRYPTION_KEY` to a 16, 24, or 32 UTF-8 byte secret. License keys are encrypted with AES-GCM before persistence and API responses expose only a masked key.


