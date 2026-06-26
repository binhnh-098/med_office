package com.example.med_office.service;

import com.example.med_office.dto.AssetMaintenanceDTOs.*;
import com.example.med_office.entity.Asset;
import com.example.med_office.entity.AssetMaintenance;
import com.example.med_office.repository.AssetMaintenanceRepository;
import com.example.med_office.repository.AssetRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Service
public class AssetMaintenanceServiceImpl implements AssetMaintenanceService {

    private final AssetMaintenanceRepository assetMaintenanceRepository;
    private final AssetRepository assetRepository;

    public AssetMaintenanceServiceImpl(
            AssetMaintenanceRepository assetMaintenanceRepository,
            AssetRepository assetRepository) {
        this.assetMaintenanceRepository = assetMaintenanceRepository;
        this.assetRepository = assetRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AssetMaintenanceResponse> getMaintenances(String assetId, String status, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        String cleanAssetId = (assetId == null || assetId.isBlank()) ? null : assetId;
        String cleanStatus = (status == null || status.isBlank()) ? null : status;
        String cleanKeyword = (keyword == null || keyword.isBlank()) ? null : keyword;

        return assetMaintenanceRepository.searchMaintenances(cleanAssetId, cleanStatus, cleanKeyword, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public AssetMaintenanceResponse getMaintenanceDetail(String id) {
        AssetMaintenance maintenance = assetMaintenanceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy thông tin bảo trì."));
        return toResponse(maintenance);
    }

    @Override
    @Transactional
    public AssetMaintenanceResponse createMaintenance(AssetMaintenanceSendRequest request) {
        if (request.assetId() == null || request.assetId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID tài sản không được để trống.");
        }

        Asset asset = assetRepository.findById(request.assetId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tài sản."));

        if ("MAINTENANCE".equals(asset.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tài sản hiện đang trong quá trình bảo trì.");
        }

        AssetMaintenance maintenance = new AssetMaintenance();
        maintenance.setAssetId(asset.getId());
        maintenance.setAsset(asset);
        maintenance.setProvider(request.provider() != null ? request.provider().trim() : null);
        maintenance.setCost(request.cost());
        maintenance.setMaintenanceDate(request.maintenanceDate() != null ? request.maintenanceDate() : LocalDate.now());
        maintenance.setContent(request.content() != null ? request.content().trim() : null);
        maintenance.setNotes(request.notes() != null ? request.notes().trim() : null);
        maintenance.setStatus("UNDER_MAINTENANCE");

        // Transition asset state
        asset.setStatus("MAINTENANCE");
        assetRepository.save(asset);

        AssetMaintenance saved = assetMaintenanceRepository.save(maintenance);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public AssetMaintenanceResponse completeMaintenance(String id, AssetMaintenanceCompleteRequest request) {
        AssetMaintenance maintenance = assetMaintenanceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy thông tin bảo trì."));

        if (!"UNDER_MAINTENANCE".equals(maintenance.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Giao dịch bảo trì này không ở trạng thái đang sửa chữa.");
        }

        maintenance.setCompletionDate(request.completionDate() != null ? request.completionDate() : LocalDate.now());
        if (request.cost() != null) {
            maintenance.setCost(request.cost());
        }
        if (request.content() != null) {
            maintenance.setContent(request.content().trim());
        }
        if (request.notes() != null) {
            maintenance.setNotes(request.notes().trim());
        }
        maintenance.setStatus("COMPLETED");

        Asset asset = assetRepository.findById(maintenance.getAssetId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tài sản tương ứng."));

        // Transition asset state back
        String nextAssetStatus = request.nextStatus() != null ? request.nextStatus().trim().toUpperCase() : "ACTIVE";
        asset.setStatus(nextAssetStatus);
        assetRepository.save(asset);

        AssetMaintenance saved = assetMaintenanceRepository.save(maintenance);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public AssetMaintenanceResponse cancelMaintenance(String id) {
        AssetMaintenance maintenance = assetMaintenanceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy thông tin bảo trì."));

        if (!"UNDER_MAINTENANCE".equals(maintenance.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ có thể hủy giao dịch bảo trì đang sửa chữa.");
        }

        maintenance.setStatus("CANCELLED");

        Asset asset = assetRepository.findById(maintenance.getAssetId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tài sản tương ứng."));

        // Reset asset status back to ACTIVE (or let it be ACTIVE by default)
        asset.setStatus("ACTIVE");
        assetRepository.save(asset);

        AssetMaintenance saved = assetMaintenanceRepository.save(maintenance);
        return toResponse(saved);
    }

    private AssetMaintenanceResponse toResponse(AssetMaintenance m) {
        String assetCode = m.getAsset() != null ? m.getAsset().getCode() : null;
        String assetName = m.getAsset() != null ? m.getAsset().getName() : null;

        return new AssetMaintenanceResponse(
                m.getId(),
                m.getAssetId(),
                assetCode,
                assetName,
                m.getProvider(),
                m.getCost(),
                m.getMaintenanceDate(),
                m.getCompletionDate(),
                m.getContent(),
                m.getNotes(),
                m.getStatus(),
                m.getCreatedAt(),
                m.getUpdatedAt()
        );
    }
}
