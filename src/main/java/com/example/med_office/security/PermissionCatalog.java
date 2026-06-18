package com.example.med_office.security;

import java.util.List;

public final class PermissionCatalog {

    public static final String OVERVIEW_DASHBOARD_VIEW = "overview.dashboard.view";
    public static final String EMPLOYEES_DIRECTORY_VIEW = "employees.directory.view";
    public static final String EMPLOYEES_DIRECTORY_UPDATE = "employees.directory.update";
    public static final String EMPLOYEES_PROFILE_VIEW = "employees.profile.view";
    public static final String EMPLOYEES_ORGANIZATION_VIEW = "employees.organization.view";
    public static final String EMPLOYEES_CONTACT_UPDATE = "employees.contact.update";
    public static final String EMPLOYEES_CONTRACT_EXPIRING_VIEW = "employees.contract.expiring.view";
    public static final String EMPLOYEES_PERSONAL_UPDATE = "employees.personal.update";
    public static final String EMPLOYEES_BANK_UPDATE = "employees.bank.update";
    public static final String EMPLOYEES_REPORT_DEPARTMENT_VIEW = "employees.report.department.view";
    public static final String EMPLOYEES_CREATE = "employees.create";
    public static final String DOCUMENTS_INCOMING_VIEW = "documents.incoming.view";
    public static final String DOCUMENTS_INCOMING_UPDATE = "documents.incoming.update";
    public static final String DOCUMENTS_OUTGOING_VIEW = "documents.outgoing.view";
    public static final String DOCUMENTS_OUTGOING_UPDATE = "documents.outgoing.update";
    public static final String DOCUMENTS_REFERENCE_VIEW = "documents.reference.view";
    public static final String DOCUMENTS_ARCHIVE_SEARCH = "documents.archive.search";
    public static final String SCHEDULES_DUTY_VIEW = "schedules.duty.view";
    public static final String SCHEDULES_DUTY_UPDATE = "schedules.duty.update";
    public static final String SCHEDULES_MEETING_VIEW = "schedules.meeting.view";
    public static final String SCHEDULES_ROOM_BOOK = "schedules.room.book";
    public static final String SCHEDULES_MINUTES_UPDATE = "schedules.minutes.update";
    public static final String MEALS_DOCTOR_VIEW = "meals.doctor.view";
    public static final String MEALS_DOCTOR_UPDATE = "meals.doctor.update";
    public static final String MEALS_PATIENT_VIEW = "meals.patient.view";
    public static final String MEALS_PATIENT_UPDATE = "meals.patient.update";
    public static final String MEALS_WEEKLY_MENU_VIEW = "meals.weekly-menu.view";
    public static final String MEALS_WEEKLY_MENU_UPDATE = "meals.weekly-menu.update";
    public static final String SYSTEM_PERMISSIONS_MANAGE = "system.permissions.manage";
    public static final String SYSTEM_ACCOUNTS_VIEW = "system.accounts.view";
    public static final String SYSTEM_ACCOUNTS_UPDATE = "system.accounts.update";
    public static final String CATALOGS_VIEW = "catalogs.view";
    public static final String WAREHOUSE_VIEW = "warehouse.view";
    public static final String WAREHOUSE_MANAGE = "warehouse.manage";
    public static final String EMPLOYEES_LEAVE_VIEW = "employees.leave.view";
    public static final String EMPLOYEES_LEAVE_MANAGE = "employees.leave.manage";

