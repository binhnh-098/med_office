package com.example.med_office.controller;

import com.example.med_office.dto.ApiResponse;
import com.example.med_office.dto.WarehouseInventoryPageResponse;
import com.example.med_office.service.WarehouseInventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping({"/api/warehouse-inventories", "/api/inventory-balances"})
@Tag(name = "Warehouse Inventories", description = "Ton kho theo kho")
public class WarehouseInventoryController {

    private final WarehouseInventoryService warehouseInventoryService;

    public WarehouseInventoryController(WarehouseInventoryService warehouseInventoryService) {
        this.warehouseInventoryService = warehouseInventoryService;
    }

    @Operation(summary = "Lay danh sach ton kho")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<WarehouseInventoryPageResponse>> findAll(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "size must be >= 1")
            @Max(value = 500, message = "size must be <= 500")
            int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String warehouseId
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lay danh sach ton kho thanh cong",
                warehouseInventoryService.findAll(page, size, keyword, warehouseId)
        ));
    }
}
