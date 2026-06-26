package com.example.med_office.controller;

import com.example.med_office.dto.ApiResponse;
import com.example.med_office.dto.AssetInventoryDTOs.*;
import com.example.med_office.service.AssetInventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/asset-inventories")
@Tag(name = "Asset Inventories", description = "Quản lý kiểm kê tài sản & thiết bị")
public class AssetInventoryController {

    private final AssetInventoryService assetInventoryService;

    public AssetInventoryController(AssetInventoryService assetInventoryService) {
        this.assetInventoryService = assetInventoryService;
    }

    @Operation(summary = "Lấy danh sách kiểm kê phân trang")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Page<AssetInventoryResponse>>> getInventories(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(ApiResponse.success(
            "Lấy danh sách biên bản kiểm kê thành công",
            assetInventoryService.getInventories(status, keyword, page, size)
        ));
    }

    @Operation(summary = "Lấy chi tiết biên bản kiểm kê")
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<AssetInventoryResponse>> getInventoryDetail(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(
            "Lấy chi tiết thông tin kiểm kê thành công",
            assetInventoryService.getInventoryDetail(id)
        ));
    }

    @Operation(summary = "Lưu biên bản kiểm kê (Tạo mới hoặc cập nhật nháp)")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<AssetInventoryResponse>> saveInventory(
            @Valid @RequestBody AssetInventorySaveRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
            "Lưu biên bản kiểm kê thành công",
            assetInventoryService.saveInventory(request)
        ));
    }

    @Operation(summary = "Xóa biên bản kiểm kê nháp")
    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Void>> deleteInventory(@PathVariable String id) {
        assetInventoryService.deleteInventory(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa biên bản kiểm kê nháp thành công", null));
    }
}
