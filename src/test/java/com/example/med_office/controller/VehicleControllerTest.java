package com.example.med_office.controller;

import com.example.med_office.dto.ApiResponse;
import com.example.med_office.dto.VehicleDTOs.VehicleResponse;
import com.example.med_office.dto.VehicleDTOs.VehicleUpsertRequest;
import com.example.med_office.repository.HoSoNhanVienRepository;
import com.example.med_office.repository.NguoiDungRepository;
import com.example.med_office.service.VehicleService;
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

@WebMvcTest(controllers = VehicleController.class)
@AutoConfigureMockMvc(addFilters = false)
class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VehicleService vehicleService;

    @MockitoBean
    private NguoiDungRepository nguoiDungRepository;

    @MockitoBean
    private HoSoNhanVienRepository hoSoNhanVienRepository;

    private final UsernamePasswordAuthenticationToken principal =
            new UsernamePasswordAuthenticationToken("testuser", "password");

    @Test
    void getVehiclesReturnsPage() throws Exception {
        Page<VehicleResponse> page = new PageImpl<>(List.of());
        when(vehicleService.getVehicles(eq("keyword"), eq(0), eq(10)))
                .thenReturn(page);

        mockMvc.perform(get("/api/vehicles")
                        .principal(principal)
                        .param("keyword", "keyword")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Lấy danh sách xe thành công"));
    }

    @Test
    void getAllVehiclesReturnsList() throws Exception {
        VehicleResponse response = new VehicleResponse(
                "v-1", "Toyota", "30H-123.45", "Driver A", "0901234567", 4,
                LocalDateTime.now(), LocalDateTime.now()
        );
        when(vehicleService.getAllVehicles()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/vehicles/all")
                        .principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].id").value("v-1"));
    }

    @Test
    void getVehicleDetailReturnsResponse() throws Exception {
        VehicleResponse response = new VehicleResponse(
                "v-1", "Toyota", "30H-123.45", "Driver A", "0901234567", 4,
                LocalDateTime.now(), LocalDateTime.now()
        );
        when(vehicleService.getVehicleDetail(eq("v-1")))
                .thenReturn(response);

        mockMvc.perform(get("/api/vehicles/{id}", "v-1")
                        .principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value("v-1"));
    }

    @Test
    void createVehicleReturnsSaved() throws Exception {
        VehicleResponse response = new VehicleResponse(
                "v-1", "Toyota", "30H-123.45", "Driver A", "0901234567", 4,
                LocalDateTime.now(), LocalDateTime.now()
        );
        when(vehicleService.createVehicle(any(VehicleUpsertRequest.class)))
                .thenReturn(response);

        String requestBody = """
                {
                    "name": "Toyota",
                    "licensePlate": "30H-123.45",
                    "driverName": "Driver A",
                    "driverPhone": "0901234567",
                    "seatCapacity": 4
                }
                """;

        mockMvc.perform(post("/api/vehicles")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("Toyota"));
    }

    @Test
    void updateVehicleReturnsUpdated() throws Exception {
        VehicleResponse response = new VehicleResponse(
                "v-1", "Toyota Updated", "30H-123.45", "Driver A", "0901234567", 4,
                LocalDateTime.now(), LocalDateTime.now()
        );
        when(vehicleService.updateVehicle(eq("v-1"), any(VehicleUpsertRequest.class)))
                .thenReturn(response);

        String requestBody = """
                {
                    "name": "Toyota Updated",
                    "licensePlate": "30H-123.45",
                    "driverName": "Driver A",
                    "driverPhone": "0901234567",
                    "seatCapacity": 4
                }
                """;

        mockMvc.perform(put("/api/vehicles/{id}", "v-1")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("Toyota Updated"));
    }

    @Test
    void deleteVehicleReturnsSuccess() throws Exception {
        mockMvc.perform(delete("/api/vehicles/{id}", "v-1")
                        .principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Xóa xe thành công"));

        verify(vehicleService).deleteVehicle(eq("v-1"));
    }
}
