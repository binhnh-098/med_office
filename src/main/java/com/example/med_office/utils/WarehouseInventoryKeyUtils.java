package com.example.med_office.utils;

import java.time.LocalDate;

public final class WarehouseInventoryKeyUtils {

    private WarehouseInventoryKeyUtils() {
    }

    public static String buildKey(
            String warehouseId,
            String itemId,
            String itemCode,
            String batchNumber,
            LocalDate expiryDate,
            String unit
    ) {
        return String.join("|",
                nullToEmpty(normalize(warehouseId)),
                nullToEmpty(normalize(itemId)),
                nullToEmpty(normalize(itemCode)),
                nullToEmpty(normalize(batchNumber)),
                expiryDate == null ? "" : expiryDate.toString(),
                nullToEmpty(normalize(unit))
        );
    }

    public static String normalize(String value) {
        if (value == null) {
            return null;
        }
        String normalizedValue = value.trim();
        return normalizedValue.isBlank() ? null : normalizedValue;
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
