package com.example.med_office.dto;

import jakarta.validation.constraints.NotNull;

public record WarehouseStatusUpdateRequest(
        @NotNull(message = "Trạng thái hiệu lực không được để trống.")
        Boolean active
) {
}
