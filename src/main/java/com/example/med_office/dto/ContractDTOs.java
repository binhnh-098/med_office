package com.example.med_office.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public final class ContractDTOs {

    private ContractDTOs() {}

    public record ContractUpsertRequest(
            @NotBlank(message = "Hồ sơ nhân sự không được để trống")
            String hoSoNhanVienId,

            @NotBlank(message = "Số hợp đồng không được để trống")
            String contractNumber,

            @NotBlank(message = "Loại hợp đồng không được để trống")
            String contractType,

            @NotNull(message = "Ngày bắt đầu không được để trống")
            LocalDate startDate,

            LocalDate endDate,

            @NotNull(message = "Mức lương không được để trống")
            @PositiveOrZero(message = "Mức lương không được là số âm")
            BigDecimal salary,

            String status,

            String note
    ) {}

    public record ContractResponse(
            String id,
            String hoSoNhanVienId,
            String employeeName,
            String employeeCode,
            String contractNumber,
            String contractType,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal salary,
            String status,
            String note,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}
}
