package com.example.med_office.service;

import com.example.med_office.dto.WarehouseInboundApprovalRequest;
import com.example.med_office.dto.WarehouseInboundCreateRequest;
import com.example.med_office.dto.WarehouseInboundDetailResponse;
import com.example.med_office.dto.WarehouseInboundMutationResponse;
import com.example.med_office.dto.WarehouseInboundPageResponse;
import com.example.med_office.dto.WarehouseInboundRejectRequest;
import com.example.med_office.dto.WarehouseInboundUpsertRequest;

import java.time.LocalDate;

public interface WarehouseInboundService {

    WarehouseInboundPageResponse findAll(
            int page,
            int size,
            String keyword,
            String status,
            String warehouseId,
            LocalDate fromDate,
            LocalDate toDate
    );

    WarehouseInboundDetailResponse findById(String id);

    WarehouseInboundMutationResponse create(WarehouseInboundCreateRequest request);

    WarehouseInboundMutationResponse updateDraft(String id, WarehouseInboundUpsertRequest request);

    WarehouseInboundMutationResponse submit(String id);

    WarehouseInboundMutationResponse approve(String id, WarehouseInboundApprovalRequest request);

    WarehouseInboundMutationResponse reject(String id, WarehouseInboundRejectRequest request);

    WarehouseInboundMutationResponse complete(String id);
}
