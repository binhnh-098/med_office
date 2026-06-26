package com.example.med_office.controller;

import com.example.med_office.dto.ApiResponse;
import com.example.med_office.dto.VehicleDTOs.VehicleResponse;
import com.example.med_office.dto.VehicleDTOs.VehicleUpsertRequest;
import com.example.med_office.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@Tag(name = "Vehicles", description = "Quản lý xe công tác cố định")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @Operation(summary = "Lấy danh sách xe phân trang")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Page<VehicleResponse>>> getVehicles(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lấy danh sách xe thành công",
                vehicleService.getVehicles(keyword, page, size)
        ));
    }

    @Operation(summary = "Lấy toàn bộ danh sách xe không phân trang")
    @GetMapping(path = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<VehicleResponse>>> getAllVehicles() {
        return ResponseEntity.ok(ApiResponse.success(
                "Lấy tất cả danh sách xe thành công",
                vehicleService.getAllVehicles()
        ));
    }

    @Operation(summary = "Lấy chi tiết xe")
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<VehicleResponse>> getVehicleDetail(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lấy chi tiết xe thành công",
                vehicleService.getVehicleDetail(id)
        ));
    }

    @Operation(summary = "Tạo mới xe")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<VehicleResponse>> createVehicle(
            @Valid @RequestBody VehicleUpsertRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Tạo xe thành công",
                vehicleService.createVehicle(request)
        ));
    }

    @Operation(summary = "Cập nhật xe")
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<VehicleResponse>> updateVehicle(
            @PathVariable String id,
            @Valid @RequestBody VehicleUpsertRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Cập nhật xe thành công",
                vehicleService.updateVehicle(id, request)
        ));
    }

    @Operation(summary = "Xóa xe")
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteVehicle(@PathVariable String id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa xe thành công", null));
    }
}
