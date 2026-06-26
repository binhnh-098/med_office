package com.example.med_office.service;

import com.example.med_office.dto.AssetInventoryDTOs.*;
import org.springframework.data.domain.Page;

public interface AssetInventoryService {
    Page<AssetInventoryResponse> getInventories(String status, String keyword, int page, int size);
    AssetInventoryResponse getInventoryDetail(String id);
    AssetInventoryResponse saveInventory(AssetInventorySaveRequest request);
    void deleteInventory(String id);
}
