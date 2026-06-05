package com.example.med_office.service;

import com.example.med_office.dto.InventoryItemOptionResponse;
import com.example.med_office.dto.SupplierOptionResponse;
import com.example.med_office.dto.WarehouseOptionResponse;
import com.example.med_office.entity.NhaCungCap;
import com.example.med_office.entity.Warehouse;
import com.example.med_office.entity.WarehouseInboundItem;
import com.example.med_office.repository.NhaCungCapRepository;
import com.example.med_office.repository.WarehouseInboundItemRepository;
import com.example.med_office.repository.WarehouseRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class CatalogOptionServiceImpl implements CatalogOptionService {

    private final WarehouseRepository warehouseRepository;
    private final NhaCungCapRepository nhaCungCapRepository;
    private final WarehouseInboundItemRepository warehouseInboundItemRepository;
    private final WarehousePermissionScopeService warehousePermissionScopeService;

    public CatalogOptionServiceImpl(
            WarehouseRepository warehouseRepository,
            NhaCungCapRepository nhaCungCapRepository,
            WarehouseInboundItemRepository warehouseInboundItemRepository,
            WarehousePermissionScopeService warehousePermissionScopeService
    ) {
        this.warehouseRepository = warehouseRepository;
        this.nhaCungCapRepository = nhaCungCapRepository;
        this.warehouseInboundItemRepository = warehouseInboundItemRepository;
        this.warehousePermissionScopeService = warehousePermissionScopeService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseOptionResponse> getWarehouseOptions() {
        List<Warehouse> warehouses = warehousePermissionScopeService.isAdmin()
                ? warehouseRepository.findAllByOrderByNameAsc()
                : findManagedWarehouses();
        return warehouses.stream()
                .map(this::toWarehouseOption)
                .toList();
    }

    private List<Warehouse> findManagedWarehouses() {
        Set<String> managedWarehouseIds = warehousePermissionScopeService.getManagedWarehouseIds();
        if (managedWarehouseIds.isEmpty()) {
            return List.of();
        }
        return warehouseRepository.findByIdInOrderByNameAsc(managedWarehouseIds);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierOptionResponse> getSupplierOptions(String keyword) {
        List<NhaCungCap> suppliers = keyword != null && !keyword.isBlank()
                ? nhaCungCapRepository.findTop20ByTenNhaCungCapContainingIgnoreCaseOrderByTenNhaCungCapAsc(keyword.trim())
                : nhaCungCapRepository.findTop20ByOrderByTenNhaCungCapAsc();
        return suppliers.stream()
                .map(this::toSupplierOption)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryItemOptionResponse> getInventoryItemOptions(String keyword) {
        String normalizedKeyword = keyword != null && !keyword.isBlank() ? keyword.trim() : null;
        return warehouseInboundItemRepository.findOptionItems(normalizedKeyword, PageRequest.of(0, 20)).stream()
                .map(this::toInventoryItemOption)
                .distinct()
                .toList();
    }

    private WarehouseOptionResponse toWarehouseOption(Warehouse warehouse) {
        return new WarehouseOptionResponse(
                warehouse.getId(),
                warehouse.getCode(),
                warehouse.getName(),
                warehouse.getStatus()
        );
    }

    private SupplierOptionResponse toSupplierOption(NhaCungCap supplier) {
        return new SupplierOptionResponse(
                supplier.getId(),
                supplier.getMaNhaCungCap(),
                supplier.getTenNhaCungCap()
        );
    }

    private InventoryItemOptionResponse toInventoryItemOption(WarehouseInboundItem item) {
        String stableId = item.getItemId() != null && !item.getItemId().isBlank()
                ? item.getItemId()
                : item.getItemCode();
        return new InventoryItemOptionResponse(
                stableId,
                item.getItemCode(),
                item.getItemName(),
                item.getUnit(),
                item.getBatchNumber() != null && !item.getBatchNumber().isBlank(),
                item.getExpiryDate() != null
        );
    }
}
