package com.example.med_office.controller;

import com.example.med_office.dto.ApiResponse;
import com.example.med_office.dto.WarehouseInboundApprovalRequest;
import com.example.med_office.dto.WarehouseInboundCreateRequest;
import com.example.med_office.dto.WarehouseInboundDetailResponse;
import com.example.med_office.dto.WarehouseInboundMutationResponse;
import com.example.med_office.dto.WarehouseInboundPageResponse;
import com.example.med_office.dto.WarehouseInboundRejectRequest;
import com.example.med_office.dto.WarehouseInboundUpsertRequest;
import com.example.med_office.service.WarehouseInboundService;
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
@RequestMapping("/api/warehouse-inbounds")
@Tag(name = "Warehouse Inbounds", description = "Quan ly phieu nhap kho")
public class WarehouseInboundController {

    private final WarehouseInboundService warehouseInboundService;

    public WarehouseInboundController(WarehouseInboundService warehouseInboundService) {
        this.warehouseInboundService = warehouseInboundService;
    }

    @Operation(summary = "Lay danh sach phieu nhap kho")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<WarehouseInboundPageResponse>> findAll(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String warehouseId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lay danh sach phieu nhap kho thanh cong",
                warehouseInboundService.findAll(page, size, keyword, status, warehouseId, fromDate, toDate)
        ));
    }

    @Operation(summary = "Lay chi tiet phieu nhap kho")
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<WarehouseInboundDetailResponse>> findById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lay chi tiet phieu nhap kho thanh cong",
                warehouseInboundService.findById(id)
        ));
    }

    @Operation(summary = "Tao moi phieu nhap kho")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<WarehouseInboundMutationResponse>> create(
            @Valid @RequestBody WarehouseInboundCreateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Tao phieu nhap kho thanh cong",
                warehouseInboundService.create(request)
        ));
    }

    @Operation(summary = "Cap nhat phieu nhap kho nhap")
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<WarehouseInboundMutationResponse>> updateDraft(
            @PathVariable String id,
            @Valid @RequestBody WarehouseInboundUpsertRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Cap nhat phieu nhap kho thanh cong",
                warehouseInboundService.updateDraft(id, request)
        ));
    }

    @Operation(summary = "Gui duyet phieu nhap kho")
    @PostMapping(path = "/{id}/submit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<WarehouseInboundMutationResponse>> submit(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Gui duyet phieu nhap kho thanh cong",
                warehouseInboundService.submit(id)
        ));
    }

    @Operation(summary = "Duyet phieu nhap kho")
    @PostMapping(path = "/{id}/approve", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<WarehouseInboundMutationResponse>> approve(
            @PathVariable String id,
            @Valid @RequestBody WarehouseInboundApprovalRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Duyet phieu nhap kho thanh cong",
                warehouseInboundService.approve(id, request)
        ));
    }

    @Operation(summary = "Tu choi phieu nhap kho")
    @PostMapping(path = "/{id}/reject", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<WarehouseInboundMutationResponse>> reject(
            @PathVariable String id,
            @Valid @RequestBody WarehouseInboundRejectRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Tu choi phieu nhap kho thanh cong",
                warehouseInboundService.reject(id, request)
        ));
    }

    @Operation(summary = "Hoan tat nhap kho")
    @PostMapping(path = "/{id}/complete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<WarehouseInboundMutationResponse>> complete(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Hoan tat nhap kho thanh cong",
                warehouseInboundService.complete(id)
        ));
    }
}
