package com.example.assets_management.permission.util;

import java.util.regex.Pattern;

/** Canonical ZAT RBAC permission validator: Asset.Module.Action. */
public final class PermissionKeyValidator {
    private static final Pattern PATTERN = Pattern.compile("^Asset\\.[A-Z][A-Za-z0-9_]*\\.[A-Z][A-Za-z0-9_]*$");
    private PermissionKeyValidator() {}
    /**
     * Turns the loosely typed form {@code application.module.action} (e.g. {@code asset.purchaseorder.create})
     * into the canonical stored form by upper-casing the first letter of every segment and of every
     * underscore-separated word ({@code Asset.Purchaseorder.Create}, {@code asset.asset_master.view} ->
     * {@code Asset.Asset_Master.View}). Input that is not three dot-separated segments is returned
     * trimmed so {@link #validate(String)} can reject it.
     */
    public static String normalize(String raw) {
        if (raw == null) {
            return null;
        }
        String[] parts = raw.trim().split("\\.", -1);
        if (parts.length != 3) {
            return raw.trim();
        }
        StringBuilder normalized = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                normalized.append('.');
            }
            normalized.append(capitalizeWords(parts[i]));
        }
        return normalized.toString();
    }

    private static String capitalizeWords(String segment) {
        StringBuilder out = new StringBuilder(segment.length());
        boolean startOfWord = true;
        for (char c : segment.toCharArray()) {
            out.append(startOfWord ? Character.toUpperCase(c) : c);
            startOfWord = c == '_';
        }
        return out.toString();
    }

    public static boolean isValid(String key) { return key != null && PATTERN.matcher(key).matches(); }
    public static void validate(String key) {
        if (!isValid(key)) throw new IllegalArgumentException("Invalid permission key. Expected format: Project.Module.Action");
    }
    public static String project(String key) { validate(key); return key.substring(0,key.indexOf('.')); }
    public static String application(String key) { return project(key); }
    public static String module(String key) { validate(key); int a=key.indexOf('.'), b=key.indexOf('.',a+1); return key.substring(a+1,b); }
    public static String action(String key) { validate(key); return key.substring(key.indexOf('.',key.indexOf('.')+1)+1); }
}
