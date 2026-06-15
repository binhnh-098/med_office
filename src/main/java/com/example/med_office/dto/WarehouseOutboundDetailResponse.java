package com.example.med_office.dto;

import com.example.med_office.entity.WarehouseOutboundStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record WarehouseOutboundDetailResponse(
        String id,
        String code,
        LocalDate outboundDate,
        WarehouseOutboundStatus status,
        String warehouseId,
        String warehouseName,
        String destinationWarehouseId,
        String destinationWarehouseName,
        String destinationName,
        String receivedBy,
        String requestedBy,
        String note,
        String approvalNote,
        String rejectionReason,
        int itemCount,
        BigDecimal totalQuantity,
        List<WarehouseOutboundItemDetailResponse> items
) {
    public record WarehouseOutboundItemDetailResponse(
            String id,
            String itemId,
            String itemCode,
            String itemName,
            String unit,
            BigDecimal quantity,
            String batchNumber,
            LocalDate expiryDate,
            String note
    ) {
    }
}
