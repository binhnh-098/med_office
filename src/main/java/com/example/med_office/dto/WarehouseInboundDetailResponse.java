package com.example.med_office.dto;

import com.example.med_office.entity.WarehouseInboundStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record WarehouseInboundDetailResponse(
        String id,
        String code,
        LocalDate receiptDate,
        WarehouseInboundStatus status,
        String warehouseId,
        String warehouseName,
        String supplierId,
        String supplierName,
        String invoiceNumber,
        String sourceDocument,
        String deliveryBy,
        String receivedBy,
        String note,
        int itemCount,
        BigDecimal totalQuantity,
        BigDecimal totalValue,
        List<WarehouseInboundItemDetailResponse> items
) {
    public record WarehouseInboundItemDetailResponse(
            String id,
            String itemId,
            String itemCode,
            String itemName,
            String unit,
            BigDecimal quantity,
            BigDecimal unitPrice,
            BigDecimal lineTotal,
            String batchNumber,
            LocalDate expiryDate
    ) {
    }
}
