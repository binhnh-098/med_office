package com.example.med_office.dto;

import java.util.List;

public record SalesOrderPageResponse(
        List<SalesOrderListItemResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
