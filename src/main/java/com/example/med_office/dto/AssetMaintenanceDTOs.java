package com.example.med_office.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public final class AssetMaintenanceDTOs {

    private AssetMaintenanceDTOs() {}

    public record AssetMaintenanceSendRequest(
            String assetId,
            String provider,
            BigDecimal cost,
            LocalDate maintenanceDate,
            String content,
            String notes
    ) {}

    public record AssetMaintenanceCompleteRequest(
            LocalDate completionDate,
            BigDecimal cost,
            String content,
            String notes,
            String nextStatus // e.g. ACTIVE, BROKEN, etc.
    ) {}

    public record AssetMaintenanceResponse(
            String id,
            String assetId,
            String assetCode,
            String assetName,
            String provider,
            BigDecimal cost,
            LocalDate maintenanceDate,
            LocalDate completionDate,
            String content,
            String notes,
            String status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}
}
