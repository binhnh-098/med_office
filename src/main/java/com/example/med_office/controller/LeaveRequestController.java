package com.example.med_office.controller;

import com.example.med_office.dto.ApiResponse;
import com.example.med_office.dto.LeaveRequestDTOs.*;
import com.example.med_office.service.LeaveRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leave-requests")
@Tag(name = "Leave Requests", description = "Quan ly yeu cau nghi phep")
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    public LeaveRequestController(LeaveRequestService leaveRequestService) {
        this.leaveRequestService = leaveRequestService;
    }

    @Operation(summary = "Lay danh sach yeu cau nghi phep")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Page<LeaveRequestResponse>>> getLeaveRequests(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lay danh sach yeu cau nghi phep thanh cong",
                leaveRequestService.getLeaveRequests(keyword, status, employeeId, authentication.getName(), page, size)
        ));
    }

    @Operation(summary = "Lay thong tin so du phep nam")
    @GetMapping(path = "/balance", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<LeaveBalanceResponse>> getLeaveBalance(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lay so du phep nam thanh cong",
                leaveRequestService.getLeaveBalance(authentication.getName())
        ));
    }

    @Operation(summary = "Lay chi tiet yeu cau nghi phep")
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<LeaveRequestResponse>> getLeaveRequestDetail(
            @PathVariable String id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lay chi tiet yeu cau nghi phep thanh cong",
                leaveRequestService.getLeaveRequestDetail(id, authentication.getName())
        ));
    }

    @Operation(summary = "Tao moi yeu cau nghi phep")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<LeaveRequestResponse>> createLeaveRequest(
            @Valid @RequestBody LeaveRequestUpsertRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Tao yeu cau nghi phep thanh cong",
                leaveRequestService.createLeaveRequest(request, authentication.getName())
        ));
    }

    @Operation(summary = "Cap nhat yeu cau nghi phep")
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<LeaveRequestResponse>> updateLeaveRequest(
            @PathVariable String id,
            @Valid @RequestBody LeaveRequestUpsertRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Cap nhat yeu cau nghi phep thanh cong",
                leaveRequestService.updateLeaveRequest(id, request, authentication.getName())
        ));
    }

    @Operation(summary = "Gui yeu cau nghi phep len cap tren cho duyet")
    @PostMapping(path = "/{id}/submit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<LeaveRequestResponse>> submitLeaveRequest(
            @PathVariable String id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Gui yeu cau nghi phep thanh cong",
                leaveRequestService.submitLeaveRequest(id, authentication.getName())
        ));
    }

    @Operation(summary = "Phe duyet yeu cau nghi phep")
    @PostMapping(path = "/{id}/approve", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<LeaveRequestResponse>> approveLeaveRequest(
            @PathVariable String id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Duyet yeu cau nghi phep thanh cong",
                leaveRequestService.approveLeaveRequest(id, authentication.getName())
        ));
    }

    @Operation(summary = "Tu choi yeu cau nghi phep")
    @PostMapping(path = "/{id}/reject", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<LeaveRequestResponse>> rejectLeaveRequest(
            @PathVariable String id,
            @Valid @RequestBody LeaveRequestRejectRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Tu choi yeu cau nghi phep thanh cong",
                leaveRequestService.rejectLeaveRequest(id, request, authentication.getName())
        ));
    }

    @Operation(summary = "Xoa yeu cau nghi phep (khi o trang thai nhap)")
    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Void>> deleteLeaveRequest(
            @PathVariable String id,
            Authentication authentication
    ) {
        leaveRequestService.deleteLeaveRequest(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Xoa yeu cau nghi phep thanh cong", null));
    }
}
