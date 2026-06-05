package com.example.med_office.dto;

public record InventoryItemOptionResponse(
        String id,
        String code,
        String name,
        String unit,
        boolean requiresBatchControl,
        boolean requiresExpiryDate
) {
}
