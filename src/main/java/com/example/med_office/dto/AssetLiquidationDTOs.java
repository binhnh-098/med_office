package com.example.med_office.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public final class AssetLiquidationDTOs {

    private AssetLiquidationDTOs() {}

    public record AssetLiquidationRequest(
            String assetId,
            LocalDate liquidationDate,
            BigDecimal price,
            String documentNumber,
            String reason,
            String notes
    ) {}

    public record AssetLiquidationResponse(
            String id,
            String assetId,
            String assetCode,
            String assetName,
            BigDecimal purchasePrice,
            LocalDate liquidationDate,
            BigDecimal price,
            String documentNumber,
            String reason,
            String notes,
            String priorStatus,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}
}
