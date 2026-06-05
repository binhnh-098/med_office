package com.example.med_office.dto;

import com.example.med_office.entity.WarehouseInboundStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record WarehouseInboundListItemResponse(
        String id,
        String code,
        Instant receiptDate,
        WarehouseInboundStatus status,
        String warehouseId,
        String warehouseName,
        String supplierId,
        String supplierName,
        int itemCount,
        BigDecimal totalQuantity,
        BigDecimal totalValue
) {
}
