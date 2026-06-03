package com.example.med_office.dto;

public record WarehouseSummaryResponse(
        long totalWarehouses,
        long activeWarehouses,
        long inactiveWarehouses,
        long totalInventoryItems
) {
}
