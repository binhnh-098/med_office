package com.example.med_office.dto;

import java.util.List;

public record WarehouseInboundPageResponse(
        List<WarehouseInboundListItemResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