    private static final List<PermissionDefinition> DEFINITIONS = List.of(
            permission(OVERVIEW_DASHBOARD_VIEW, "overview", "Tong quan", "Xem dashboard"),
            permission(EMPLOYEES_DIRECTORY_VIEW, "human-resources", "Module nhan su", "Xem danh sach nhan su"),
            permission(EMPLOYEES_DIRECTORY_UPDATE, "human-resources", "Module nhan su", "Cap nhat nhan su"),
            permission(EMPLOYEES_PROFILE_VIEW, "human-resources", "Module nhan su", "Xem ho so nhan vien"),
            permission(EMPLOYEES_ORGANIZATION_VIEW, "human-resources", "Module nhan su", "Xem co cau to chuc"),
            permission(EMPLOYEES_CONTACT_UPDATE, "human-resources", "Module nhan su", "Cap nhat lien he nhan vien"),
            permission(EMPLOYEES_CONTRACT_EXPIRING_VIEW, "human-resources", "Module nhan su", "Xem hop dong sap het han"),
            permission(EMPLOYEES_PERSONAL_UPDATE, "human-resources", "Module nhan su", "Cap nhat thong tin ca nhan"),
            permission(EMPLOYEES_BANK_UPDATE, "human-resources", "Module nhan su", "Cap nhat thong tin ngan hang"),
            permission(EMPLOYEES_REPORT_DEPARTMENT_VIEW, "human-resources", "Module nhan su", "Xem bao cao theo phong ban"),
            permission(EMPLOYEES_CREATE, "human-resources", "Module nhan su", "Tao nhan vien"),
            permission(DOCUMENTS_INCOMING_VIEW, "documents", "Module cong van", "Xem cong van den"),
            permission(DOCUMENTS_INCOMING_UPDATE, "documents", "Module cong van", "Cap nhat cong van den"),
            permission(DOCUMENTS_OUTGOING_VIEW, "documents", "Module cong van", "Xem cong van di"),
            permission(DOCUMENTS_OUTGOING_UPDATE, "documents", "Module cong van", "Cap nhat cong van di"),
            permission(DOCUMENTS_REFERENCE_VIEW, "documents", "Module cong van", "Xem van ban tham chieu"),
            permission(DOCUMENTS_ARCHIVE_SEARCH, "documents", "Module cong van", "Tra cuu luu tru"),
            permission(SCHEDULES_DUTY_VIEW, "schedules", "Module lich", "Xem lich truc"),
            permission(SCHEDULES_DUTY_UPDATE, "schedules", "Module lich", "Cap nhat lich truc"),
            permission(SCHEDULES_MEETING_VIEW, "schedules", "Module lich", "Xem lich hop"),
            permission(SCHEDULES_ROOM_BOOK, "schedules", "Module lich", "Dat phong hop"),
            permission(SCHEDULES_MINUTES_UPDATE, "schedules", "Module lich", "Cap nhat bien ban hop"),
            permission(MEALS_DOCTOR_VIEW, "meals", "Module suat an", "Xem suat an bac si"),
            permission(MEALS_DOCTOR_UPDATE, "meals", "Module suat an", "Cap nhat suat an bac si"),
            permission(MEALS_PATIENT_VIEW, "meals", "Module suat an", "Xem suat an benh nhan"),
            permission(MEALS_PATIENT_UPDATE, "meals", "Module suat an", "Cap nhat suat an benh nhan"),
            permission(MEALS_WEEKLY_MENU_VIEW, "meals", "Module suat an", "Xem thuc don tuan"),
            permission(MEALS_WEEKLY_MENU_UPDATE, "meals", "Module suat an", "Cap nhat thuc don tuan"),
            permission(SYSTEM_PERMISSIONS_MANAGE, "system", "He thong", "Quan ly phan quyen"),
            permission(SYSTEM_ACCOUNTS_VIEW, "system", "He thong", "Xem tai khoan"),
            permission(SYSTEM_ACCOUNTS_UPDATE, "system", "He thong", "Cap nhat tai khoan"),
            permission(CATALOGS_VIEW, "catalogs", "Danh muc", "Xem danh muc"),
            permission(WAREHOUSE_VIEW, "warehouse", "Module kho", "Xem kho"),
            permission(WAREHOUSE_MANAGE, "warehouse", "Module kho", "Quan ly kho"),
            permission(EMPLOYEES_LEAVE_VIEW, "human-resources", "Module nhan su", "Xem danh sach nghi phep"),
            permission(EMPLOYEES_LEAVE_MANAGE, "human-resources", "Module nhan su", "Phe duyet nghi phep")
    );

    private PermissionCatalog() {
    }

    public static List<PermissionDefinition> definitions() {
        return DEFINITIONS;
    }

    private static PermissionDefinition permission(String code, String moduleCode, String moduleName, String name) {
        return new PermissionDefinition(code, moduleCode, moduleName, name, name);
    }
}
