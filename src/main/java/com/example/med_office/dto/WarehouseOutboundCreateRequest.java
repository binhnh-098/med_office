package com.example.med_office.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record WarehouseOutboundCreateRequest(
        @Valid WarehouseOutboundUpsertRequest outbound,

        @Schema(example = "PXK-2026-001")
        @Size(max = 50, message = "Ma phieu xuat khong duoc vuot qua 50 ky tu")
        String code,

        @Schema(example = "2026-06-11")
        LocalDate outboundDate,

        @Schema(example = "kho-a")
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

        List<WarehouseOutboundUpsertRequest.WarehouseOutboundItemRequest> items,

        @NotNull(message = "Action khong duoc de trong")
        WarehouseOutboundAction action
) {
    public WarehouseOutboundUpsertRequest toUpsertRequest() {
        if (outbound != null) {
            return outbound;
        }
        return new WarehouseOutboundUpsertRequest(
                code,
                outboundDate,
                warehouseId,
                destinationWarehouseId,
                destinationName,
                receivedBy,
                requestedBy,
                note,
                items
        );
    }
}
