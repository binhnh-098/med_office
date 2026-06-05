package com.example.med_office.controller;

import com.example.med_office.dto.ApiResponse;
import com.example.med_office.dto.InventoryItemOptionResponse;
import com.example.med_office.service.CatalogOptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/inventory-items")
@Tag(name = "Inventory Item Options", description = "Danh muc vat tu")
public class InventoryItemController {

    private final CatalogOptionService catalogOptionService;

    public InventoryItemController(CatalogOptionService catalogOptionService) {
        this.catalogOptionService = catalogOptionService;
    }

    @Operation(summary = "Lay danh sach vat tu")
    @GetMapping(path = "/options", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<InventoryItemOptionResponse>>> getOptions(
            @RequestParam(required = false) String keyword
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lay danh sach vat tu thanh cong",
                catalogOptionService.getInventoryItemOptions(keyword)
        ));
    }
}
