package com.example.med_office.dto;

import com.example.med_office.entity.SalesOrderStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record SalesOrderDetailResponse(
        String id,
        String code,
        LocalDate orderDate,
        SalesOrderStatus status,
        String warehouseId,
        String warehouseName,
        String buyerName,
        String taxCode,
        String buyerCompany,
        String buyerAddress,
        String buyerEmail,
        String paymentMethod,
        String paymentStatus,
        BigDecimal totalAmountBeforeTax,
        BigDecimal totalTaxAmount,
        BigDecimal totalAmountAfterTax,
        String eInvoiceStatus,
        String eInvoiceNumber,
        String eInvoiceLookupCode,
        String eInvoiceUrl,
        String eInvoiceErrorMessage,
        String note,
        String warehouseOutboundId,
        List<SalesOrderItemDetailResponse> items
) {
    public record SalesOrderItemDetailResponse(
            String id,
            String itemId,
            String itemCode,
            String itemName,
            String unit,
            BigDecimal quantity,
            BigDecimal unitPrice,
            BigDecimal lineTotalBeforeTax,
            BigDecimal vatRate,
            BigDecimal taxAmount,
            BigDecimal lineTotalAfterTax,
            String batchNumber,
            LocalDate expiryDate,
            String note
    ) {
    }
}
