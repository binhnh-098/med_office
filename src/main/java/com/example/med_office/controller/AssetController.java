package com.example.med_office.controller;

import com.example.med_office.dto.ApiResponse;
import com.example.med_office.dto.AssetDTOs.AssetResponse;
import com.example.med_office.dto.AssetDTOs.AssetUpsertRequest;
import com.example.med_office.service.AssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assets")
@Tag(name = "Assets", description = "Quản lý danh mục tài sản")
public class AssetController {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @Operation(summary = "Lấy danh sách tài sản phân trang")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Page<AssetResponse>>> getAssets(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lấy danh sách tài sản thành công",
                assetService.getAssets(keyword, category, status, page, size)
        ));
    }

    @Operation(summary = "Lấy chi tiết tài sản")
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<AssetResponse>> getAssetDetail(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lấy chi tiết tài sản thành công",
                assetService.getAssetDetail(id)
        ));
    }

    @Operation(summary = "Tạo mới tài sản")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<AssetResponse>> createAsset(
            @Valid @RequestBody AssetUpsertRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Tạo tài sản thành công",
                assetService.createAsset(request)
        ));
    }

    @Operation(summary = "Cập nhật tài sản")
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<AssetResponse>> updateAsset(
            @PathVariable String id,
            @Valid @RequestBody AssetUpsertRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Cập nhật tài sản thành công",
                assetService.updateAsset(id, request)
        ));
    }

    @Operation(summary = "Xóa tài sản")
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAsset(@PathVariable String id) {
        assetService.deleteAsset(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa tài sản thành công", null));
    }
}
