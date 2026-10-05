package com.example.assets_management.security.rbac.seed;

import com.example.assets_management.role.enums.SystemRole;

import java.util.*;

/**
 * Seed data for the ZAT Asset Management RBAC model.
 *
 * <p>Permission key format is {@code Application.Module.Action}, e.g.
 * {@code Asset.Asset_Master.Create} (same shape as the CRM's {@code crm.Leads.View}).
 *
 * <p>GENERATED from the permission catalog. {@link #CATALOG} lists every permission the
 * backend checks with {@code @PreAuthorize}; {@link #defaultRolePermissions()} lists what each
 * built-in role receives the first time it is seeded. After the first seed an administrator can
 * freely add / remove permissions on a built-in role - the seeder never re-adds removed ones.
 */
public final class RbacDefaults {

    public static final String APPLICATION = "Asset";

    /** module -> actions (insertion ordered). */
    public static final Map<String, List<String>> CATALOG = new LinkedHashMap<>();

    static {
        module("AMC", "Create", "Delete", "Manage", "Update", "View");
        module("Alert", "Acknowledge", "Generate", "View");
        module("Asset_Allocation", "Acknowledge", "Allocate", "Create", "Deallocate", "Delete", "Update", "View");
        module("Asset_Audit", "Close", "Complete", "Create", "Delete", "Reconcile", "Run", "Scan", "Update", "View");
        module("Asset_Category", "Create", "Delete", "Export", "Import", "Update", "View");
        module("Asset_Document", "Delete", "Download", "Upload", "View");
        module("Asset_History", "View");
        module("Asset_Master", "Create", "Delete", "Export", "Import", "Update", "View");
        module("Asset_Model", "Create", "Delete", "Update", "View");
        module("Asset_Request", "Approve_IT", "Approve_Manager", "Cancel", "Complete", "Create", "Create_Asset", "Delete", "Fulfill", "Pay_Bill", "Receive", "Reject", "Send", "Transfer", "Update", "View");
        module("Asset_Return", "Complete", "Create", "Delete", "Return_Request", "Return_Verify", "Update", "View");
        module("Asset_Tracking", "Create", "Delete", "View", "View_Branch", "View_Company", "View_Department", "View_Own");
        module("Asset_Transfer", "Create", "Delete", "Transfer", "View");
        module("Banking", "Manage", "View");
        module("Branch_Asset", "View");
        module("Dashboard", "View");
        module("Department_Asset", "View");
        module("Depreciation", "Calculate", "Manage", "View");
        module("Disposal", "Approve", "Complete", "Initiate", "View");
        module("Insurance", "Create", "Delete", "Manage", "Update", "View");
        module("Maintenance", "Cancel", "Complete", "Create", "Delete", "Schedule", "Update", "View");
        module("Organization", "Create", "Delete", "Manage", "Update", "View");
        module("Permission", "Manage", "View");
        module("Preventive_Maintenance", "Cancel", "Complete", "Create", "Delete", "Schedule", "Update", "View");
        module("QR_Barcode", "Download", "Generate", "Print", "Scan", "View");
        module("Report", "Export", "View_Branch", "View_Company", "View_Department", "View_Own");
        module("Role", "Assign_Permissions", "Create", "Deactivate", "Update", "View");
        module("Security_Audit", "View");
        module("Software_Asset", "Create", "Delete", "Manage", "Update", "View");
        module("Technician", "Create", "Delete", "Update", "View");
        module("User", "Assign_Role", "Create", "Deactivate", "Delete", "Manage_Permissions", "Update", "View");
        module("Vehicle", "Create", "Delete", "Manage", "Update", "View");
        module("Vendor", "Create", "Delete", "Update", "View");
        module("Warranty", "Create", "Delete", "Manage", "Update", "View");
    }

    private RbacDefaults() {}

    private static void module(String module, String... actions) {
        CATALOG.put(module, List.of(actions));
    }

    public static String key(String module, String action) {
        return APPLICATION + "." + module + "." + action;
    }

    public static String describe(String module, String action) {
        return APPLICATION + " " + module + " " + action + " permission";
    }

    /** Every permission key in the catalog. */
    public static List<String> allKeys() {
        List<String> keys = new ArrayList<>();
        CATALOG.forEach((module, actions) -> actions.forEach(action -> keys.add(key(module, action))));
        return keys;
    }

