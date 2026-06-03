package com.example.med_office.controller;

import com.example.med_office.dto.ApiResponse;
import com.example.med_office.dto.WarehouseHierarchyItem;
import com.example.med_office.dto.WarehouseHierarchyUpdateRequest;
import com.example.med_office.dto.WarehousePageResponse;
import com.example.med_office.dto.WarehouseRequest;
import com.example.med_office.dto.WarehouseResponse;
import com.example.med_office.dto.WarehouseStatusUpdateRequest;
import com.example.med_office.dto.WarehouseSummaryResponse;
import com.example.med_office.service.WarehouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/kho")
@Tag(name = "Kho", description = "Quản lý kho")
public class WarehouseController {

    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @Operation(summary = "Lấy danh sách kho")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<WarehousePageResponse>> findAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lấy danh sách kho thành công",
                warehouseService.findAll(keyword, status, page, size, sort)
        ));
    }

    @Operation(summary = "Lấy cây phân cấp kho")
    @GetMapping(path = "/hierarchy", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<WarehouseHierarchyItem>>> getHierarchy() {
        return ResponseEntity.ok(ApiResponse.success("Lấy cây phân cấp kho thành công", warehouseService.getHierarchy()));
    }

    @Operation(summary = "Lưu cây phân cấp kho")
    @PutMapping(path = "/hierarchy", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<WarehouseHierarchyItem>>> updateHierarchy(
            @Valid @RequestBody WarehouseHierarchyUpdateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Cập nhật cây phân cấp kho thành công", warehouseService.updateHierarchy(request)));
    }

    @Operation(summary = "Lấy thống kê kho")
    @GetMapping(path = "/summary", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<WarehouseSummaryResponse>> getSummary() {
        return ResponseEntity.ok(ApiResponse.success("Lấy thống kê kho thành công", warehouseService.getSummary()));
    }

    @Operation(summary = "Lấy chi tiết kho")
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<WarehouseResponse>> findById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success("Lấy chi tiết kho thành công", warehouseService.findById(id)));
    }

    @Operation(summary = "Tạo kho")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<WarehouseResponse>> create(@Valid @RequestBody WarehouseRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Tạo kho thành công", warehouseService.create(request)));
    }

    @Operation(summary = "Cập nhật kho")
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<WarehouseResponse>> update(
            @PathVariable String id,
            @Valid @RequestBody WarehouseRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Cập nhật kho thành công", warehouseService.update(id, request)));
    }

    @Operation(summary = "Khóa hoặc mở lại kho")
    @PutMapping(path = "/{id}/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<WarehouseResponse>> updateStatus(
            @PathVariable String id,
            @Valid @RequestBody WarehouseStatusUpdateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái kho thành công", warehouseService.updateStatus(id, request)));
    }
}
