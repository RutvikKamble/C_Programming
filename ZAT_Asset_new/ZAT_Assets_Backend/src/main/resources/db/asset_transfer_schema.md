# Asset Transfer schema

Hibernate creates/updates the `asset_transfers` table when `spring.jpa.hibernate.ddl-auto=update` is enabled.

Columns:
- id UUID PK
- asset_id UUID FK -> assets.id
- from_branch_id UUID FK -> branches.id, nullable
- to_branch_id UUID FK -> branches.id, nullable
- from_employee_id UUID FK -> users.id, nullable
- to_employee_id UUID FK -> users.id, nullable
- transfer_date DATE
- remarks TEXT
- created_at TIMESTAMP

At least one destination (`to_branch_id` or `to_employee_id`) is required by the service layer. The source values
are captured from the current Asset rather than trusted from the request.
