package com.example.med_office.controller;

import com.example.med_office.dto.ApiResponse;
import com.example.med_office.dto.VehicleRequestDTOs.*;
import com.example.med_office.repository.HoSoNhanVienRepository;
import com.example.med_office.repository.NguoiDungRepository;
import com.example.med_office.service.VehicleRequestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = VehicleRequestController.class)
@AutoConfigureMockMvc(addFilters = false)
class VehicleRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VehicleRequestService vehicleRequestService;

    @MockitoBean
    private NguoiDungRepository nguoiDungRepository;

    @MockitoBean
    private HoSoNhanVienRepository hoSoNhanVienRepository;

    private final UsernamePasswordAuthenticationToken principal =
            new UsernamePasswordAuthenticationToken("testuser", "password");

    @Test
    void getMyRequestsReturnsPage() throws Exception {
        Page<VehicleRequestResponse> page = new PageImpl<>(List.of());
        when(vehicleRequestService.getMyRequests(eq("keyword"), eq("DRAFT"), eq("testuser"), eq(0), eq(10)))
                .thenReturn(page);

        mockMvc.perform(get("/api/vehicle-requests")
                        .principal(principal)
                        .param("keyword", "keyword")
                        .param("status", "DRAFT")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Lay danh sach de xuat xe thanh cong"));
    }

    @Test
    void getPendingApprovalsReturnsPage() throws Exception {
        Page<VehicleRequestResponse> page = new PageImpl<>(List.of());
        when(vehicleRequestService.getPendingApprovals(eq("keyword"), eq("testuser"), eq(0), eq(10)))
                .thenReturn(page);

        mockMvc.perform(get("/api/vehicle-requests/approvals")
                        .principal(principal)
                        .param("keyword", "keyword")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Lay danh sach de xuat xe cho duyet thanh cong"));
    }

    @Test
    void getRequestDetailReturnsResponse() throws Exception {
        VehicleRequestResponse response = new VehicleRequestResponse(
                "req-id", "emp-id", null, null, "Employee Name", "EMP001", "IT",
                "Xe 4 chỗ", LocalDateTime.now(), LocalDateTime.now().plusHours(2),
                "Hanoi - Haiphong", 2, "Meeting", "DRAFT", "mgr-id", "Manager Name",
                null, null, null, null, LocalDateTime.now(), LocalDateTime.now()
        );
        when(vehicleRequestService.getRequestDetail(eq("req-id"), eq("testuser")))
                .thenReturn(response);

        mockMvc.perform(get("/api/vehicle-requests/{id}", "req-id")
                        .principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value("req-id"));
    }

    @Test
    void createRequestSavesRequest() throws Exception {
        VehicleRequestResponse response = new VehicleRequestResponse(
                "req-id", "emp-id", null, null, "Employee Name", "EMP001", "IT",
                "Xe 4 chỗ", LocalDateTime.of(2026, 6, 19, 10, 0), LocalDateTime.of(2026, 6, 19, 12, 0),
                "Hanoi - Haiphong", 2, "Meeting", "DRAFT", "mgr-id", "Manager Name",
                null, null, null, null, LocalDateTime.now(), LocalDateTime.now()
        );
        when(vehicleRequestService.createRequest(any(VehicleRequestUpsertRequest.class), eq("testuser")))
                .thenReturn(response);

        mockMvc.perform(post("/api/vehicle-requests")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "vehicleType": "Xe 4 chỗ",
                                  "departureTime": "2026-06-19T10:00:00",
                                  "returnTime": "2026-06-19T12:00:00",
                                  "routeDescription": "Hanoi - Haiphong",
                                  "passengerCount": 2,
                                  "purpose": "Meeting",
                                  "approverId": "mgr-id"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Tao de xuat xe thanh cong"));
    }

    @Test
    void updateRequestSavesRequest() throws Exception {
        VehicleRequestResponse response = new VehicleRequestResponse(
                "req-id", "emp-id", null, null, "Employee Name", "EMP001", "IT",
                "Xe 4 chỗ", LocalDateTime.of(2026, 6, 19, 10, 0), LocalDateTime.of(2026, 6, 19, 12, 0),
                "Hanoi - Haiphong", 2, "Meeting", "DRAFT", "mgr-id", "Manager Name",
                null, null, null, null, LocalDateTime.now(), LocalDateTime.now()
        );
        when(vehicleRequestService.updateRequest(eq("req-id"), any(VehicleRequestUpsertRequest.class), eq("testuser")))
                .thenReturn(response);

        mockMvc.perform(put("/api/vehicle-requests/{id}", "req-id")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "vehicleType": "Xe 4 chỗ",
                                  "departureTime": "2026-06-19T10:00:00",
                                  "returnTime": "2026-06-19T12:00:00",
                                  "routeDescription": "Hanoi - Haiphong",
                                  "passengerCount": 2,
                                  "purpose": "Meeting",
                                  "approverId": "mgr-id"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Cap nhat de xuat xe thanh cong"));
    }

    @Test
    void submitRequestSendsToApproval() throws Exception {
        VehicleRequestResponse response = new VehicleRequestResponse(
                "req-id", "emp-id", null, null, "Employee Name", "EMP001", "IT",
                "Xe 4 chỗ", LocalDateTime.of(2026, 6, 19, 10, 0), LocalDateTime.of(2026, 6, 19, 12, 0),
                "Hanoi - Haiphong", 2, "Meeting", "PENDING_APPROVAL", "mgr-id", "Manager Name",
                null, null, null, null, LocalDateTime.now(), LocalDateTime.now()
        );
        when(vehicleRequestService.submitRequest(eq("req-id"), eq("testuser")))
                .thenReturn(response);

        mockMvc.perform(post("/api/vehicle-requests/{id}/submit", "req-id")
                        .principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.status").value("PENDING_APPROVAL"));
    }

    @Test
    void approveRequestSavesApprovedStatus() throws Exception {
        VehicleRequestResponse response = new VehicleRequestResponse(
                "req-id", "emp-id", null, null, "Employee Name", "EMP001", "IT",
                "Xe 4 chỗ", LocalDateTime.of(2026, 6, 19, 10, 0), LocalDateTime.of(2026, 6, 19, 12, 0),
                "Hanoi - Haiphong", 2, "Meeting", "APPROVED", "mgr-id", "Manager Name",
                "Driver Name", "0987654321", "30A-12345", null, LocalDateTime.now(), LocalDateTime.now()
        );
        when(vehicleRequestService.approveRequest(eq("req-id"), any(VehicleRequestApproveRequest.class), eq("testuser")))
                .thenReturn(response);

        mockMvc.perform(post("/api/vehicle-requests/{id}/approve", "req-id")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "driverName": "Driver Name",
                                  "driverPhone": "0987654321",
                                  "licensePlate": "30A-12345"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.status").value("APPROVED"));
    }

    @Test
    void rejectRequestSavesRejectedStatus() throws Exception {
        VehicleRequestResponse response = new VehicleRequestResponse(
                "req-id", "emp-id", null, null, "Employee Name", "EMP001", "IT",
                "Xe 4 chỗ", LocalDateTime.of(2026, 6, 19, 10, 0), LocalDateTime.of(2026, 6, 19, 12, 0),
                "Hanoi - Haiphong", 2, "Meeting", "REJECTED", "mgr-id", "Manager Name",
                null, null, null, "Not budget", LocalDateTime.now(), LocalDateTime.now()
        );
        when(vehicleRequestService.rejectRequest(eq("req-id"), any(VehicleRequestRejectRequest.class), eq("testuser")))
                .thenReturn(response);

        mockMvc.perform(post("/api/vehicle-requests/{id}/reject", "req-id")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "rejectReason": "Not budget"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.status").value("REJECTED"));
    }

    @Test
    void deleteRequestRemovesRequest() throws Exception {
        mockMvc.perform(delete("/api/vehicle-requests/{id}", "req-id")
                        .principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Xoa de xuat xe thanh cong"));

        verify(vehicleRequestService).deleteRequest(eq("req-id"), eq("testuser"));
    }
}
