package com.example.med_office.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public final class AssetInventoryDTOs {

    private AssetInventoryDTOs() {}

    public record AssetInventoryDetailRequest(
            String assetId,
            Boolean isPresent,
            String actualStatus,
            String note
    ) {}

    public record AssetInventorySaveRequest(
            String id, // nullable, populated if updating an existing draft
            String documentNumber,
            LocalDate inventoryDate,
            String status, // DRAFT, COMPLETED
            String notes,
            List<AssetInventoryDetailRequest> details
    ) {}

    public record AssetInventoryDetailResponse(
            String id,
            String assetId,
            String assetCode,
            String assetName,
            Boolean isPresent,
            String currentStatus,
            String actualStatus,
            String note
    ) {}

    public record AssetInventoryResponse(
            String id,
            String documentNumber,
            LocalDate inventoryDate,
            String status, // DRAFT, COMPLETED
            String notes,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            List<AssetInventoryDetailResponse> details
    ) {}
}
