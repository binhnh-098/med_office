package com.example.med_office.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record WarehouseInventoryAggregateRow(
        String itemId,
        String itemCode,
        String itemName,
        String warehouseId,
        String warehouseName,
        String batchNumber,
        LocalDate expiryDate,
        String unit,
        BigDecimal quantity
) {
}