    /**
     * Default permission keys per built-in role. SUPER_ADMIN and ADMIN receive the whole catalog;
     * the other roles receive a least-privilege subset.
     */
    public static Map<SystemRole, Set<String>> defaultRolePermissions() {
        Map<SystemRole, Set<String>> map = new EnumMap<>(SystemRole.class);
        map.put(SystemRole.SUPER_ADMIN, new LinkedHashSet<>(allKeys()));
        map.put(SystemRole.ADMIN, new LinkedHashSet<>(allKeys()));

        Set<String> assetManager = new LinkedHashSet<>();
        grant(assetManager, "Asset_Master", "View", "Create", "Update", "Import", "Export");
        grant(assetManager, "Asset_Category", "View", "Create", "Update", "Import", "Export");
        grant(assetManager, "Asset_Model", "View", "Create", "Update");
        grant(assetManager, "Asset_Allocation", "View", "Create", "Update", "Allocate", "Deallocate", "Acknowledge");
        grant(assetManager, "Asset_Return", "View", "Create", "Update", "Return_Request", "Return_Verify", "Complete");
        grant(assetManager, "Asset_Transfer", "View", "Create", "Transfer");
        grant(assetManager, "Asset_Tracking", "View", "Create", "View_Own", "View_Department", "View_Branch", "View_Company");
        grant(assetManager, "Asset_Document", "View", "Upload", "Download", "Delete");
        grant(assetManager, "Asset_History", "View");
        grant(assetManager, "Asset_Audit", "View", "Create", "Update", "Run", "Scan", "Complete", "Reconcile", "Close");
        grant(assetManager, "Asset_Request", "View", "Create", "Update", "Cancel", "Send", "Receive", "Approve_IT", "Fulfill", "Complete", "Reject", "Create_Asset", "Transfer");
        grant(assetManager, "Maintenance", "View", "Create", "Update", "Schedule", "Complete", "Cancel");
        grant(assetManager, "Preventive_Maintenance", "View", "Create", "Update", "Schedule", "Complete", "Cancel");
        grant(assetManager, "Warranty", "View", "Create", "Update", "Manage");
        grant(assetManager, "AMC", "View", "Create", "Update", "Manage");
        grant(assetManager, "Insurance", "View", "Create", "Update", "Manage");
        grant(assetManager, "Software_Asset", "View", "Create", "Update", "Manage");
        grant(assetManager, "Vehicle", "View", "Create", "Update", "Manage");
        grant(assetManager, "Technician", "View", "Create", "Update");
        grant(assetManager, "Vendor", "View", "Create", "Update");
        grant(assetManager, "QR_Barcode", "View", "Generate", "Scan", "Print", "Download");
        grant(assetManager, "Depreciation", "View", "Calculate");
        grant(assetManager, "Disposal", "View", "Initiate", "Complete");
        grant(assetManager, "Alert", "View", "Acknowledge", "Generate");
        grant(assetManager, "Dashboard", "View");
        grant(assetManager, "Branch_Asset", "View");
        grant(assetManager, "Department_Asset", "View");
        grant(assetManager, "Report", "View_Company", "View_Branch", "View_Department", "View_Own", "Export");
        grant(assetManager, "User", "View");
        grant(assetManager, "Organization", "View");
        map.put(SystemRole.ASSET_MANAGER, assetManager);

        Set<String> departmentHead = new LinkedHashSet<>();
        grant(departmentHead, "Asset_Master", "View");
        grant(departmentHead, "Asset_Category", "View");
        grant(departmentHead, "Asset_Model", "View");
        grant(departmentHead, "Asset_Allocation", "View", "Acknowledge");
        grant(departmentHead, "Asset_Return", "View", "Return_Request");
        grant(departmentHead, "Asset_Transfer", "View");
        grant(departmentHead, "Asset_Tracking", "View", "View_Own", "View_Department");
        grant(departmentHead, "Asset_History", "View");
        grant(departmentHead, "Asset_Request", "View", "Create", "Cancel", "Approve_Manager", "Reject");
        grant(departmentHead, "QR_Barcode", "View", "Scan");
        grant(departmentHead, "Maintenance", "View");
        grant(departmentHead, "Warranty", "View");
        grant(departmentHead, "Dashboard", "View");
        grant(departmentHead, "Department_Asset", "View");
        grant(departmentHead, "Alert", "View");
        grant(departmentHead, "Report", "View_Department", "View_Own");
        grant(departmentHead, "User", "View");
        grant(departmentHead, "Organization", "View");
        map.put(SystemRole.DEPARTMENT_HEAD, departmentHead);

        Set<String> employee = new LinkedHashSet<>();
        grant(employee, "Asset_Allocation", "View", "Acknowledge");
        grant(employee, "Asset_Return", "View", "Return_Request");
        grant(employee, "Asset_Tracking", "View", "View_Own");
        grant(employee, "Asset_History", "View");
        grant(employee, "Asset_Request", "View", "Create", "Cancel");
        grant(employee, "QR_Barcode", "View", "Scan");
        grant(employee, "Dashboard", "View");
        grant(employee, "Report", "View_Own");
        map.put(SystemRole.EMPLOYEE, employee);
        return map;
    }

    private static void grant(Set<String> target, String module, String... actions) {
        for (String action : actions) {
            target.add(key(module, action));
        }
    }
}
