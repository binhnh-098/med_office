package com.example.med_office.dto;

import java.util.List;

public record WarehouseOutboundPageResponse(
        List<WarehouseOutboundListItemResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}