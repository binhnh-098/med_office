package com.example.med_office.controller;

import com.example.med_office.dto.WarehouseInventoryPageResponse;
import com.example.med_office.repository.HoSoNhanVienRepository;
import com.example.med_office.repository.NguoiDungRepository;
import com.example.med_office.service.WarehouseInventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = WarehouseInventoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class WarehouseInventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WarehouseInventoryService warehouseInventoryService;

    @MockitoBean
    private NguoiDungRepository nguoiDungRepository;

    @MockitoBean
    private HoSoNhanVienRepository hoSoNhanVienRepository;

    @Test
    void inventoryBalancesAliasReturnsSameContract() throws Exception {
        when(warehouseInventoryService.findAll(eq(0), eq(10), eq(null), eq(null)))
                .thenReturn(new WarehouseInventoryPageResponse(List.of(), 0, 10, 0, 0));

        mockMvc.perform(get("/api/inventory-balances"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Lay danh sach ton kho thanh cong"))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.size").value(10))
                .andExpect(jsonPath("$.data.totalElements").value(0))
                .andExpect(jsonPath("$.data.totalPages").value(0));
    }

    @Test
    void inventoryEndpointRejectsSizeAbove500WithClearValidationMessage() throws Exception {
        mockMvc.perform(get("/api/warehouse-inventories")
                        .param("page", "0")
                        .param("size", "501"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.data.size").value("size must be <= 500"));

        verifyNoInteractions(warehouseInventoryService);
    }
}
