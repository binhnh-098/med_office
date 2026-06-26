package com.example.med_office.controller;

import com.example.med_office.dto.ApiResponse;
import com.example.med_office.dto.AssetLiquidationDTOs.*;
import com.example.med_office.service.AssetLiquidationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/asset-liquidations")
@Tag(name = "Asset Liquidations", description = "Quản lý thanh lý tài sản & thiết bị")
public class AssetLiquidationController {

    private final AssetLiquidationService assetLiquidationService;

    public AssetLiquidationController(AssetLiquidationService assetLiquidationService) {
        this.assetLiquidationService = assetLiquidationService;
    }

    @Operation(summary = "Lấy lịch sử thanh lý phân trang")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Page<AssetLiquidationResponse>>> getLiquidations(
            @RequestParam(required = false) String assetId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(ApiResponse.success(
            "Lấy danh sách lịch sử thanh lý thành công",
            assetLiquidationService.getLiquidations(assetId, keyword, page, size)
        ));
    }

    @Operation(summary = "Lấy chi tiết biên bản thanh lý")
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<AssetLiquidationResponse>> getLiquidationDetail(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(
            "Lấy chi tiết thông tin thanh lý thành công",
            assetLiquidationService.getLiquidationDetail(id)
        ));
    }

    @Operation(summary = "Thanh lý tài sản")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<AssetLiquidationResponse>> createLiquidation(
            @Valid @RequestBody AssetLiquidationRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
            "Thanh lý tài sản thành công",
            assetLiquidationService.createLiquidation(request)
        ));
    }

    @Operation(summary = "Hủy thanh lý tài sản")
    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Void>> cancelLiquidation(@PathVariable String id) {
        assetLiquidationService.cancelLiquidation(id);
        return ResponseEntity.ok(ApiResponse.success("Hủy thanh lý thành công", null));
    }
}
