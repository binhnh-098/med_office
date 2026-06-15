package com.example.med_office.controller;

import com.example.med_office.repository.HoSoNhanVienRepository;
import com.example.med_office.repository.NguoiDungRepository;
import com.example.med_office.security.PermissionCatalog;
import com.example.med_office.service.AccessControlService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminPermissionController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminPermissionControllerTest {

    private static final String ROLE_ID = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccessControlService accessControlService;

    @MockitoBean
    private NguoiDungRepository nguoiDungRepository;

    @MockitoBean
    private HoSoNhanVienRepository hoSoNhanVienRepository;

    @Test
    void updateRolePermissionsAcceptsPermissionCodesWrapper() throws Exception {
        mockMvc.perform(put("/api/admin/roles/{roleId}/permissions", ROLE_ID)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "permissionCodes": ["catalogs.view", "warehouse.view"]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Cap nhat phan quyen thanh cong"));

        verify(accessControlService).updateRolePermissions(
                eq(ROLE_ID),
                argThat(request -> request.permissionCodes().equals(List.of("catalogs.view", "warehouse.view")))
        );
    }

    @Test
    void updateRolePermissionsAcceptsRawArrayPayload() throws Exception {
        mockMvc.perform(put("/api/admin/roles/{roleId}/permissions", ROLE_ID)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                ["catalogs.view", "warehouse.view"]
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(accessControlService).updateRolePermissions(
                eq(ROLE_ID),
                argThat(request -> request.permissionCodes().equals(List.of("catalogs.view", "warehouse.view")))
        );
    }

    @Test
    void updateRolePermissionsAcceptsPermissionsArrayOfObjects() throws Exception {
        mockMvc.perform(put("/api/admin/roles/{roleId}/permissions", ROLE_ID)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "permissions": [
                                    {"code": "catalogs.view"},
                                    {"permissionCode": "warehouse.view"},
                                    {"value": "system.accounts.view"}
                                  ]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(accessControlService).updateRolePermissions(
                eq(ROLE_ID),
                argThat(request -> request.permissionCodes().equals(List.of(
                        "catalogs.view",
                        "warehouse.view",
                        "system.accounts.view"
                )))
        );
    }

    @Test
    void updateRolePermissionsAcceptsManyPermissionsAcrossModules() throws Exception {
        mockMvc.perform(put("/api/admin/roles/{roleId}/permissions", ROLE_ID)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "permissionCodes": [
                                    "overview.dashboard.view",
                                    "employees.directory.view",
                                    "employees.directory.update",
                                    "employees.profile.view",
                                    "documents.incoming.view",
                                    "documents.outgoing.view",
                                    "schedules.duty.view",
                                    "meals.doctor.view",
                                    "meals.patient.view",
                                    "system.permissions.manage",
                                    "system.accounts.view",
                                    "catalogs.view",
                                    "warehouse.view",
                                    "warehouse.manage"
                                  ]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(accessControlService).updateRolePermissions(
                eq(ROLE_ID),
                argThat(request -> request.permissionCodes().containsAll(List.of(
                        PermissionCatalog.OVERVIEW_DASHBOARD_VIEW,
                        PermissionCatalog.EMPLOYEES_DIRECTORY_VIEW,
                        PermissionCatalog.EMPLOYEES_DIRECTORY_UPDATE,
                        PermissionCatalog.EMPLOYEES_PROFILE_VIEW,
                        PermissionCatalog.DOCUMENTS_INCOMING_VIEW,
                        PermissionCatalog.DOCUMENTS_OUTGOING_VIEW,
                        PermissionCatalog.SCHEDULES_DUTY_VIEW,
                        PermissionCatalog.MEALS_DOCTOR_VIEW,
                        PermissionCatalog.MEALS_PATIENT_VIEW,
                        PermissionCatalog.SYSTEM_PERMISSIONS_MANAGE,
                        PermissionCatalog.SYSTEM_ACCOUNTS_VIEW,
                        PermissionCatalog.CATALOGS_VIEW,
                        PermissionCatalog.WAREHOUSE_VIEW,
                        PermissionCatalog.WAREHOUSE_MANAGE
                )) && request.permissionCodes().size() == 14)
        );
    }

    @Test
    void updateRolePermissionsRejectsMissingPermissionCodesField() throws Exception {
        mockMvc.perform(put("/api/admin/roles/{roleId}/permissions", ROLE_ID)
                        .contentType(APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.data.permissionCodes").value("permissionCodes is required"));
    }
}
