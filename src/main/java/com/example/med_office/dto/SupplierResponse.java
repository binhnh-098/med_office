package com.example.med_office.dto;

import java.time.LocalDateTime;

public record SupplierResponse(
        String id,
        String code,
        String name,
        String status,
        LocalDateTime createdAt
) {
}
