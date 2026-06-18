package com.example.med_office.service;

import com.example.med_office.dto.WarehouseOutboundApprovalRequest;
import com.example.med_office.dto.WarehouseOutboundCreateRequest;
import com.example.med_office.dto.WarehouseOutboundDetailResponse;
import com.example.med_office.dto.WarehouseOutboundMutationResponse;
import com.example.med_office.dto.WarehouseOutboundPageResponse;
import com.example.med_office.dto.WarehouseOutboundRejectRequest;
import com.example.med_office.dto.WarehouseOutboundUpsertRequest;

import java.time.LocalDate;

public interface WarehouseOutboundService {

    WarehouseOutboundPageResponse findAll(
            int page,
            int size,
            String keyword,
            String status,
            String warehouseId,
            String destinationWarehouseId,
            LocalDate fromDate,
            LocalDate toDate
    );

    WarehouseOutboundDetailResponse findById(String id);

    WarehouseOutboundMutationResponse create(WarehouseOutboundCreateRequest request);

    WarehouseOutboundMutationResponse updateDraft(String id, WarehouseOutboundUpsertRequest request);

    WarehouseOutboundMutationResponse submit(String id);

    WarehouseOutboundMutationResponse approve(String id, WarehouseOutboundApprovalRequest request);

    WarehouseOutboundMutationResponse reject(String id, WarehouseOutboundRejectRequest request);

    WarehouseOutboundMutationResponse complete(String id);

    void delete(String id);
}
