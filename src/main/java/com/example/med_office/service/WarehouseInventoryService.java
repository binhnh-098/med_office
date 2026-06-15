package com.example.med_office.service;

import com.example.med_office.dto.WarehouseInventoryPageResponse;
import com.example.med_office.dto.WarehouseOutboundUpsertRequest;

import java.time.LocalDate;
import java.util.List;

public interface WarehouseInventoryService {

    WarehouseInventoryPageResponse findAll(int page, int size, String keyword, String warehouseId);

    void assertSufficientAvailability(
            String warehouseId,
            List<WarehouseOutboundUpsertRequest.WarehouseOutboundItemRequest> items,
            LocalDate outboundDate
    );

    void assertSufficientAvailability(
            String warehouseId,
            List<WarehouseOutboundUpsertRequest.WarehouseOutboundItemRequest> items,
            LocalDate outboundDate,
            List<WarehouseOutboundUpsertRequest.WarehouseOutboundItemRequest> releasedReservedItems
    );
}
