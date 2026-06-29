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

public record SalesOrderUpsertRequest(
        @Schema(example = "DH-2026-001")
        @Size(max = 50, message = "Ma don hang khong duoc vuot qua 50 ky tu")
        String code,

        @Schema(example = "2026-06-11")
        @NotNull(message = "Ngay don hang khong duoc de trong")
        LocalDate orderDate,

        @Schema(example = "kho-a")
        @NotBlank(message = "Kho hang khong duoc de trong")
        String warehouseId,

        @Size(max = 255, message = "Ten khach hang khong duoc vuot qua 255 ky tu")
        String buyerName,

        @Size(max = 50, message = "Ma so thue khong duoc vuot qua 50 ky tu")
        String taxCode,

        @Size(max = 255, message = "Ten don vi khong duoc vuot qua 255 ky tu")
        String buyerCompany,

        @Size(max = 500, message = "Dia chi khong duoc vuot qua 500 ky tu")
        String buyerAddress,

        @Size(max = 255, message = "Email khong duoc vuot qua 255 ky tu")
        String buyerEmail,

        @Size(max = 50, message = "Phuong thuc thanh toan khong duoc vuot qua 50 ky tu")
        String paymentMethod,

        @NotBlank(message = "Trang thai thanh toan khong duoc de trong")
        String paymentStatus,

        @Size(max = 2000, message = "Ghi chu khong duoc vuot qua 2000 ky tu")
        String note,

        @NotEmpty(message = "Don hang phai co it nhat 1 mat hang")
        List<@Valid SalesOrderItemRequest> items
) {
    public record SalesOrderItemRequest(
            String itemId,
            String itemCode,

            @NotBlank(message = "Ten mat hang khong duoc de trong")
            @Size(max = 255, message = "Ten mat hang khong duoc vuot qua 255 ky tu")
            String itemName,

            @NotNull(message = "So luong khong duoc de trong")
            @DecimalMin(value = "0.01", message = "So luong phai lon hon 0")
            BigDecimal quantity,

            @NotNull(message = "Don gia khong duoc de trong")
            @DecimalMin(value = "0.00", message = "Don gia khong duoc am")
            BigDecimal unitPrice,

            BigDecimal vatRate,

            @Size(max = 100, message = "Don vi tinh khong duoc vuot qua 100 ky tu")
            String unit,

            @Size(max = 100, message = "So lo khong duoc vuot qua 100 ky tu")
            String batchNumber,

            LocalDate expiryDate,

            @Size(max = 1000, message = "Ghi chu mat hang khong duoc vuot qua 1000 ky tu")
            String note
    ) {
    }
}
