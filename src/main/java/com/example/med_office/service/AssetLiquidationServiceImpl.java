package com.example.med_office.service;

import com.example.med_office.dto.AssetLiquidationDTOs.*;
import com.example.med_office.entity.Asset;
import com.example.med_office.entity.AssetLiquidation;
import com.example.med_office.repository.AssetLiquidationRepository;
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
public class AssetLiquidationServiceImpl implements AssetLiquidationService {

    private final AssetLiquidationRepository assetLiquidationRepository;
    private final AssetRepository assetRepository;

    public AssetLiquidationServiceImpl(
            AssetLiquidationRepository assetLiquidationRepository,
            AssetRepository assetRepository) {
        this.assetLiquidationRepository = assetLiquidationRepository;
        this.assetRepository = assetRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AssetLiquidationResponse> getLiquidations(String assetId, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        String cleanAssetId = (assetId == null || assetId.isBlank()) ? null : assetId;
        String cleanKeyword = (keyword == null || keyword.isBlank()) ? null : keyword;

        return assetLiquidationRepository.searchLiquidations(cleanAssetId, cleanKeyword, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public AssetLiquidationResponse getLiquidationDetail(String id) {
        AssetLiquidation liquidation = assetLiquidationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy thông tin thanh lý."));
        return toResponse(liquidation);
    }

    @Override
    @Transactional
    public AssetLiquidationResponse createLiquidation(AssetLiquidationRequest request) {
        if (request.assetId() == null || request.assetId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID tài sản không được để trống.");
        }
        if (request.reason() == null || request.reason().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lý do thanh lý không được để trống.");
        }

        Asset asset = assetRepository.findById(request.assetId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tài sản."));

        if ("LIQUIDATED".equals(asset.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tài sản này đã được thanh lý trước đó.");
        }

        AssetLiquidation liquidation = new AssetLiquidation();
        liquidation.setAssetId(asset.getId());
        liquidation.setAsset(asset);
        liquidation.setLiquidationDate(request.liquidationDate() != null ? request.liquidationDate() : LocalDate.now());
        liquidation.setPrice(request.price());
        liquidation.setDocumentNumber(request.documentNumber() != null ? request.documentNumber().trim() : null);
        liquidation.setReason(request.reason().trim());
        liquidation.setNotes(request.notes() != null ? request.notes().trim() : null);
        liquidation.setPriorStatus(asset.getStatus());

        // Update asset status and clear assignments
        asset.setStatus("LIQUIDATED");
        asset.setCurrentEmployeeId(null);
        asset.setCurrentDepartment(null);
        assetRepository.save(asset);

        AssetLiquidation saved = assetLiquidationRepository.save(liquidation);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void cancelLiquidation(String id) {
        AssetLiquidation liquidation = assetLiquidationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy thông tin thanh lý."));

        Asset asset = assetRepository.findById(liquidation.getAssetId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tài sản tương ứng."));

        // Revert asset status to prior status
        asset.setStatus(liquidation.getPriorStatus());
        assetRepository.save(asset);

        // Delete liquidation log
        assetLiquidationRepository.delete(liquidation);
    }

    private AssetLiquidationResponse toResponse(AssetLiquidation l) {
        String assetCode = l.getAsset() != null ? l.getAsset().getCode() : null;
        String assetName = l.getAsset() != null ? l.getAsset().getName() : null;
        java.math.BigDecimal purchasePrice = l.getAsset() != null ? l.getAsset().getPurchasePrice() : null;

        return new AssetLiquidationResponse(
                l.getId(),
                l.getAssetId(),
                assetCode,
                assetName,
                purchasePrice,
                l.getLiquidationDate(),
                l.getPrice(),
                l.getDocumentNumber(),
                l.getReason(),
                l.getNotes(),
                l.getPriorStatus(),
                l.getCreatedAt(),
                l.getUpdatedAt()
        );
    }
}
