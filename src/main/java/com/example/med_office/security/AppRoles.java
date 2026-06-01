package com.example.med_office.security;

import java.util.Locale;

public final class AppRoles {

    public static final String USER = "USER";
    public static final String ADMIN = "ADMIN";
    public static final String NHAN_SU = "NHAN_SU";
    public static final String VAN_THU = "VAN_THU";
    public static final String DINH_DUONG = "DINH_DUONG";
    public static final String GIAM_DOC = "GIAM_DOC";
    public static final String TRUONG_KHOA = "TRUONG_KHOA";
    public static final String BAC_SI = "BAC_SI";
    public static final String DIEU_DUONG = "DIEU_DUONG";
    public static final String LE_TAN = "LE_TAN";

    private AppRoles() {
    }

    public static String normalizeRoleCode(String roleCode) {
        if (roleCode == null || roleCode.isBlank()) {
            return USER;
        }
        return roleCode.trim()
                .toUpperCase(Locale.ROOT)
                .replace('-', '_')
                .replace(' ', '_');
    }

    public static String toPermissionRoleCode(String legacyRoleCode) {
        String normalized = normalizeRoleCode(legacyRoleCode);
        return switch (normalized) {
            case GIAM_DOC -> ADMIN;
            case TRUONG_KHOA -> NHAN_SU;
            case LE_TAN -> VAN_THU;
            default -> normalized;
        };
    }
}
