package com.example.med_office.controller;

import com.example.med_office.dto.ApiResponse;
import com.example.med_office.dto.ContractDTOs.*;
import com.example.med_office.service.ContractService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contracts")
@Tag(name = "Contracts", description = "Quản lý hợp đồng lao động")
public class ContractController {

    private final ContractService contractService;

    public ContractController(ContractService contractService) {
        this.contractService = contractService;
    }

    @Operation(summary = "Lấy danh sách hợp đồng")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Page<ContractResponse>>> getContracts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lấy danh sách hợp đồng thành công",
                contractService.getContracts(keyword, status, employeeId, authentication.getName(), page, size)
        ));
    }

    @Operation(summary = "Lấy chi tiết hợp đồng")
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ContractResponse>> getContractDetail(
            @PathVariable String id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lấy chi tiết hợp đồng thành công",
                contractService.getContractDetail(id, authentication.getName())
        ));
    }

    @Operation(summary = "Tạo hợp đồng mới")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ContractResponse>> createContract(
            @Valid @RequestBody ContractUpsertRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Tạo hợp đồng thành công",
                contractService.createContract(request, authentication.getName())
        ));
    }

    @Operation(summary = "Cập nhật hợp đồng")
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ContractResponse>> updateContract(
            @PathVariable String id,
            @Valid @RequestBody ContractUpsertRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Cập nhật hợp đồng thành công",
                contractService.updateContract(id, request, authentication.getName())
        ));
    }

    @Operation(summary = "Xóa hợp đồng")
    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Void>> deleteContract(
            @PathVariable String id,
            Authentication authentication
    ) {
        contractService.deleteContract(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Xóa hợp đồng thành công", null));
    }
}
