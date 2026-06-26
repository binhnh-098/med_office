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
    public static final String EMPLOYEES_CONTRACT_MANAGE = "employees.contract.manage";
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
    public static final String WAREHOUSE_INBOUND_VIEW = "warehouse.inbound.view";
    public static final String WAREHOUSE_INBOUND_CREATE = "warehouse.inbound.create";
    public static final String WAREHOUSE_INBOUND_UPDATE = "warehouse.inbound.update";
    public static final String WAREHOUSE_INBOUND_SUBMIT = "warehouse.inbound.submit";
    public static final String WAREHOUSE_INBOUND_APPROVE = "warehouse.inbound.approve";
    public static final String WAREHOUSE_INBOUND_REJECT = "warehouse.inbound.reject";
    public static final String WAREHOUSE_INBOUND_COMPLETE = "warehouse.inbound.complete";
    public static final String WAREHOUSE_OUTBOUND_VIEW = "warehouse.outbound.view";
    public static final String WAREHOUSE_OUTBOUND_CREATE = "warehouse.outbound.create";
    public static final String WAREHOUSE_OUTBOUND_UPDATE = "warehouse.outbound.update";
    public static final String WAREHOUSE_OUTBOUND_SUBMIT = "warehouse.outbound.submit";
    public static final String WAREHOUSE_OUTBOUND_APPROVE = "warehouse.outbound.approve";
    public static final String WAREHOUSE_OUTBOUND_REJECT = "warehouse.outbound.reject";
    public static final String WAREHOUSE_OUTBOUND_COMPLETE = "warehouse.outbound.complete";
    public static final String WAREHOUSE_INVENTORY_VIEW = "warehouse.inventory.view";
    public static final String EMPLOYEES_LEAVE_VIEW = "employees.leave.view";
    public static final String EMPLOYEES_LEAVE_MANAGE = "employees.leave.manage";
    public static final String EMPLOYEES_BUSINESS_TRIP_VIEW = "employees.business-trip.view";
    public static final String EMPLOYEES_BUSINESS_TRIP_MANAGE = "employees.business-trip.manage";
    public static final String CATALOGS_ASSET_VIEW = "catalogs.asset.view";
    public static final String CATALOGS_ASSET_MANAGE = "catalogs.asset.manage";

    private static final List<PermissionDefinition> DEFINITIONS = List.of(
            permission(OVERVIEW_DASHBOARD_VIEW, "overview", "Tổng quan", "Xem báo cáo ở tổng quan"),
            permission(EMPLOYEES_DIRECTORY_VIEW, "human-resources", "Nhân sự", "Xem danh sách nhân sự"),
            permission(EMPLOYEES_DIRECTORY_UPDATE, "human-resources", "Nhân sự", "Cập nhật nhân sự"),
            permission(EMPLOYEES_PROFILE_VIEW, "human-resources", "Nhân sự", "Xem hồ sơ nhân sự"),
            permission(EMPLOYEES_ORGANIZATION_VIEW, "human-resources", "Nhân sự", "Xem sơ đồ tổ chức"),
            permission(EMPLOYEES_CONTACT_UPDATE, "human-resources", "Nhân sự", "Cập nhật thông tin liên hệ"),
            permission(EMPLOYEES_CONTRACT_EXPIRING_VIEW, "human-resources", "Nhân sự", "Xem danh sách nhân sự gần hết hạn hợp đồng"),
            permission(EMPLOYEES_CONTRACT_MANAGE, "human-resources", "Nhân sự", "Quản lý hợp đồng lao động"),
            permission(EMPLOYEES_PERSONAL_UPDATE, "human-resources", "Nhân sự", "Cập nhật thông tin cá nhân"),
            permission(EMPLOYEES_BANK_UPDATE, "human-resources", "Nhân sự", "Cập nhật thông tin ngân hàng"),
            permission(EMPLOYEES_REPORT_DEPARTMENT_VIEW, "human-resources", "Nhân sự", "Xem báo cáo nhân sự theo phòng"),
            permission(EMPLOYEES_CREATE, "human-resources", "Nhân sự", "Tạo mới nhân sự"),
            permission(DOCUMENTS_INCOMING_VIEW, "documents", "Công văn, văn bản", "Xem công văn đến"),
            permission(DOCUMENTS_INCOMING_UPDATE, "documents", "Công văn, văn bản", "Cập nhật công văn đến"),
            permission(DOCUMENTS_OUTGOING_VIEW, "documents", "Công văn, văn bản", "Xem công văn đi"),
            permission(DOCUMENTS_OUTGOING_UPDATE, "documents", "Công văn, văn bản", "Cập nhật công văn đi"),
            permission(DOCUMENTS_REFERENCE_VIEW, "documents", "Công văn, văn bản", "Xem văn bản tham khảo"),
            permission(DOCUMENTS_ARCHIVE_SEARCH, "documents", "Công văn, văn bản", "Tra cứu hồ sơ lưu trữ"),
            permission(SCHEDULES_DUTY_VIEW, "schedules", "Lịch họp, điều phối trực", "Xem lịch trực"),
            permission(SCHEDULES_DUTY_UPDATE, "schedules", "Lịch họp, điều phối trực", "Cập nhật lịch trực"),
            permission(SCHEDULES_MEETING_VIEW, "schedules", "Lịch họp, điều phối trực", "Xem lịch họp"),
            permission(SCHEDULES_ROOM_BOOK, "schedules", "Lịch họp, điều phối trực", "Đặt phòng họp"),
            permission(SCHEDULES_MINUTES_UPDATE, "schedules", "Lịch họp, điều phối trực", "Cập nhật biên bản cuộc họp"),
            permission(MEALS_DOCTOR_VIEW, "meals", "Quản lý suất ăn", "Xem suất ăn bác sĩ"),
            permission(MEALS_DOCTOR_UPDATE, "meals", "Quản lý suất ăn", "Cập nhật suất ăn bác sĩ"),
            permission(MEALS_PATIENT_VIEW, "meals", "Quản lý suất ăn", "Xem suất ăn bệnh nhân"),
            permission(MEALS_PATIENT_UPDATE, "meals", "Quản lý suất ăn", "Cập nhật suất ăn bệnh nhân"),
            permission(MEALS_WEEKLY_MENU_VIEW, "meals", "Quản lý suất ăn", "Xem thực đơn tuần"),
            permission(MEALS_WEEKLY_MENU_UPDATE, "meals", "Quản lý suất ăn", "Cập nhật thực đơn tuần"),
            permission(SYSTEM_PERMISSIONS_MANAGE, "system", "Quản trị hệ thống", "Cấu hình phân quyền"),
            permission(SYSTEM_ACCOUNTS_VIEW, "system", "Quản trị hệ thống", "Xem danh sách tài khoản"),
            permission(SYSTEM_ACCOUNTS_UPDATE, "system", "Quản trị hệ thống", "Cập nhật tài khoản"),
            permission(CATALOGS_VIEW, "catalogs", "Danh mục", "Xem danh sách danh mục"),
            permission(WAREHOUSE_VIEW, "warehouse", "Kho", "Xem module kho"),
            permission(WAREHOUSE_MANAGE, "warehouse", "Kho", "Quản trị kho"),
            permission(WAREHOUSE_INBOUND_VIEW, "warehouse", "Kho", "Nhập kho: xem danh sách phiếu"),
            permission(WAREHOUSE_INBOUND_CREATE, "warehouse", "Kho", "Nhập kho: tạo mới phiếu"),
            permission(WAREHOUSE_INBOUND_UPDATE, "warehouse", "Kho", "Nhập kho: cập nhật phiếu"),
            permission(WAREHOUSE_INBOUND_SUBMIT, "warehouse", "Kho", "Nhập kho: gửi duyệt phiếu"),
            permission(WAREHOUSE_INBOUND_APPROVE, "warehouse", "Kho", "Nhập kho: duyệt phiếu"),
            permission(WAREHOUSE_INBOUND_REJECT, "warehouse", "Kho", "Nhập kho: từ chối phiếu"),
            permission(WAREHOUSE_INBOUND_COMPLETE, "warehouse", "Kho", "Nhập kho: hoàn tất"),
            permission(WAREHOUSE_OUTBOUND_VIEW, "warehouse", "Kho", "Xuất kho: xem danh sách phiếu"),
            permission(WAREHOUSE_OUTBOUND_CREATE, "warehouse", "Kho", "Xuất kho: tạo mới phiếu"),
            permission(WAREHOUSE_OUTBOUND_UPDATE, "warehouse", "Kho", "Xuất kho: cập nhật phiếu"),
            permission(WAREHOUSE_OUTBOUND_SUBMIT, "warehouse", "Kho", "Xuất kho: gửi duyệt phiếu"),
            permission(WAREHOUSE_OUTBOUND_APPROVE, "warehouse", "Kho", "Xuất kho: duyệt phiếu"),
            permission(WAREHOUSE_OUTBOUND_REJECT, "warehouse", "Kho", "Xuất kho: từ chối phiếu"),
            permission(WAREHOUSE_OUTBOUND_COMPLETE, "warehouse", "Kho", "Xuất kho: hoàn tất"),
            permission(WAREHOUSE_INVENTORY_VIEW, "warehouse", "Kho", "Tồn kho: xem danh sách"),
            permission(EMPLOYEES_LEAVE_VIEW, "human-resources", "Nhân sự", "Xem nghỉ phép"),
            permission(EMPLOYEES_LEAVE_MANAGE, "human-resources", "Nhân sự", "Quản lý nghỉ phép"),
            permission(EMPLOYEES_BUSINESS_TRIP_VIEW, "human-resources", "Nhân sự", "Xem thông tin công tác"),
            permission(EMPLOYEES_BUSINESS_TRIP_MANAGE, "human-resources", "Nhân sự", "Quản lý và đề xuất công tác"),
            permission(CATALOGS_ASSET_VIEW, "catalogs", "Danh mục", "Xem danh mục tài sản"),
            permission(CATALOGS_ASSET_MANAGE, "catalogs", "Danh mục", "Quản lý danh mục tài sản")
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
