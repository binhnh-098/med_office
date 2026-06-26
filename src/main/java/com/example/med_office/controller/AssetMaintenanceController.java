package com.example.med_office.controller;

import com.example.med_office.dto.ApiResponse;
import com.example.med_office.dto.AssetMaintenanceDTOs.*;
import com.example.med_office.service.AssetMaintenanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/asset-maintenances")
@Tag(name = "Asset Maintenances", description = "Quản lý bảo trì tài sản & thiết bị")
public class AssetMaintenanceController {

    private final AssetMaintenanceService assetMaintenanceService;

    public AssetMaintenanceController(AssetMaintenanceService assetMaintenanceService) {
        this.assetMaintenanceService = assetMaintenanceService;
    }

    @Operation(summary = "Lấy lịch sử bảo trì phân trang")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Page<AssetMaintenanceResponse>>> getMaintenances(
            @RequestParam(required = false) String assetId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(ApiResponse.success(
            "Lấy danh sách lịch sử bảo trì thành công",
            assetMaintenanceService.getMaintenances(assetId, status, keyword, page, size)
        ));
    }

    @Operation(summary = "Lấy chi tiết biên bản bảo trì")
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<AssetMaintenanceResponse>> getMaintenanceDetail(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(
            "Lấy chi tiết thông tin bảo trì thành công",
            assetMaintenanceService.getMaintenanceDetail(id)
        ));
    }

    @Operation(summary = "Gửi tài sản đi bảo trì")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<AssetMaintenanceResponse>> createMaintenance(
            @Valid @RequestBody AssetMaintenanceSendRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
            "Gửi tài sản đi bảo trì thành công",
            assetMaintenanceService.createMaintenance(request)
        ));
    }

    @Operation(summary = "Hoàn thành bảo trì tài sản")
    @PostMapping(path = "/{id}/complete", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<AssetMaintenanceResponse>> completeMaintenance(
            @PathVariable String id,
            @Valid @RequestBody AssetMaintenanceCompleteRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
            "Xác nhận hoàn thành bảo trì thành công",
            assetMaintenanceService.completeMaintenance(id, request)
        ));
    }

    @Operation(summary = "Hủy bảo trì tài sản")
    @PostMapping(path = "/{id}/cancel", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<AssetMaintenanceResponse>> cancelMaintenance(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(
            "Hủy bảo trì thành công",
            assetMaintenanceService.cancelMaintenance(id)
        ));
    }
}
