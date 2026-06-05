package com.example.med_office.controller;

import com.example.med_office.dto.ApiResponse;
import com.example.med_office.dto.WarehouseOptionResponse;
import com.example.med_office.service.CatalogOptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
@Tag(name = "Warehouse Options", description = "Danh muc kho")
public class WarehouseOptionController {

    private final CatalogOptionService catalogOptionService;

    public WarehouseOptionController(CatalogOptionService catalogOptionService) {
        this.catalogOptionService = catalogOptionService;
    }

    @Operation(summary = "Lay danh sach kho dang hoat dong")
    @GetMapping(path = "/options", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<WarehouseOptionResponse>>> getOptions() {
        return ResponseEntity.ok(ApiResponse.success(
                "Lay danh sach kho thanh cong",
                catalogOptionService.getWarehouseOptions()
        ));
    }
}
