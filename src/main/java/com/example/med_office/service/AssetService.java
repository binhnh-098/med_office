package com.example.med_office.service;

import com.example.med_office.dto.AssetDTOs.*;
import org.springframework.data.domain.Page;

public interface AssetService {
    Page<AssetResponse> getAssets(String keyword, String category, String status, int page, int size);
    AssetResponse getAssetDetail(String id);
    AssetResponse createAsset(AssetUpsertRequest request);
    AssetResponse updateAsset(String id, AssetUpsertRequest request);
    void deleteAsset(String id);
}
