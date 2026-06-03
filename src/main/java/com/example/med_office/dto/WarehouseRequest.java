package com.example.med_office.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record WarehouseRequest(
        @NotBlank(message = "Mã kho không được để trống.")
        @Size(max = 50, message = "Mã kho không được vượt quá 50 ký tự.")
        String code,

        @NotBlank(message = "Tên kho không được để trống.")
        @Size(max = 255, message = "Tên kho không được vượt quá 255 ký tự.")
        String name,

        @Size(max = 50, message = "Loại kho không được vượt quá 50 ký tự.")
        String type,

        @NotBlank(message = "Vị trí kho không được để trống.")
        @Size(max = 500, message = "Vị trí kho không được vượt quá 500 ký tự.")
        String location,

        @Size(max = 2000, message = "Ghi chú không được vượt quá 2000 ký tự.")
        String note,

        Boolean active,

        List<String> managerIds,

        String parentWarehouseId
) {
}
