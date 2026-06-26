package com.example.med_office.service;

import com.example.med_office.dto.AssetHandoverDTOs.*;
import org.springframework.data.domain.Page;

public interface AssetHandoverService {
    Page<AssetHandoverResponse> getHandovers(String assetId, String type, String keyword, int page, int size);
    AssetHandoverResponse getHandoverDetail(String id);
    AssetHandoverResponse createHandover(AssetHandoverUpsertRequest request);
}
