package com.example.med_office.service;

import com.example.med_office.dto.AssetLiquidationDTOs.*;
import org.springframework.data.domain.Page;

public interface AssetLiquidationService {
    Page<AssetLiquidationResponse> getLiquidations(String assetId, String keyword, int page, int size);
    AssetLiquidationResponse getLiquidationDetail(String id);
    AssetLiquidationResponse createLiquidation(AssetLiquidationRequest request);
    void cancelLiquidation(String id);
}
