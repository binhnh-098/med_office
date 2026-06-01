package com.example.med_office.security;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class AppPermissions {

    public static final String[] ADMIN_ROLES = {
            AppRoles.GIAM_DOC
    };

    public static final String[] STAFF_ROLES = {
            AppRoles.GIAM_DOC,
            AppRoles.TRUONG_KHOA,
            AppRoles.BAC_SI,
            AppRoles.DIEU_DUONG,
            AppRoles.LE_TAN
    };

    public static final String[] PROFILE_READ_ROLES = STAFF_ROLES;

    public static final String[] PROFILE_WRITE_ROLES = {
            AppRoles.GIAM_DOC,
            AppRoles.TRUONG_KHOA,
            AppRoles.LE_TAN
    };

    public static final String[] SPECIALTY_WRITE_ROLES = {
            AppRoles.GIAM_DOC,
            AppRoles.TRUONG_KHOA
    };

    public static final String[] DOCUMENT_ROLES = {
            AppRoles.GIAM_DOC,
            AppRoles.TRUONG_KHOA,
            AppRoles.LE_TAN
    };

    public static final String[] MEAL_ROLES = STAFF_ROLES;

    public static final String[] MEAL_MENU_WRITE_ROLES = {
            AppRoles.GIAM_DOC,
            AppRoles.LE_TAN
    };

    public static final String[] ROWBOAT_ROLES = {
            AppRoles.GIAM_DOC,
            AppRoles.TRUONG_KHOA,
            AppRoles.BAC_SI,
            AppRoles.LE_TAN
    };

    private AppPermissions() {
    }

    public static List<String> modulesForRole(String role) {
        LinkedHashSet<String> modules = new LinkedHashSet<>();
        String normalizedRole = AppRoles.normalizeRoleCode(role);

        if (AppRoles.ADMIN.equals(normalizedRole) || AppRoles.GIAM_DOC.equals(normalizedRole)) {
            modules.addAll(List.of(
                    AppModules.DASHBOARD,
                    AppModules.HO_SO_NHAN_VIEN,
                    AppModules.CHUYEN_KHOA,
                    AppModules.CHUC_VU,
                    AppModules.NGUOI_DUNG,
                    AppModules.CONG_VAN,
                    AppModules.DOCTOR_MEALS,
                    AppModules.ROWBOAT
            ));
            return List.copyOf(modules);
        }

        if (AppRoles.NHAN_SU.equals(normalizedRole) || AppRoles.TRUONG_KHOA.equals(normalizedRole)) {
            modules.addAll(List.of(
                    AppModules.DASHBOARD,
                    AppModules.HO_SO_NHAN_VIEN,
                    AppModules.CHUYEN_KHOA,
                    AppModules.CONG_VAN,
                    AppModules.DOCTOR_MEALS,
                    AppModules.ROWBOAT
            ));
            return List.copyOf(modules);
        }

        if (AppRoles.BAC_SI.equals(normalizedRole)) {
            modules.addAll(List.of(
                    AppModules.DASHBOARD,
                    AppModules.HO_SO_NHAN_VIEN,
                    AppModules.CHUYEN_KHOA,
                    AppModules.DOCTOR_MEALS,
                    AppModules.ROWBOAT
            ));
            return List.copyOf(modules);
        }

        if (AppRoles.DIEU_DUONG.equals(normalizedRole)) {
            modules.addAll(List.of(
                    AppModules.DASHBOARD,
                    AppModules.HO_SO_NHAN_VIEN,
                    AppModules.CHUYEN_KHOA,
                    AppModules.DOCTOR_MEALS
            ));
            return List.copyOf(modules);
        }

        if (AppRoles.VAN_THU.equals(normalizedRole) || AppRoles.LE_TAN.equals(normalizedRole)) {
            modules.addAll(List.of(
                    AppModules.DASHBOARD,
                    AppModules.CONG_VAN
            ));
            return List.copyOf(modules);
        }

        if (AppRoles.DINH_DUONG.equals(normalizedRole)) {
            modules.addAll(List.of(
                    AppModules.DASHBOARD,
                    AppModules.DOCTOR_MEALS
            ));
            return List.copyOf(modules);
        }

        return List.of(AppModules.DASHBOARD);
    }

    public static List<String> modulesForRoles(List<String> roles) {
        if (roles == null || roles.isEmpty()) {
            return modulesForRole(AppRoles.USER);
        }

        Set<String> modules = new LinkedHashSet<>();
        roles.forEach(role -> modules.addAll(modulesForRole(role)));
        return List.copyOf(modules);
    }
}
