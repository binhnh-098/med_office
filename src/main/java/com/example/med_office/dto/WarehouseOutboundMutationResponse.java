package com.example.med_office.dto;

import com.example.med_office.entity.WarehouseOutboundStatus;

public record WarehouseOutboundMutationResponse(
        String id,
        String code,
        WarehouseOutboundStatus status
) {
}