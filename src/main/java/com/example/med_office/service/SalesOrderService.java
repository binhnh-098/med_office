package com.example.med_office.service;

import com.example.med_office.dto.SalesOrderDetailResponse;
import com.example.med_office.dto.SalesOrderMutationResponse;
import com.example.med_office.dto.SalesOrderPageResponse;
import com.example.med_office.dto.SalesOrderUpsertRequest;

import java.time.LocalDate;

public interface SalesOrderService {

    SalesOrderPageResponse findAll(
            int page,
            int size,
            String keyword,
            String status,
            String warehouseId,
            String paymentStatus,
            LocalDate fromDate,
            LocalDate toDate
    );

    SalesOrderDetailResponse findById(String id);

    SalesOrderMutationResponse create(SalesOrderUpsertRequest request);

    SalesOrderMutationResponse updateDraft(String id, SalesOrderUpsertRequest request);

    SalesOrderMutationResponse complete(String id);

    SalesOrderMutationResponse issueInvoiceManually(String id);

    void delete(String id);
}
