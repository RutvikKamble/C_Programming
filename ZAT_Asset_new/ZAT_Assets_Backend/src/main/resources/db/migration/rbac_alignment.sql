-- ZAT RBAC alignment migration. Run once after the existing RBAC phase scripts.
-- This migration preserves legacy user_permissions for rollback/audit, but the application
-- authorization source becomes role_permissions + user_permission_overrides.
ALTER TABLE roles ADD COLUMN IF NOT EXISTS company_id UUID;
ALTER TABLE roles ADD COLUMN IF NOT EXISTS code VARCHAR(80);
ALTER TABLE roles ADD COLUMN IF NOT EXISTS is_system BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE roles ADD COLUMN IF NOT EXISTS is_active BOOLEAN NOT NULL DEFAULT TRUE;
UPDATE roles SET code=UPPER(REGEXP_REPLACE(name,'[^A-Za-z0-9]+','_','g')) WHERE code IS NULL;
UPDATE roles SET is_system=system_defined WHERE is_system=false AND system_defined=true;
UPDATE roles SET is_active=(status='ACTIVE');
ALTER TABLE roles DROP CONSTRAINT IF EXISTS uk_roles_company_code;
CREATE UNIQUE INDEX IF NOT EXISTS uk_roles_company_code ON roles(company_id,code) WHERE company_id IS NOT NULL;
ALTER TABLE roles DROP CONSTRAINT IF EXISTS fk_roles_company;
ALTER TABLE roles ADD CONSTRAINT fk_roles_company FOREIGN KEY(company_id) REFERENCES companies(id) ON DELETE SET NULL;
ALTER TABLE permissions ADD COLUMN IF NOT EXISTS project VARCHAR(80);
ALTER TABLE permissions ADD COLUMN IF NOT EXISTS code VARCHAR(255);
ALTER TABLE permissions ADD COLUMN IF NOT EXISTS is_system BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE permissions ADD COLUMN IF NOT EXISTS is_active BOOLEAN NOT NULL DEFAULT TRUE;
-- Convert legacy three-part lowercase keys to the canonical documented form.
UPDATE permissions SET project='Asset' WHERE project IS NULL;
UPDATE permissions SET project='Asset', module=CASE module
 WHEN 'asset' THEN 'Asset_Master' WHEN 'asset_maintenance' THEN 'Maintenance' WHEN 'asset_warranty' THEN 'Warranty' WHEN 'asset_insurance' THEN 'Insurance' WHEN 'asset_vehicle' THEN 'Vehicle' WHEN 'asset_document' THEN 'Asset_Document' WHEN 'asset_transfer' THEN 'Asset_Transfer' WHEN 'asset_tracking' THEN 'Asset_Tracking' WHEN 'asset_return' THEN 'Asset_Return' WHEN 'asset_request' THEN 'Asset_Request' WHEN 'asset_allocation' THEN 'Asset_Allocation' WHEN 'asset_category' THEN 'Asset_Category' WHEN 'asset_audit' THEN 'Asset_Audit' WHEN 'asset_history' THEN 'Asset_History' WHEN 'audit_run' THEN 'Asset_Audit' WHEN 'barcode' THEN 'QR_Barcode' WHEN 'company' THEN 'Organization' WHEN 'branch' THEN 'Organization' WHEN 'department' THEN 'Organization' WHEN 'employee' THEN 'User' ELSE INITCAP(REPLACE(module,'_','_')) END, action=CASE WHEN module='asset_request' AND action='approve' THEN 'Approve_Manager' WHEN module='asset_return' AND action='verify' THEN 'Return_Verify' ELSE INITCAP(REPLACE(action,'_','_')) END WHERE application='asset_management';
