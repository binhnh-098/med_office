package com.example.med_office.controller;

import com.example.med_office.dto.ApiResponse;
import com.example.med_office.dto.BusinessTripDTOs.*;
import com.example.med_office.service.BusinessTripService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/business-trips")
@Tag(name = "Business Trips", description = "Quan ly va de xuat cong tac")
public class BusinessTripController {

    private final BusinessTripService businessTripService;

    public BusinessTripController(BusinessTripService businessTripService) {
        this.businessTripService = businessTripService;
    }

    @Operation(summary = "Lay danh sach de xuat cong tac cua toi")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Page<BusinessTripResponse>>> getMyTrips(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lay danh sach de xuat cong tac thanh cong",
                businessTripService.getMyTrips(keyword, status, authentication.getName(), page, size)
        ));
    }

    @Operation(summary = "Lay danh sach de xuat cong tac cho duyet")
    @GetMapping(path = "/approvals", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Page<BusinessTripResponse>>> getPendingApprovals(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lay danh sach de xuat cho duyet thanh cong",
                businessTripService.getPendingApprovals(keyword, authentication.getName(), page, size)
        ));
    }

    @Operation(summary = "Lay danh sach nhan su dang trong qua trinh cong tac")
    @GetMapping(path = "/active", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Page<BusinessTripResponse>>> getActiveTrips(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lay danh sach nhan su dang cong tac thanh cong",
                businessTripService.getActiveTrips(keyword, page, size)
        ));
    }

    @Operation(summary = "Lay chi tiet de xuat cong tac")
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<BusinessTripResponse>> getTripDetail(
            @PathVariable String id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lay chi tiet cong tac thanh cong",
                businessTripService.getTripDetail(id, authentication.getName())
        ));
    }

    @Operation(summary = "Tao moi de xuat cong tac")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<BusinessTripResponse>> createTrip(
            @Valid @RequestBody BusinessTripUpsertRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Tao de xuat cong tac thanh cong",
                businessTripService.createTrip(request, authentication.getName())
        ));
    }

    @Operation(summary = "Cap nhat de xuat cong tac")
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<BusinessTripResponse>> updateTrip(
            @PathVariable String id,
            @Valid @RequestBody BusinessTripUpsertRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Cap nhat de xuat cong tac thanh cong",
                businessTripService.updateTrip(id, request, authentication.getName())
        ));
    }

    @Operation(summary = "Gui de xuat cong tac cho cap tren duyet")
    @PostMapping(path = "/{id}/submit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<BusinessTripResponse>> submitTrip(
            @PathVariable String id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Gui de xuat cong tac thanh cong",
                businessTripService.submitTrip(id, authentication.getName())
        ));
    }

    @Operation(summary = "Phe duyet de xuat cong tac")
    @PostMapping(path = "/{id}/approve", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<BusinessTripResponse>> approveTrip(
            @PathVariable String id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Duyet de xuat cong tac thanh cong",
                businessTripService.approveTrip(id, authentication.getName())
        ));
    }

    @Operation(summary = "Tu choi de xuat cong tac")
    @PostMapping(path = "/{id}/reject", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<BusinessTripResponse>> rejectTrip(
            @PathVariable String id,
            @Valid @RequestBody BusinessTripRejectRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Tu choi de xuat cong tac thanh cong",
                businessTripService.rejectTrip(id, request, authentication.getName())
        ));
    }

    @Operation(summary = "Xoa de xuat cong tac nhap")
    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Void>> deleteTrip(
            @PathVariable String id,
            Authentication authentication
    ) {
        businessTripService.deleteTrip(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Xoa de xuat cong tac thanh cong", null));
    }
}
