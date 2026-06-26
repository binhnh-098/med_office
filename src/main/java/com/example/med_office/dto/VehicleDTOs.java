package com.example.med_office.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public final class VehicleDTOs {

    private VehicleDTOs() {}

    public record VehicleUpsertRequest(
            @NotBlank(message = "Tên xe không được để trống")
            String name,

            @NotBlank(message = "Biển số xe không được để trống")
            String licensePlate,

            @NotBlank(message = "Tên tài xế không được để trống")
            String driverName,

            @NotBlank(message = "Số điện thoại tài xế không được để trống")
            String driverPhone,

            @jakarta.validation.constraints.NotNull(message = "Số lượng chỗ không được để trống")
            @jakarta.validation.constraints.Min(value = 1, message = "Số lượng chỗ phải lớn hơn hoặc bằng 1")
            Integer seatCapacity
    ) {}

    public record VehicleResponse(
            String id,
            String name,
            String licensePlate,
            String driverName,
            String driverPhone,
            Integer seatCapacity,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}
}
