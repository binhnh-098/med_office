package com.example.med_office.service;

import com.example.med_office.dto.AssetInventoryDTOs.*;
import com.example.med_office.entity.Asset;
import com.example.med_office.entity.AssetInventory;
import com.example.med_office.entity.AssetInventoryDetail;
import com.example.med_office.repository.AssetInventoryDetailRepository;
import com.example.med_office.repository.AssetInventoryRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AssetInventoryServiceImpl implements AssetInventoryService {

    private final AssetInventoryRepository assetInventoryRepository;
    private final AssetInventoryDetailRepository assetInventoryDetailRepository;
    private final AssetRepository assetRepository;

    public AssetInventoryServiceImpl(
            AssetInventoryRepository assetInventoryRepository,
            AssetInventoryDetailRepository assetInventoryDetailRepository,
            AssetRepository assetRepository) {
        this.assetInventoryRepository = assetInventoryRepository;
        this.assetInventoryDetailRepository = assetInventoryDetailRepository;
        this.assetRepository = assetRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AssetInventoryResponse> getInventories(String status, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        String cleanStatus = (status == null || status.isBlank()) ? null : status;
        String cleanKeyword = (keyword == null || keyword.isBlank()) ? null : keyword;

        return assetInventoryRepository.searchInventories(cleanStatus, cleanKeyword, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public AssetInventoryResponse getInventoryDetail(String id) {
        AssetInventory inventory = assetInventoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy thông tin kiểm kê."));
        return toResponse(inventory);
    }

    @Override
    @Transactional
    public AssetInventoryResponse saveInventory(AssetInventorySaveRequest request) {
        if (request.documentNumber() == null || request.documentNumber().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Số biên bản không được để trống.");
        }

        // Validate document number uniqueness
        Optional<AssetInventory> existingDoc = assetInventoryRepository.findByDocumentNumber(request.documentNumber().trim());
        if (existingDoc.isPresent() && (request.id() == null || !existingDoc.get().getId().equals(request.id()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Số biên bản này đã tồn tại trên hệ thống.");
        }

        AssetInventory inventory;
        if (request.id() != null && !request.id().isBlank()) {
            inventory = assetInventoryRepository.findById(request.id())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy biên bản kiểm kê cần chỉnh sửa."));
            if ("COMPLETED".equals(inventory.getStatus())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không thể chỉnh sửa biên bản kiểm kê đã hoàn thành.");
            }
            // Clear existing details first to replace them
            inventory.getDetails().clear();
            assetInventoryRepository.saveAndFlush(inventory);
        } else {
            inventory = new AssetInventory();
            inventory.setId(com.example.med_office.utils.UuidUtils.newUuid());
        }

        inventory.setDocumentNumber(request.documentNumber().trim());
        inventory.setInventoryDate(request.inventoryDate() != null ? request.inventoryDate() : LocalDate.now());
        inventory.setNotes(request.notes() != null ? request.notes().trim() : null);
        String finalStatus = "COMPLETED".equalsIgnoreCase(request.status()) ? "COMPLETED" : "DRAFT";
        inventory.setStatus(finalStatus);

        List<AssetInventoryDetail> details = new ArrayList<>();
        if (request.details() != null) {
            for (AssetInventoryDetailRequest detReq : request.details()) {
                Asset asset = assetRepository.findById(detReq.assetId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tài sản với ID: " + detReq.assetId()));

                AssetInventoryDetail detail = new AssetInventoryDetail();
                detail.setInventoryId(inventory.getId() != null ? inventory.getId() : ""); // Will be mapped by JPA
                detail.setInventory(inventory);
                detail.setAssetId(asset.getId());
                detail.setAsset(asset);
                detail.setIsPresent(detReq.isPresent() != null ? detReq.isPresent() : true);
                detail.setCurrentStatus(asset.getStatus());
                
                String actualStatus = detReq.actualStatus() != null ? detReq.actualStatus().trim().toUpperCase() : "ACTIVE";
                detail.setActualStatus(actualStatus);
                detail.setNote(detReq.note() != null ? detReq.note().trim() : null);
                
                details.add(detail);

                // If finalizing the session, synchronize actual status to the assets table
                if ("COMPLETED".equals(finalStatus)) {
                    asset.setStatus(actualStatus);
                    assetRepository.save(asset);
                }
            }
        }
        
        inventory.getDetails().clear();
        inventory.getDetails().addAll(details);
        AssetInventory saved = assetInventoryRepository.save(inventory);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteInventory(String id) {
        AssetInventory inventory = assetInventoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy biên bản kiểm kê."));
        if ("COMPLETED".equals(inventory.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không thể xóa biên bản kiểm kê đã hoàn thành.");
        }
        assetInventoryRepository.delete(inventory);
    }

    private AssetInventoryResponse toResponse(AssetInventory i) {
        List<AssetInventoryDetailResponse> detailResponses = i.getDetails().stream()
                .map(d -> new AssetInventoryDetailResponse(
                        d.getId(),
                        d.getAssetId(),
                        d.getAsset() != null ? d.getAsset().getCode() : null,
                        d.getAsset() != null ? d.getAsset().getName() : null,
                        d.getIsPresent(),
                        d.getCurrentStatus(),
                        d.getActualStatus(),
                        d.getNote()
                ))
                .collect(Collectors.toList());

        return new AssetInventoryResponse(
                i.getId(),
                i.getDocumentNumber(),
                i.getInventoryDate(),
                i.getStatus(),
                i.getNotes(),
                i.getCreatedAt(),
                i.getUpdatedAt(),
                detailResponses
        );
    }
}
