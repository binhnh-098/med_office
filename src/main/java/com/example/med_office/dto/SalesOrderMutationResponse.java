package com.example.med_office.dto;

import com.example.med_office.entity.SalesOrderStatus;

public record SalesOrderMutationResponse(
        String id,
        String code,
        SalesOrderStatus status
) {
}
