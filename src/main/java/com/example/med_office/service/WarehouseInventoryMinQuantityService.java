package com.example.med_office.service;

import com.example.med_office.entity.WarehouseInboundItem;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

public interface WarehouseInventoryMinQuantityService {

    Map<String, BigDecimal> getMinQuantitiesByInventoryKeys(Collection<String> inventoryKeys);

    void upsertFromInbound(String warehouseId, Collection<WarehouseInboundItem> items);
}
