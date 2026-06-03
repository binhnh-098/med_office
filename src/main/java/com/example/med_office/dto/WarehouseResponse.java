package com.example.med_office.dto;

import java.time.Instant;
import java.util.List;

public record WarehouseResponse(
        String id,
        String code,
        String name,
        String type,
        String location,
        String note,
        String status,
        long itemCount,
        String parentWarehouseId,
        List<WarehouseManagerResponse> managers,
        Instant createdAt,
        Instant updatedAt
) {
}
