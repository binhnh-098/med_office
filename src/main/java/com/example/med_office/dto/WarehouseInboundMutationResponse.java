package com.example.med_office.dto;

import com.example.med_office.entity.WarehouseInboundStatus;

public record WarehouseInboundMutationResponse(
        String id,
        String code,
        WarehouseInboundStatus status
) {
}
