package com.example.med_office.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public final class VehicleRequestDTOs {

    private VehicleRequestDTOs() {}

    public record VehicleRequestUpsertRequest(
            String vehicleId,

            @NotBlank(message = "Loại xe không được để trống")
            String vehicleType,

            @NotNull(message = "Thời gian xuất phát không được để trống")
            LocalDateTime departureTime,

            @NotNull(message = "Thời gian về không được để trống")
            LocalDateTime returnTime,

            @NotBlank(message = "Lộ trình di chuyển không được để trống")
            String routeDescription,

            @NotNull(message = "Số người đi không được để trống")
            @Min(value = 1, message = "Số người đi phải lớn hơn hoặc bằng 1")
            Integer passengerCount,

            @NotBlank(message = "Mục đích sử dụng không được để trống")
            String purpose,

            @NotBlank(message = "Người duyệt không được để trống")
            String approverId
    ) {}

    public record VehicleRequestRejectRequest(
            @NotBlank(message = "Lý do từ chối không được để trống")
            String rejectReason
    ) {}

    public record VehicleRequestApproveRequest(
            @NotBlank(message = "Tên tài xế không được để trống")
            String driverName,

            @NotBlank(message = "Số điện thoại tài xế không được để trống")
            String driverPhone,

            @NotBlank(message = "Biển số xe không được để trống")
            String licensePlate
    ) {}

    public record VehicleRequestResponse(
            String id,
            String hoSoNhanVienId,
            String vehicleId,
            String vehicleName,
            String employeeName,
            String employeeCode,
            String departmentName,
            String vehicleType,
            LocalDateTime departureTime,
            LocalDateTime returnTime,
            String routeDescription,
            Integer passengerCount,
            String purpose,
            String status,
            String approverId,
            String approverName,
            String driverName,
            String driverPhone,
            String licensePlate,
            String rejectReason,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}
}
