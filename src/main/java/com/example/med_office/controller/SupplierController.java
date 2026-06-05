package com.example.med_office.controller;

import com.example.med_office.dto.ApiResponse;
import com.example.med_office.dto.PagedResponse;
import com.example.med_office.dto.SupplierOptionResponse;
import com.example.med_office.dto.SupplierResponse;
import com.example.med_office.service.CatalogOptionService;
import com.example.med_office.service.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/suppliers")
@Tag(name = "Supplier Options", description = "Danh muc nha cung cap")
public class SupplierController {

    private final CatalogOptionService catalogOptionService;
    private final SupplierService supplierService;

    public SupplierController(CatalogOptionService catalogOptionService, SupplierService supplierService) {
        this.catalogOptionService = catalogOptionService;
        this.supplierService = supplierService;
    }

    @Operation(summary = "Lay danh sach nha cung cap co phan trang")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<PagedResponse<SupplierResponse>>> findAll(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lay danh sach nha cung cap thanh cong",
                supplierService.findAll(page, size, keyword, status)
        ));
    }

    @Operation(summary = "Lay danh sach nha cung cap")
    @GetMapping(path = "/options", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<SupplierOptionResponse>>> getOptions(
            @RequestParam(required = false) String keyword
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lay danh sach nha cung cap thanh cong",
                catalogOptionService.getSupplierOptions(keyword)
        ));
    }
}
