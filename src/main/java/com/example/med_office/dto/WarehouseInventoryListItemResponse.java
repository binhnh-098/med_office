package com.example.med_office.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record WarehouseInventoryListItemResponse(
        String id,
        String itemCode,
        String itemName,
        String warehouseId,
        String warehouseName,
        String batchNumber,
        LocalDate expiryDate,
        String unit,
        BigDecimal availableQuantity,
        BigDecimal reservedQuantity,
        BigDecimal totalQuantity,
        BigDecimal minQuantity
) {
}