package com.example.med_office.controller;

import com.example.med_office.dto.ApiResponse;
import com.example.med_office.dto.SalesOrderDetailResponse;
import com.example.med_office.dto.SalesOrderMutationResponse;
import com.example.med_office.dto.SalesOrderPageResponse;
import com.example.med_office.dto.SalesOrderUpsertRequest;
import com.example.med_office.service.SalesOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/api/sales-orders")
@Tag(name = "Sales Orders", description = "Quan ly don ban hang va hoa don")
public class SalesOrderController {

    private final SalesOrderService salesOrderService;

    public SalesOrderController(SalesOrderService salesOrderService) {
        this.salesOrderService = salesOrderService;
    }

    @Operation(summary = "Lay danh sach don ban hang")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('sales.orders.view')")
    public ResponseEntity<ApiResponse<SalesOrderPageResponse>> findAll(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String warehouseId,
            @RequestParam(required = false) String paymentStatus,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lay danh sach don ban hang thanh cong",
                salesOrderService.findAll(page, size, keyword, status, warehouseId, paymentStatus, fromDate, toDate)
        ));
    }

    @Operation(summary = "Lay chi tiet don ban hang")
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('sales.orders.view')")
    public ResponseEntity<ApiResponse<SalesOrderDetailResponse>> findById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lay chi tiet don ban hang thanh cong",
                salesOrderService.findById(id)
        ));
    }

    @Operation(summary = "Tao moi don ban hang nhap")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('sales.orders.manage')")
    public ResponseEntity<ApiResponse<SalesOrderMutationResponse>> create(
            @Valid @RequestBody SalesOrderUpsertRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Tao moi don ban hang thanh cong",
                salesOrderService.create(request)
        ));
    }

    @Operation(summary = "Cap nhat don ban hang nhap")
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('sales.orders.manage')")
    public ResponseEntity<ApiResponse<SalesOrderMutationResponse>> updateDraft(
            @PathVariable String id,
            @Valid @RequestBody SalesOrderUpsertRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Cap nhat don ban hang thanh cong",
                salesOrderService.updateDraft(id, request)
        ));
    }

    @Operation(summary = "Hoan tat don ban hang va tu dong xuat kho")
    @PostMapping(path = "/{id}/complete", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('sales.orders.manage')")
    public ResponseEntity<ApiResponse<SalesOrderMutationResponse>> complete(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Hoan tat don ban hang thanh cong",
                salesOrderService.complete(id)
        ));
    }

    @Operation(summary = "Phat hanh lai hoa don dien tu MISA")
    @PostMapping(path = "/{id}/issue-e-invoice", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('sales.orders.manage')")
    public ResponseEntity<ApiResponse<SalesOrderMutationResponse>> issueEInvoice(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Yeu cau phat hanh hoa don dien tu thanh cong",
                salesOrderService.issueInvoiceManually(id)
        ));
    }

    @Operation(summary = "Xoa don ban hang nhap")
    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('sales.orders.manage')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        salesOrderService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Xoa don ban hang thanh cong", null));
    }
}
