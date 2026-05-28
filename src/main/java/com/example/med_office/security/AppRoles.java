package com.example.med_office.security;

import java.util.Locale;

public final class AppRoles {

    public static final String USER = "USER";
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
}
