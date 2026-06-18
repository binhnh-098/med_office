package com.example.med_office.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record WarehouseInboundUpsertRequest(
        @Size(max = 50, message = "Ma phieu nhap khong duoc vuot qua 50 ky tu")
        String code,


        @NotNull(message = "Ngay nhap khong duoc de trong")
        LocalDate receiptDate,

        @NotBlank(message = "Kho nhap khong duoc de trong")
        String warehouseId,

        String supplierId,

        @Size(max = 255, message = "Ten nha cung cap khong duoc vuot qua 255 ky tu")
        String supplierName,

        String sourceWarehouseId,

        @Size(max = 255, message = "Ten kho xuat khong duoc vuot qua 255 ky tu")
        String sourceWarehouseName,

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

        @NotEmpty(message = "Phieu nhap kho phai co it nhat 1 dong vat tu")
        List<@Valid WarehouseInboundItemRequest> items
) {
    public record WarehouseInboundItemRequest(
            String itemId,
            String itemCode,

            @NotBlank(message = "Ten vat tu khong duoc de trong")
            @Size(max = 255, message = "Ten vat tu khong duoc vuot qua 255 ky tu")
            String itemName,

            @Size(max = 100, message = "Don vi tinh khong duoc vuot qua 100 ky tu")
            String unit,

            @NotNull(message = "So luong khong duoc de trong")
            @DecimalMin(value = "0.01", message = "So luong phai lon hon 0")
            BigDecimal quantity,

            @NotNull(message = "Don gia khong duoc de trong")
            @DecimalMin(value = "0", inclusive = true, message = "Don gia phai lon hon hoac bang 0")
            BigDecimal unitPrice,

            @Size(max = 100, message = "So lo khong duoc vuot qua 100 ky tu")
            String batchNumber,

            LocalDate expiryDate,

            @DecimalMin(value = "0", inclusive = true, message = "So ton toi thieu phai lon hon hoac bang 0")
            BigDecimal minQuantity
    ) {
    }
}
