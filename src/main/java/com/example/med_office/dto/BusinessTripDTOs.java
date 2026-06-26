package com.example.med_office.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

public final class BusinessTripDTOs {

    private BusinessTripDTOs() {}

    public record BusinessTripUpsertRequest(
            @NotBlank(message = "Địa điểm công tác không được để trống")
            String destination,

            @NotNull(message = "Ngày bắt đầu không được để trống")
            LocalDate startDate,

            @NotNull(message = "Ngày kết thúc không được để trống")
            LocalDate endDate,

            @NotBlank(message = "Mục đích công tác không được để trống")
            String purpose,

            @NotBlank(message = "Người duyệt không được để trống")
            String approverId
    ) {}

    public record BusinessTripRejectRequest(
            @NotBlank(message = "Lý do từ chối không được để trống")
            String rejectReason
    ) {}

    public record BusinessTripResponse(
            String id,
            String hoSoNhanVienId,
            String employeeName,
            String employeeCode,
            String departmentName,
            String destination,
            LocalDate startDate,
            LocalDate endDate,
            String purpose,
            String status,
            String approverId,
            String approverName,
            String rejectReason,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}
}
