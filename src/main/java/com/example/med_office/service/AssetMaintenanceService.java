package com.example.med_office.service;

import com.example.med_office.dto.AssetMaintenanceDTOs.*;
import org.springframework.data.domain.Page;

public interface AssetMaintenanceService {
    Page<AssetMaintenanceResponse> getMaintenances(String assetId, String status, String keyword, int page, int size);
    AssetMaintenanceResponse getMaintenanceDetail(String id);
    AssetMaintenanceResponse createMaintenance(AssetMaintenanceSendRequest request);
    AssetMaintenanceResponse completeMaintenance(String id, AssetMaintenanceCompleteRequest request);
    AssetMaintenanceResponse cancelMaintenance(String id);
}
