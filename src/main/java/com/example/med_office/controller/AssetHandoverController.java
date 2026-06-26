package com.example.med_office.controller;

import com.example.med_office.dto.ApiResponse;
import com.example.med_office.dto.AssetHandoverDTOs.*;
import com.example.med_office.service.AssetHandoverService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/asset-handovers")
@Tag(name = "Asset Handovers", description = "Quản lý bàn giao & điều chuyển tài sản")
public class AssetHandoverController {

    private final AssetHandoverService assetHandoverService;

    public AssetHandoverController(AssetHandoverService assetHandoverService) {
        this.assetHandoverService = assetHandoverService;
    }

    @Operation(summary = "Lấy lịch sử bàn giao/điều chuyển phân trang")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Page<AssetHandoverResponse>>> getHandovers(
            @RequestParam(required = false) String assetId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lấy lịch sử bàn giao/điều chuyển thành công",
                assetHandoverService.getHandovers(assetId, type, keyword, page, size)
        ));
    }

    @Operation(summary = "Lấy chi tiết biên bản bàn giao/điều chuyển")
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<AssetHandoverResponse>> getHandoverDetail(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lấy chi tiết bàn giao/điều chuyển thành công",
                assetHandoverService.getHandoverDetail(id)
        ));
    }

    @Operation(summary = "Tạo biên bản bàn giao/điều chuyển/thu hồi tài sản")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<AssetHandoverResponse>> createHandover(
            @Valid @RequestBody AssetHandoverUpsertRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Tạo bàn giao/điều chuyển thành công",
                assetHandoverService.createHandover(request)
        ));
    }
}
