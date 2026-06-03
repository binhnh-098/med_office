package com.example.med_office.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record WarehouseHierarchyUpdateRequest(
        @NotNull(message = "Danh sách kho không được để trống.")
        List<@Valid WarehouseParentUpdate> warehouses
) {
    public record WarehouseParentUpdate(
            @NotNull(message = "Id kho không được để trống.")
            String id,
            String parentWarehouseId
    ) {
    }
}
