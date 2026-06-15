package com.example.med_office.service;

import com.example.med_office.entity.WarehouseInboundItem;
import com.example.med_office.entity.WarehouseInventoryMinQuantity;
import com.example.med_office.repository.WarehouseInventoryMinQuantityRepository;
import com.example.med_office.utils.WarehouseInventoryKeyUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class WarehouseInventoryMinQuantityServiceImpl implements WarehouseInventoryMinQuantityService {

    private final WarehouseInventoryMinQuantityRepository warehouseInventoryMinQuantityRepository;

    public WarehouseInventoryMinQuantityServiceImpl(
            WarehouseInventoryMinQuantityRepository warehouseInventoryMinQuantityRepository
    ) {
        this.warehouseInventoryMinQuantityRepository = warehouseInventoryMinQuantityRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> getMinQuantitiesByInventoryKeys(Collection<String> inventoryKeys) {
        if (inventoryKeys == null || inventoryKeys.isEmpty()) {
            return Map.of();
        }

        Map<String, BigDecimal> minQuantityByKey = new LinkedHashMap<>();
        List<WarehouseInventoryMinQuantity> savedMinQuantities =
                warehouseInventoryMinQuantityRepository.findByInventoryKeyIn(inventoryKeys);
        for (WarehouseInventoryMinQuantity savedMinQuantity : savedMinQuantities) {
            minQuantityByKey.put(savedMinQuantity.getInventoryKey(), defaultDecimal(savedMinQuantity.getMinQuantity()));
        }
        return minQuantityByKey;
    }

    @Override
    @Transactional
    public void upsertFromInbound(String warehouseId, Collection<WarehouseInboundItem> items) {
        if (warehouseId == null || warehouseId.isBlank() || items == null || items.isEmpty()) {
            return;
        }

        for (WarehouseInboundItem item : items) {
            String inventoryKey = WarehouseInventoryKeyUtils.buildKey(
                    warehouseId,
                    item.getItemId(),
                    item.getItemCode(),
                    item.getBatchNumber(),
                    item.getExpiryDate(),
                    item.getUnit()
            );
            WarehouseInventoryMinQuantity inventoryMinQuantity = warehouseInventoryMinQuantityRepository
                    .findByInventoryKey(inventoryKey)
                    .orElseGet(WarehouseInventoryMinQuantity::new);

            inventoryMinQuantity.setInventoryKey(inventoryKey);
            inventoryMinQuantity.setWarehouseId(WarehouseInventoryKeyUtils.normalize(warehouseId));
            inventoryMinQuantity.setItemId(WarehouseInventoryKeyUtils.normalize(item.getItemId()));
            inventoryMinQuantity.setItemCode(WarehouseInventoryKeyUtils.normalize(item.getItemCode()));
            inventoryMinQuantity.setBatchNumber(WarehouseInventoryKeyUtils.normalize(item.getBatchNumber()));
            inventoryMinQuantity.setExpiryDate(item.getExpiryDate());
            inventoryMinQuantity.setUnit(WarehouseInventoryKeyUtils.normalize(item.getUnit()));
            inventoryMinQuantity.setMinQuantity(normalizeDecimal(item.getMinQuantity()));
            warehouseInventoryMinQuantityRepository.save(inventoryMinQuantity);
        }
    }

    private BigDecimal normalizeDecimal(BigDecimal value) {
        if (value == null) {
            return null;
        }
        return value.stripTrailingZeros();
    }

    private BigDecimal defaultDecimal(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value.stripTrailingZeros();
    }
}
