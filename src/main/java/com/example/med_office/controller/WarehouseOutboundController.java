package com.example.med_office.controller;

import com.example.med_office.dto.ApiResponse;
import com.example.med_office.dto.WarehouseOutboundApprovalRequest;
import com.example.med_office.dto.WarehouseOutboundCreateRequest;
import com.example.med_office.dto.WarehouseOutboundDetailResponse;
import com.example.med_office.dto.WarehouseOutboundMutationResponse;
import com.example.med_office.dto.WarehouseOutboundPageResponse;
import com.example.med_office.dto.WarehouseOutboundRejectRequest;
import com.example.med_office.dto.WarehouseOutboundUpsertRequest;
import com.example.med_office.service.WarehouseOutboundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.LocalDate;

@RestController
@Validated
@RequestMapping("/api/warehouse-outbounds")
@Tag(name = "Warehouse Outbounds", description = "Quan ly phieu xuat kho")
public class WarehouseOutboundController {

    private final WarehouseOutboundService warehouseOutboundService;

    public WarehouseOutboundController(WarehouseOutboundService warehouseOutboundService) {
        this.warehouseOutboundService = warehouseOutboundService;
    }

    @Operation(summary = "Lay danh sach phieu xuat kho")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<WarehouseOutboundPageResponse>> findAll(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String warehouseId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lay danh sach phieu xuat kho thanh cong",
                warehouseOutboundService.findAll(page, size, keyword, status, warehouseId, fromDate, toDate)
        ));
    }

    @Operation(summary = "Lay chi tiet phieu xuat kho")
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<WarehouseOutboundDetailResponse>> findById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lay chi tiet phieu xuat kho thanh cong",
                warehouseOutboundService.findById(id)
        ));
    }

    @Operation(summary = "Tao moi phieu xuat kho")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<WarehouseOutboundMutationResponse>> create(
            @Valid @RequestBody WarehouseOutboundCreateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Tao phieu xuat kho thanh cong",
                warehouseOutboundService.create(request)
        ));
    }

    @Operation(summary = "Cap nhat phieu xuat kho nhap")
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<WarehouseOutboundMutationResponse>> updateDraft(
            @PathVariable String id,
            @Valid @RequestBody WarehouseOutboundUpsertRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Cap nhat phieu xuat kho thanh cong",
                warehouseOutboundService.updateDraft(id, request)
        ));
    }

    @Operation(summary = "Gui duyet phieu xuat kho")
    @PostMapping(path = "/{id}/submit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<WarehouseOutboundMutationResponse>> submit(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Gui duyet phieu xuat kho thanh cong",
                warehouseOutboundService.submit(id)
        ));
    }

    @Operation(summary = "Duyet phieu xuat kho")
    @PostMapping(path = "/{id}/approve", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<WarehouseOutboundMutationResponse>> approve(
            @PathVariable String id,
            @Valid @RequestBody WarehouseOutboundApprovalRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Duyet phieu xuat kho thanh cong",
                warehouseOutboundService.approve(id, request)
        ));
    }

    @Operation(summary = "Tu choi phieu xuat kho")
    @PostMapping(path = "/{id}/reject", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<WarehouseOutboundMutationResponse>> reject(
            @PathVariable String id,
            @Valid @RequestBody WarehouseOutboundRejectRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Tu choi phieu xuat kho thanh cong",
                warehouseOutboundService.reject(id, request)
        ));
    }

    @Operation(summary = "Hoan tat xuat kho")
    @PostMapping(path = "/{id}/complete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<WarehouseOutboundMutationResponse>> complete(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Hoan tat xuat kho thanh cong",
                warehouseOutboundService.complete(id)
        ));
    }
}
