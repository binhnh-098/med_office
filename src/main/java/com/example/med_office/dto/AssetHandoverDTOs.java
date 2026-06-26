package com.example.med_office.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class AssetHandoverDTOs {

    private AssetHandoverDTOs() {}

    public record AssetHandoverUpsertRequest(
            String assetId,
            String type, // HANDOVER, TRANSFER, RECLAIM
            String toEmployeeId,
            String toDepartment,
            LocalDate handoverDate,
            String documentNumber,
            String note
    ) {}

    public record AssetHandoverResponse(
            String id,
            String assetId,
            String assetCode,
            String assetName,
            String type,
            String fromEmployeeId,
            String fromEmployeeName,
            String toEmployeeId,
            String toEmployeeName,
            String fromDepartment,
            String toDepartment,
            LocalDate handoverDate,
            String documentNumber,
            String note,
            String status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}
}
