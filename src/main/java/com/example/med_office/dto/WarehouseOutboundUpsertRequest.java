package com.example.med_office.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record WarehouseOutboundUpsertRequest(
        @Schema(example = "PXK-2026-001")
        @NotBlank(message = "Ma phieu xuat khong duoc de trong")
        @Size(max = 50, message = "Ma phieu xuat khong duoc vuot qua 50 ky tu")
        String code,

        @Schema(example = "2026-06-11")
        @NotNull(message = "Ngay xuat khong duoc de trong")
        LocalDate outboundDate,

        @Schema(example = "kho-a")
        @NotBlank(message = "Kho xuat khong duoc de trong")
        String warehouseId,

        @Schema(example = "kho-b", description = "Kho nhan doi voi nghiep vu chuyen kho noi bo. Neu co gia tri, BE se tu suy ra destinationName.")
        @Size(max = 36, message = "Kho nhan khong duoc vuot qua 36 ky tu")
        String destinationWarehouseId,

        @Schema(example = "Kho Le", description = "Gia tri tuong thich nguoc cho client cu. Neu co destinationWarehouseId, gia tri nay se bi bo qua.")
        @Size(max = 255, message = "Noi nhan khong duoc vuot qua 255 ky tu")
        String destinationName,

        @Size(max = 255, message = "Nguoi nhan khong duoc vuot qua 255 ky tu")
        String receivedBy,

        @Size(max = 255, message = "Nguoi yeu cau khong duoc vuot qua 255 ky tu")
        String requestedBy,

        @Size(max = 2000, message = "Ghi chu khong duoc vuot qua 2000 ky tu")
        String note,

        @NotEmpty(message = "Phieu xuat kho phai co it nhat 1 dong vat tu")
        List<@Valid WarehouseOutboundItemRequest> items
) {
    public record WarehouseOutboundItemRequest(
            String itemId,
            String itemCode,

            @NotBlank(message = "Ten vat tu khong duoc de trong")
            @Size(max = 255, message = "Ten vat tu khong duoc vuot qua 255 ky tu")
            String itemName,

            @NotNull(message = "So luong khong duoc de trong")
            @DecimalMin(value = "0.01", message = "So luong phai lon hon 0")
            BigDecimal quantity,

            @Size(max = 100, message = "Don vi tinh khong duoc vuot qua 100 ky tu")
            String unit,

            @Size(max = 100, message = "So lo khong duoc vuot qua 100 ky tu")
            String batchNumber,

            LocalDate expiryDate,

            @Size(max = 1000, message = "Ghi chu dong vat tu khong duoc vuot qua 1000 ky tu")
            String note
    ) {
    }
}
