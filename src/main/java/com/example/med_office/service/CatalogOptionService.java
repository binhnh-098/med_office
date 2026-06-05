package com.example.med_office.service;

import com.example.med_office.dto.InventoryItemOptionResponse;
import com.example.med_office.dto.SupplierOptionResponse;
import com.example.med_office.dto.WarehouseOptionResponse;

import java.util.List;

public interface CatalogOptionService {

    List<WarehouseOptionResponse> getWarehouseOptions();

    List<SupplierOptionResponse> getSupplierOptions(String keyword);

    List<InventoryItemOptionResponse> getInventoryItemOptions(String keyword);
}
