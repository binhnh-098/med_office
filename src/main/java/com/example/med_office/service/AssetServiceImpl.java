package com.example.med_office.service;

import com.example.med_office.dto.AssetDTOs.*;
import com.example.med_office.entity.Asset;
import com.example.med_office.repository.AssetRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AssetServiceImpl implements AssetService {

    private final AssetRepository assetRepository;

    public AssetServiceImpl(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AssetResponse> getAssets(String keyword, String category, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        String cleanKeyword = (keyword == null || keyword.isBlank()) ? null : keyword;
        String cleanCategory = (category == null || category.isBlank()) ? null : category;
        String cleanStatus = (status == null || status.isBlank()) ? null : status;

        return assetRepository.searchAssets(cleanKeyword, cleanCategory, cleanStatus, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public AssetResponse getAssetDetail(String id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tài sản."));
        return toResponse(asset);
    }

    @Override
    @Transactional
    public AssetResponse createAsset(AssetUpsertRequest request) {
        validateRequest(request);

        if (assetRepository.findByCode(request.code()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã tài sản đã tồn tại trong hệ thống.");
        }

        Asset asset = new Asset();
        copyProperties(request, asset);

        Asset saved = assetRepository.save(asset);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public AssetResponse updateAsset(String id, AssetUpsertRequest request) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tài sản."));

        validateRequest(request);

        assetRepository.findByCode(request.code()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã tài sản đã tồn tại trong hệ thống.");
            }
        });

        copyProperties(request, asset);

        Asset saved = assetRepository.save(asset);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteAsset(String id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tài sản."));
        assetRepository.delete(asset);
    }

    private void validateRequest(AssetUpsertRequest request) {
        if (request.code() == null || request.code().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã tài sản không được để trống.");
        }
        if (request.name() == null || request.name().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên tài sản không được để trống.");
        }
        if (request.category() == null || request.category().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nhóm tài sản không được để trống.");
        }
        if (request.unit() == null || request.unit().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Đơn vị tính không được để trống.");
        }
        if (request.status() == null || request.status().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trạng thái không được để trống.");
        }
    }

    private void copyProperties(AssetUpsertRequest request, Asset asset) {
        asset.setCode(request.code().trim());
        asset.setName(request.name().trim());
        asset.setCategory(request.category().trim());
        asset.setUnit(request.unit().trim());
        asset.setModel(request.model() != null ? request.model().trim() : null);
        asset.setSerialNumber(request.serialNumber() != null ? request.serialNumber().trim() : null);
        asset.setBrand(request.brand() != null ? request.brand().trim() : null);
        asset.setManufacturer(request.manufacturer() != null ? request.manufacturer().trim() : null);
        asset.setImage(request.image());
        asset.setSpecification(request.specification() != null ? request.specification().trim() : null);
        asset.setPurchasePrice(request.purchasePrice());
        asset.setPurchaseDate(request.purchaseDate());
        asset.setStatus(request.status().trim());
        asset.setDescription(request.description() != null ? request.description().trim() : null);
    }

    private AssetResponse toResponse(Asset asset) {
        String currentEmployeeName = asset.getCurrentEmployee() != null ? asset.getCurrentEmployee().getName() : null;
        return new AssetResponse(
                asset.getId(),
                asset.getCode(),
                asset.getName(),
                asset.getCategory(),
                asset.getUnit(),
                asset.getModel(),
                asset.getSerialNumber(),
                asset.getBrand(),
                asset.getManufacturer(),
                asset.getImage(),
                asset.getSpecification(),
                asset.getPurchasePrice(),
                asset.getPurchaseDate(),
                asset.getStatus(),
                asset.getCurrentEmployeeId(),
                currentEmployeeName,
                asset.getCurrentDepartment(),
                asset.getDescription(),
                asset.getCreatedAt(),
                asset.getUpdatedAt()
        );
    }
}
