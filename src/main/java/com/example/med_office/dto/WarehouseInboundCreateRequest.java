package com.example.med_office.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record WarehouseInboundCreateRequest(
        @Valid WarehouseInboundUpsertRequest receipt,

        @Size(max = 50, message = "Ma phieu nhap khong duoc vuot qua 50 ky tu")
        String code,

        LocalDate receiptDate,

        String warehouseId,

        String supplierId,

        @Size(max = 255, message = "Ten nha cung cap khong duoc vuot qua 255 ky tu")
        String supplierName,

        @Size(max = 100, message = "So hoa don khong duoc vuot qua 100 ky tu")
        String invoiceNumber,

        @Size(max = 100, message = "Chung tu nguon khong duoc vuot qua 100 ky tu")
        String sourceDocument,

        @Size(max = 255, message = "Nguoi giao khong duoc vuot qua 255 ky tu")
        String deliveryBy,

        @Size(max = 255, message = "Nguoi nhan khong duoc vuot qua 255 ky tu")
        String receivedBy,

        @Size(max = 2000, message = "Ghi chu khong duoc vuot qua 2000 ky tu")
        String note,

        List<WarehouseInboundUpsertRequest.WarehouseInboundItemRequest> items,

        @NotNull(message = "Action khong duoc de trong")
        WarehouseInboundAction action
) {
    public WarehouseInboundUpsertRequest toUpsertRequest() {
        if (receipt != null) {
            return receipt;
        }
        return new WarehouseInboundUpsertRequest(
                code,
                receiptDate,
                warehouseId,
                supplierId,
                supplierName,
                invoiceNumber,
                sourceDocument,
                deliveryBy,
                receivedBy,
                note,
                items
        );
    }
}
