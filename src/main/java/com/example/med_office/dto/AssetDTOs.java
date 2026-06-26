package com.example.med_office.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public final class AssetDTOs {

    private AssetDTOs() {}

    public record AssetUpsertRequest(
            String code,
            String name,
            String category,
            String unit,
            String model,
            String serialNumber,
            String brand,
            String manufacturer,
            String image,
            String specification,
            BigDecimal purchasePrice,
            LocalDate purchaseDate,
            String status,
            String description
    ) {}

    public record AssetResponse(
            String id,
            String code,
            String name,
            String category,
            String unit,
            String model,
            String serialNumber,
            String brand,
            String manufacturer,
            String image,
            String specification,
            BigDecimal purchasePrice,
            LocalDate purchaseDate,
            String status,
            String currentEmployeeId,
            String currentEmployeeName,
            String currentDepartment,
            String description,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}
}