UPDATE permissions SET module=INITCAP(REPLACE(module,'_','_')) WHERE application='asset_management' AND module NOT IN ('Asset_Master','Maintenance','Warranty','Insurance','Vehicle','Asset_Document','Asset_Transfer','Asset_Tracking','Asset_Return','Asset_Request','Asset_Allocation','Asset_Category','Asset_Audit','Asset_History','QR_Barcode','Organization','User');
UPDATE permissions SET action=INITCAP(REPLACE(action,'_','_')) WHERE application='asset_management' AND action IS NOT NULL;
-- If several legacy modules collapse to one documented module/action, keep one permission row and repoint relationships before deleting duplicates.
DELETE FROM role_permissions rp USING permissions p, permissions q WHERE rp.permission_id=p.id AND p.id<>q.id AND p.project=q.project AND p.module=q.module AND p.action=q.action AND p.id>q.id AND EXISTS (SELECT 1 FROM role_permissions keep WHERE keep.role_id=rp.role_id AND keep.permission_id=q.id);
UPDATE role_permissions rp SET permission_id=q.id FROM permissions p JOIN permissions q ON p.project=q.project AND p.module=q.module AND p.action=q.action AND p.id>q.id WHERE rp.permission_id=p.id AND NOT EXISTS (SELECT 1 FROM role_permissions keep WHERE keep.role_id=rp.role_id AND keep.permission_id=q.id);
DELETE FROM user_permissions up USING permissions p, permissions q WHERE up.permission_id=p.id AND p.id<>q.id AND p.project=q.project AND p.module=q.module AND p.action=q.action AND p.id>q.id AND EXISTS (SELECT 1 FROM user_permissions keep WHERE keep.user_id=up.user_id AND keep.permission_id=q.id AND keep.source=up.source);
UPDATE user_permissions up SET permission_id=q.id FROM permissions p JOIN permissions q ON p.project=q.project AND p.module=q.module AND p.action=q.action AND p.id>q.id WHERE up.permission_id=p.id AND NOT EXISTS (SELECT 1 FROM user_permissions keep WHERE keep.user_id=up.user_id AND keep.permission_id=q.id AND keep.source=up.source);
DELETE FROM user_permission_overrides upo USING permissions p, permissions q WHERE upo.permission_id=p.id AND p.id<>q.id AND p.project=q.project AND p.module=q.module AND p.action=q.action AND p.id>q.id AND EXISTS (SELECT 1 FROM user_permission_overrides keep WHERE keep.user_id=upo.user_id AND keep.permission_id=q.id);
UPDATE user_permission_overrides upo SET permission_id=q.id FROM permissions p JOIN permissions q ON p.project=q.project AND p.module=q.module AND p.action=q.action AND p.id>q.id WHERE upo.permission_id=p.id AND NOT EXISTS (SELECT 1 FROM user_permission_overrides keep WHERE keep.user_id=upo.user_id AND keep.permission_id=q.id);
DELETE FROM permissions p USING permissions q WHERE p.id<>q.id AND p.project=q.project AND p.module=q.module AND p.action=q.action AND p.id>q.id;

UPDATE permissions SET code=project||'.'||module||'.'||action WHERE code IS NULL;
UPDATE permissions SET permission_key=code, application=project WHERE code IS NOT NULL;
UPDATE permissions SET is_active=(status='ACTIVE');
ALTER TABLE permissions DROP CONSTRAINT IF EXISTS uk_permissions_code;
ALTER TABLE permissions ADD CONSTRAINT uk_permissions_code UNIQUE(code);
ALTER TABLE role_permissions ADD COLUMN IF NOT EXISTS assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE role_permissions ADD COLUMN IF NOT EXISTS assigned_by UUID;
ALTER TABLE role_permissions DROP CONSTRAINT IF EXISTS fk_role_permissions_assigned_by;
ALTER TABLE role_permissions ADD CONSTRAINT fk_role_permissions_assigned_by FOREIGN KEY(assigned_by) REFERENCES users(id) ON DELETE SET NULL;
CREATE TABLE IF NOT EXISTS user_permission_overrides(id UUID PRIMARY KEY DEFAULT gen_random_uuid(),user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,permission_id UUID NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,effect VARCHAR(10) NOT NULL CHECK(effect IN ('GRANT','DENY')),reason TEXT,created_by UUID REFERENCES users(id) ON DELETE SET NULL,created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,CONSTRAINT uk_user_permission_override_user_permission UNIQUE(user_id,permission_id));
CREATE INDEX IF NOT EXISTS idx_user_permission_overrides_user ON user_permission_overrides(user_id);
CREATE INDEX IF NOT EXISTS idx_user_permission_overrides_permission ON user_permission_overrides(permission_id);
-- Preserve explicit DIRECT user permissions as GRANT overrides.
INSERT INTO user_permission_overrides(user_id,permission_id,effect,reason,created_at,updated_at)
SELECT up.user_id,up.permission_id,'GRANT','Migrated from legacy DIRECT user permission',up.created_at,up.created_at FROM user_permissions up WHERE up.source='DIRECT'
ON CONFLICT(user_id,permission_id) DO NOTHING;
-- ROLE-derived rows are no longer the authorization source.
DROP TRIGGER IF EXISTS trg_sync_user_role_permissions ON users;
DROP TRIGGER IF EXISTS trg_sync_role_permission_insert ON role_permissions;
DROP TRIGGER IF EXISTS trg_sync_role_permission_delete ON role_permissions;
DELETE FROM user_permissions WHERE source='ROLE';
CREATE TABLE IF NOT EXISTS security_audit_log(id UUID PRIMARY KEY DEFAULT gen_random_uuid(),company_id UUID REFERENCES companies(id) ON DELETE SET NULL,actor_user_id UUID REFERENCES users(id) ON DELETE SET NULL,target_user_id UUID REFERENCES users(id) ON DELETE SET NULL,role_id UUID REFERENCES roles(id) ON DELETE SET NULL,permission_id UUID REFERENCES permissions(id) ON DELETE SET NULL,event_type VARCHAR(60) NOT NULL,old_value TEXT,new_value TEXT,created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP);
CREATE INDEX IF NOT EXISTS idx_security_audit_company_time ON security_audit_log(company_id,created_at DESC);
CREATE INDEX IF NOT EXISTS idx_security_audit_target_time ON security_audit_log(target_user_id,created_at DESC);
