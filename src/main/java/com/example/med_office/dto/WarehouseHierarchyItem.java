package com.example.med_office.dto;

import java.util.List;

public record WarehouseHierarchyItem(
        String id,
        String code,
        String name,
        String parentWarehouseId,
        String status,
        List<WarehouseManagerResponse> managers,
        List<WarehouseHierarchyItem> children
) {
}
