package com.example.med_office.dto;

import java.util.List;

public record WarehouseInventoryPageResponse(
        List<WarehouseInventoryListItemResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}