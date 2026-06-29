package com.example.med_office.dto;

import com.example.med_office.entity.SalesOrderStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record SalesOrderListItemResponse(
        String id,
        String code,
        Instant orderDate,
        SalesOrderStatus status,
        String warehouseId,
        String warehouseName,
        String buyerCompany,
        String buyerName,
        String paymentStatus,
        BigDecimal totalAmountAfterTax,
        String eInvoiceStatus
) {
}
