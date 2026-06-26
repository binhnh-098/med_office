package com.example.med_office.controller;

import com.example.med_office.dto.ApiResponse;
import com.example.med_office.dto.VehicleRequestDTOs.*;
import com.example.med_office.service.VehicleRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vehicle-requests")
@Tag(name = "Vehicle Requests", description = "Quan ly va de xuat phuong tien di cong tac")
public class VehicleRequestController {

    private final VehicleRequestService vehicleRequestService;

    public VehicleRequestController(VehicleRequestService vehicleRequestService) {
        this.vehicleRequestService = vehicleRequestService;
    }

    @Operation(summary = "Lay danh sach de xuat xe cua toi")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Page<VehicleRequestResponse>>> getMyRequests(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lay danh sach de xuat xe thanh cong",
                vehicleRequestService.getMyRequests(keyword, status, authentication.getName(), page, size)
        ));
    }

    @Operation(summary = "Lay danh sach de xuat xe cho duyet")
    @GetMapping(path = "/approvals", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Page<VehicleRequestResponse>>> getPendingApprovals(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lay danh sach de xuat xe cho duyet thanh cong",
                vehicleRequestService.getPendingApprovals(keyword, authentication.getName(), page, size)
        ));
    }

    @Operation(summary = "Lay chi tiet de xuat xe")
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<VehicleRequestResponse>> getRequestDetail(
            @PathVariable String id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lay chi tiet de xuat xe thanh cong",
                vehicleRequestService.getRequestDetail(id, authentication.getName())
        ));
    }

    @Operation(summary = "Tao moi de xuat xe")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<VehicleRequestResponse>> createRequest(
            @Valid @RequestBody VehicleRequestUpsertRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Tao de xuat xe thanh cong",
                vehicleRequestService.createRequest(request, authentication.getName())
        ));
    }

    @Operation(summary = "Cap nhat de xuat xe")
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<VehicleRequestResponse>> updateRequest(
            @PathVariable String id,
            @Valid @RequestBody VehicleRequestUpsertRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Cap nhat de xuat xe thanh cong",
                vehicleRequestService.updateRequest(id, request, authentication.getName())
        ));
    }

    @Operation(summary = "Gui de xuat xe cho cap tren duyet")
    @PostMapping(path = "/{id}/submit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<VehicleRequestResponse>> submitRequest(
            @PathVariable String id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Gui de xuat xe thanh cong",
                vehicleRequestService.submitRequest(id, authentication.getName())
        ));
    }

    @Operation(summary = "Phe duyet de xuat xe")
    @PostMapping(path = "/{id}/approve", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<VehicleRequestResponse>> approveRequest(
            @PathVariable String id,
            @Valid @RequestBody VehicleRequestApproveRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Duyet de xuat xe thanh cong",
                vehicleRequestService.approveRequest(id, request, authentication.getName())
        ));
    }

    @Operation(summary = "Tu choi de xuat xe")
    @PostMapping(path = "/{id}/reject", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<VehicleRequestResponse>> rejectRequest(
            @PathVariable String id,
            @Valid @RequestBody VehicleRequestRejectRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Tu choi de xuat xe thanh cong",
                vehicleRequestService.rejectRequest(id, request, authentication.getName())
        ));
    }

    @Operation(summary = "Xoa de xuat xe nhap")
    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Void>> deleteRequest(
            @PathVariable String id,
            Authentication authentication
    ) {
        vehicleRequestService.deleteRequest(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Xoa de xuat xe thanh cong", null));
    }
}
