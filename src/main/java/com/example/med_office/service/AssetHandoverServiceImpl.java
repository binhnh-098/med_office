package com.example.med_office.service;

import com.example.med_office.dto.AssetHandoverDTOs.*;
import com.example.med_office.entity.Asset;
import com.example.med_office.entity.AssetHandover;
import com.example.med_office.entity.HoSoNhanVien;
import com.example.med_office.repository.AssetHandoverRepository;
import com.example.med_office.repository.AssetRepository;
import com.example.med_office.repository.HoSoNhanVienRepository;
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
public class AssetHandoverServiceImpl implements AssetHandoverService {

    private final AssetHandoverRepository assetHandoverRepository;
    private final AssetRepository assetRepository;
    private final HoSoNhanVienRepository hoSoNhanVienRepository;

    public AssetHandoverServiceImpl(
            AssetHandoverRepository assetHandoverRepository,
            AssetRepository assetRepository,
            HoSoNhanVienRepository hoSoNhanVienRepository) {
        this.assetHandoverRepository = assetHandoverRepository;
        this.assetRepository = assetRepository;
        this.hoSoNhanVienRepository = hoSoNhanVienRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AssetHandoverResponse> getHandovers(String assetId, String type, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        String cleanAssetId = (assetId == null || assetId.isBlank()) ? null : assetId;
        String cleanType = (type == null || type.isBlank()) ? null : type;
        String cleanKeyword = (keyword == null || keyword.isBlank()) ? null : keyword;

        return assetHandoverRepository.searchHandovers(cleanAssetId, cleanType, cleanKeyword, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public AssetHandoverResponse getHandoverDetail(String id) {
        AssetHandover handover = assetHandoverRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy thông tin bàn giao/điều chuyển."));
        return toResponse(handover);
    }

    @Override
    @Transactional
    public AssetHandoverResponse createHandover(AssetHandoverUpsertRequest request) {
        validateRequest(request);

        Asset asset = assetRepository.findById(request.assetId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tài sản."));

        AssetHandover handover = new AssetHandover();
        handover.setAssetId(asset.getId());
        handover.setAsset(asset);
        handover.setType(request.type().trim().toUpperCase());
        handover.setHandoverDate(request.handoverDate() != null ? request.handoverDate() : LocalDate.now());
        handover.setDocumentNumber(request.documentNumber() != null ? request.documentNumber().trim() : null);
        handover.setNote(request.note() != null ? request.note().trim() : null);
        handover.setStatus("COMPLETED");

        // Set FROM info from asset's current state
        handover.setFromEmployeeId(asset.getCurrentEmployeeId());
        handover.setFromDepartment(asset.getCurrentDepartment());
        if (asset.getCurrentEmployeeId() != null) {
            HoSoNhanVien fromEmp = hoSoNhanVienRepository.findById(asset.getCurrentEmployeeId()).orElse(null);
            handover.setFromEmployee(fromEmp);
        }

        if ("HANDOVER".equals(handover.getType())) {
            // From inventory to user/department
            if (request.toEmployeeId() == null && request.toDepartment() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cần chọn nhân viên hoặc phòng ban tiếp nhận.");
            }
            if (request.toEmployeeId() != null) {
                HoSoNhanVien toEmp = hoSoNhanVienRepository.findById(request.toEmployeeId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy nhân viên tiếp nhận."));
                handover.setToEmployee(toEmp);
                handover.setToEmployeeId(toEmp.getId());
            }
            handover.setToDepartment(request.toDepartment() != null ? request.toDepartment().trim() : null);

            // Update Asset
            asset.setCurrentEmployeeId(request.toEmployeeId());
            asset.setCurrentDepartment(handover.getToDepartment());
        } else if ("TRANSFER".equals(handover.getType())) {
            // From current user/department to new user/department
            if (asset.getCurrentEmployeeId() == null && asset.getCurrentDepartment() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tài sản hiện đang trong kho, vui lòng thực hiện bàn giao thay vì điều chuyển.");
            }
            if (request.toEmployeeId() == null && request.toDepartment() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cần chọn nhân viên hoặc phòng ban điều chuyển đến.");
            }
            if (request.toEmployeeId() != null) {
                HoSoNhanVien toEmp = hoSoNhanVienRepository.findById(request.toEmployeeId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy nhân viên điều chuyển đến."));
                handover.setToEmployee(toEmp);
                handover.setToEmployeeId(toEmp.getId());
            }
            handover.setToDepartment(request.toDepartment() != null ? request.toDepartment().trim() : null);

            // Update Asset
            asset.setCurrentEmployeeId(request.toEmployeeId());
            asset.setCurrentDepartment(handover.getToDepartment());
        } else if ("RECLAIM".equals(handover.getType())) {
            // Return to stock
            if (asset.getCurrentEmployeeId() == null && asset.getCurrentDepartment() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tài sản hiện đã trong kho, không cần thu hồi.");
            }
            handover.setToEmployeeId(null);
            handover.setToEmployee(null);
            handover.setToDepartment(null);

            // Update Asset
            asset.setCurrentEmployeeId(null);
            asset.setCurrentDepartment(null);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Loại bàn giao không hợp lệ.");
        }

        assetRepository.save(asset);
        AssetHandover saved = assetHandoverRepository.save(handover);
        return toResponse(saved);
    }

    private void validateRequest(AssetHandoverUpsertRequest request) {
        if (request.assetId() == null || request.assetId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã ID tài sản không được để trống.");
        }
        if (request.type() == null || request.type().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Loại giao dịch không được để trống.");
        }
    }

    private AssetHandoverResponse toResponse(AssetHandover h) {
        String fromEmpName = h.getFromEmployee() != null ? h.getFromEmployee().getName() : null;
        String toEmpName = h.getToEmployee() != null ? h.getToEmployee().getName() : null;
        String assetCode = h.getAsset() != null ? h.getAsset().getCode() : null;
        String assetName = h.getAsset() != null ? h.getAsset().getName() : null;

        return new AssetHandoverResponse(
                h.getId(),
                h.getAssetId(),
                assetCode,
                assetName,
                h.getType(),
                h.getFromEmployeeId(),
                fromEmpName,
                h.getToEmployeeId(),
                toEmpName,
                h.getFromDepartment(),
                h.getToDepartment(),
                h.getHandoverDate(),
                h.getDocumentNumber(),
                h.getNote(),
                h.getStatus(),
                h.getCreatedAt(),
                h.getUpdatedAt()
        );
    }
}
