package com.example.med_office.dto;

import com.example.med_office.entity.WarehouseOutboundStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record WarehouseOutboundListItemResponse(
        String id,
        String code,
        Instant outboundDate,
        WarehouseOutboundStatus status,
        String warehouseId,
        String warehouseName,
        String destinationWarehouseId,
        String destinationWarehouseName,
        String destinationName,
        String receivedBy,
        String requestedBy,
        int itemCount,
        BigDecimal totalQuantity
) {
}
