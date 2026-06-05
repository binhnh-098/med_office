package com.example.med_office.service;

import com.example.med_office.dto.WarehouseInboundAction;
import com.example.med_office.dto.WarehouseInboundApprovalRequest;
import com.example.med_office.dto.WarehouseInboundCreateRequest;
import com.example.med_office.dto.WarehouseInboundDetailResponse;
import com.example.med_office.dto.WarehouseInboundListItemResponse;
import com.example.med_office.dto.WarehouseInboundMutationResponse;
import com.example.med_office.dto.WarehouseInboundPageResponse;
import com.example.med_office.dto.WarehouseInboundRejectRequest;
import com.example.med_office.dto.WarehouseInboundUpsertRequest;
import com.example.med_office.entity.NhaCungCap;
import com.example.med_office.entity.Warehouse;
import com.example.med_office.entity.WarehouseInbound;
import com.example.med_office.entity.WarehouseInboundItem;
import com.example.med_office.entity.WarehouseInboundStatus;
import com.example.med_office.repository.NhaCungCapRepository;
import com.example.med_office.repository.WarehouseInboundRepository;
import com.example.med_office.repository.WarehouseRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class WarehouseInboundServiceImpl implements WarehouseInboundService {

    private final WarehouseInboundRepository warehouseInboundRepository;
    private final WarehouseRepository warehouseRepository;
    private final NhaCungCapRepository nhaCungCapRepository;
    private final WarehousePermissionScopeService warehousePermissionScopeService;

    public WarehouseInboundServiceImpl(
            WarehouseInboundRepository warehouseInboundRepository,
            WarehouseRepository warehouseRepository,
            NhaCungCapRepository nhaCungCapRepository,
            WarehousePermissionScopeService warehousePermissionScopeService
    ) {
        this.warehouseInboundRepository = warehouseInboundRepository;
        this.warehouseRepository = warehouseRepository;
        this.nhaCungCapRepository = nhaCungCapRepository;
        this.warehousePermissionScopeService = warehousePermissionScopeService;
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseInboundPageResponse findAll(
            int page,
            int size,
            String keyword,
            String status,
            String warehouseId,
            LocalDate fromDate,
            LocalDate toDate
    ) {
        validateDateRange(fromDate, toDate);
        if (!warehousePermissionScopeService.isAdmin() && warehousePermissionScopeService.getManagedWarehouseIds().isEmpty()) {
            return new WarehouseInboundPageResponse(List.of(), page, size, 0, 0);
        }
        Page<WarehouseInbound> result = warehouseInboundRepository.findAll(
                buildSpecification(keyword, status, warehouseId, fromDate, toDate, resolveWarehouseScope()),
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "receiptDate", "createdAt", "id"))
        );

        return new WarehouseInboundPageResponse(
                result.getContent().stream().map(this::toListItemResponse).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseInboundDetailResponse findById(String id) {
        return toDetailResponse(requireAccessibleInbound(id));
    }

    @Override
    @Transactional
    public WarehouseInboundMutationResponse create(WarehouseInboundCreateRequest request) {
        WarehouseInboundUpsertRequest payload = request.toUpsertRequest();
        validateCreatePayload(payload);
        validateUniqueCode(payload.code(), null);

        WarehouseInbound warehouseInbound = new WarehouseInbound();
        applyPayload(warehouseInbound, payload, request.action());
        WarehouseInbound saved = warehouseInboundRepository.save(warehouseInbound);
        return toMutationResponse(saved);
    }

    @Override
    @Transactional
    public WarehouseInboundMutationResponse updateDraft(String id, WarehouseInboundUpsertRequest request) {
        WarehouseInbound warehouseInbound = requireAccessibleInbound(id);
        requireStatus(warehouseInbound, WarehouseInboundStatus.DRAFT, "Chi duoc cap nhat phieu nhap kho o trang thai DRAFT");
        validateUniqueCode(request.code(), id);

        applyPayload(warehouseInbound, request, WarehouseInboundAction.SAVE_DRAFT);
        WarehouseInbound saved = warehouseInboundRepository.save(warehouseInbound);
        return toMutationResponse(saved);
    }

    @Override
    @Transactional
    public WarehouseInboundMutationResponse submit(String id) {
        WarehouseInbound warehouseInbound = requireAccessibleInbound(id);
        requireStatus(warehouseInbound, WarehouseInboundStatus.DRAFT, "Phieu nhap kho khong o trang thai hop le de gui duyet");
        warehouseInbound.setStatus(WarehouseInboundStatus.PENDING_APPROVAL);
        WarehouseInbound saved = warehouseInboundRepository.save(warehouseInbound);
        return toMutationResponse(saved);
    }

    @Override
    @Transactional
    public WarehouseInboundMutationResponse approve(String id, WarehouseInboundApprovalRequest request) {
        WarehouseInbound warehouseInbound = requireAccessibleInbound(id);
        requireStatus(warehouseInbound, WarehouseInboundStatus.PENDING_APPROVAL, "Chi duyet duoc phieu nhap kho o trang thai PENDING_APPROVAL");
        warehouseInbound.setStatus(WarehouseInboundStatus.APPROVED);
        warehouseInbound.setApprovalNote(trim(request.note()));
        WarehouseInbound saved = warehouseInboundRepository.save(warehouseInbound);
        return toMutationResponse(saved);
    }

    @Override
    @Transactional
    public WarehouseInboundMutationResponse reject(String id, WarehouseInboundRejectRequest request) {
        WarehouseInbound warehouseInbound = requireAccessibleInbound(id);
        requireStatus(warehouseInbound, WarehouseInboundStatus.PENDING_APPROVAL, "Chi tu choi duoc phieu nhap kho o trang thai PENDING_APPROVAL");
        warehouseInbound.setStatus(WarehouseInboundStatus.REJECTED);
        warehouseInbound.setRejectionReason(trim(request.reason()));
        WarehouseInbound saved = warehouseInboundRepository.save(warehouseInbound);
        return toMutationResponse(saved);
    }

    @Override
    @Transactional
    public WarehouseInboundMutationResponse complete(String id) {
        WarehouseInbound warehouseInbound = requireAccessibleInbound(id);
        requireStatus(warehouseInbound, WarehouseInboundStatus.APPROVED, "Chi hoan tat duoc phieu nhap kho o trang thai APPROVED");
        warehouseInbound.setStatus(WarehouseInboundStatus.COMPLETED);
        warehouseInbound.setCompletedAt(Instant.now());
        WarehouseInbound saved = warehouseInboundRepository.save(warehouseInbound);
        return toMutationResponse(saved);
    }

    private Specification<WarehouseInbound> buildSpecification(
            String keyword,
            String status,
            String warehouseId,
            LocalDate fromDate,
            LocalDate toDate,
            Set<String> warehouseScope
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (warehouseScope != null && !warehouseScope.isEmpty()) {
                predicates.add(root.get("warehouseId").in(warehouseScope));
            }

            if (keyword != null && !keyword.isBlank()) {
                String normalizedKeyword = "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("code")), normalizedKeyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("supplierName")), normalizedKeyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("warehouseName")), normalizedKeyword)
                ));
            }

            if (status != null && !status.isBlank()) {
                WarehouseInboundStatus normalizedStatus = parseStatus(status);
                predicates.add(criteriaBuilder.equal(root.get("status"), normalizedStatus));
            }

            if (warehouseId != null && !warehouseId.isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("warehouseId"), warehouseId.trim()));
            }

            if (fromDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("receiptDate"), fromDate));
            }

            if (toDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("receiptDate"), toDate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private void applyPayload(WarehouseInbound warehouseInbound, WarehouseInboundUpsertRequest request, WarehouseInboundAction action) {
        Warehouse warehouse = requireWarehouse(request.warehouseId());
        assertWarehouseAccess(warehouse.getId());
        NhaCungCap supplier = resolveSupplier(request.supplierId());
        validateItems(request.items());

        warehouseInbound.setCode(trim(request.code()));
        warehouseInbound.setReceiptDate(request.receiptDate());
        warehouseInbound.setWarehouseId(warehouse.getId());
        warehouseInbound.setWarehouseName(warehouse.getName());
        warehouseInbound.setSupplierId(supplier != null ? supplier.getId() : normalizeNullable(request.supplierId()));
        warehouseInbound.setSupplierName(supplier != null ? supplier.getTenNhaCungCap() : trim(request.supplierName()));
        warehouseInbound.setInvoiceNumber(trim(request.invoiceNumber()));
        warehouseInbound.setSourceDocument(trim(request.sourceDocument()));
        warehouseInbound.setDeliveryBy(trim(request.deliveryBy()));
        warehouseInbound.setReceivedBy(trim(request.receivedBy()));
        warehouseInbound.setNote(trim(request.note()));
        warehouseInbound.setApprovalNote(null);
        warehouseInbound.setRejectionReason(null);
        warehouseInbound.setCompletedAt(null);
        warehouseInbound.setStatus(action == WarehouseInboundAction.SUBMIT_FOR_APPROVAL
                ? WarehouseInboundStatus.PENDING_APPROVAL
                : WarehouseInboundStatus.DRAFT);

        warehouseInbound.clearItems();
        for (WarehouseInboundUpsertRequest.WarehouseInboundItemRequest itemRequest : request.items()) {
            WarehouseInboundItem item = new WarehouseInboundItem();
            item.setItemId(normalizeNullable(itemRequest.itemId()));
            item.setItemCode(normalizeNullable(itemRequest.itemCode()));
            item.setItemName(trim(itemRequest.itemName()));
            item.setUnit(trim(itemRequest.unit()));
            item.setQuantity(itemRequest.quantity().stripTrailingZeros());
            item.setUnitPrice(itemRequest.unitPrice().stripTrailingZeros());
            item.setLineTotal(itemRequest.quantity().multiply(itemRequest.unitPrice()).stripTrailingZeros());
            item.setBatchNumber(trim(itemRequest.batchNumber()));
            item.setExpiryDate(itemRequest.expiryDate());
            warehouseInbound.addItem(item);
        }
    }

    private void validateItems(List<WarehouseInboundUpsertRequest.WarehouseInboundItemRequest> items) {
        if (items == null || items.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phieu nhap kho phai co it nhat 1 dong vat tu");
        }

        for (int index = 0; index < items.size(); index++) {
            WarehouseInboundUpsertRequest.WarehouseInboundItemRequest item = items.get(index);
            if (isBlank(item.itemName())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ten vat tu o dong thu " + (index + 1) + " khong duoc de trong");
            }
            if (item.quantity() == null || item.quantity().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "So luong o dong thu " + (index + 1) + " phai lon hon 0");
            }
            if (item.unitPrice() == null || item.unitPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Don gia o dong thu " + (index + 1) + " phai lon hon hoac bang 0");
            }
            if (isBlank(item.itemId()) && isBlank(item.itemCode())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dong vat tu thu " + (index + 1) + " phai co itemId hoac itemCode");
            }
        }
    }

    private void validateCreatePayload(WarehouseInboundUpsertRequest payload) {
        if (payload == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Thong tin phieu nhap khong duoc de trong");
        }
        if (isBlank(payload.code())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ma phieu nhap khong duoc de trong");
        }
        if (payload.receiptDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ngay nhap khong duoc de trong");
        }
        if (isBlank(payload.warehouseId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kho nhap khong duoc de trong");
        }
        validateItems(payload.items());
    }

    private void validateUniqueCode(String code, String currentId) {
        String normalizedCode = trim(code);
        boolean exists = currentId == null
                ? warehouseInboundRepository.existsByCodeIgnoreCase(normalizedCode)
                : warehouseInboundRepository.existsByCodeIgnoreCaseAndIdNot(normalizedCode, currentId);
        if (exists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ma phieu nhap kho da ton tai");
        }
    }

    private Warehouse requireWarehouse(String id) {
        return warehouseRepository.findById(trim(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kho nhap khong ton tai"));
    }

    private NhaCungCap resolveSupplier(String supplierId) {
        String normalizedId = normalizeNullable(supplierId);
        if (normalizedId == null) {
            return null;
        }
        return nhaCungCapRepository.findById(normalizedId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nha cung cap khong ton tai"));
    }

    private WarehouseInbound requireInbound(String id) {
        return warehouseInboundRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Khong tim thay phieu nhap kho"));
    }

    private WarehouseInbound requireAccessibleInbound(String id) {
        WarehouseInbound warehouseInbound = requireInbound(id);
        assertWarehouseAccess(warehouseInbound.getWarehouseId());
        return warehouseInbound;
    }

    private void requireStatus(WarehouseInbound warehouseInbound, WarehouseInboundStatus expectedStatus, String message) {
        if (warehouseInbound.getStatus() != expectedStatus) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    private WarehouseInboundStatus parseStatus(String value) {
        try {
            return WarehouseInboundStatus.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trang thai phieu nhap kho khong hop le");
        }
    }

    private void validateDateRange(LocalDate fromDate, LocalDate toDate) {
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Khoang ngay nhap khong hop le");
        }
    }

    private WarehouseInboundListItemResponse toListItemResponse(WarehouseInbound warehouseInbound) {
        Totals totals = calculateTotals(warehouseInbound);
        return new WarehouseInboundListItemResponse(
                warehouseInbound.getId(),
                warehouseInbound.getCode(),
                warehouseInbound.getReceiptDate().atStartOfDay().toInstant(ZoneOffset.UTC),
                warehouseInbound.getStatus(),
                warehouseInbound.getWarehouseId(),
                warehouseInbound.getWarehouseName(),
                warehouseInbound.getSupplierId(),
                warehouseInbound.getSupplierName(),
                warehouseInbound.getItems().size(),
                totals.totalQuantity(),
                totals.totalValue()
        );
    }

    private WarehouseInboundDetailResponse toDetailResponse(WarehouseInbound warehouseInbound) {
        Totals totals = calculateTotals(warehouseInbound);
        return new WarehouseInboundDetailResponse(
                warehouseInbound.getId(),
                warehouseInbound.getCode(),
                warehouseInbound.getReceiptDate(),
                warehouseInbound.getStatus(),
                warehouseInbound.getWarehouseId(),
                warehouseInbound.getWarehouseName(),
                warehouseInbound.getSupplierId(),
                warehouseInbound.getSupplierName(),
                warehouseInbound.getInvoiceNumber(),
                warehouseInbound.getSourceDocument(),
                warehouseInbound.getDeliveryBy(),
                warehouseInbound.getReceivedBy(),
                warehouseInbound.getNote(),
                warehouseInbound.getItems().size(),
                totals.totalQuantity(),
                totals.totalValue(),
                warehouseInbound.getItems().stream()
                        .sorted((left, right) -> left.getId().compareTo(right.getId()))
                        .map(item -> new WarehouseInboundDetailResponse.WarehouseInboundItemDetailResponse(
                                item.getId(),
                                item.getItemId(),
                                item.getItemCode(),
                                item.getItemName(),
                                item.getUnit(),
                                item.getQuantity(),
                                item.getUnitPrice(),
                                item.getLineTotal(),
                                item.getBatchNumber(),
                                item.getExpiryDate()
                        ))
                        .toList()
        );
    }

    private WarehouseInboundMutationResponse toMutationResponse(WarehouseInbound warehouseInbound) {
        return new WarehouseInboundMutationResponse(
                warehouseInbound.getId(),
                warehouseInbound.getCode(),
                warehouseInbound.getStatus()
        );
    }

    private Totals calculateTotals(WarehouseInbound warehouseInbound) {
        BigDecimal totalQuantity = BigDecimal.ZERO;
        BigDecimal totalValue = BigDecimal.ZERO;
        for (WarehouseInboundItem item : warehouseInbound.getItems()) {
            totalQuantity = totalQuantity.add(defaultDecimal(item.getQuantity()));
            totalValue = totalValue.add(defaultDecimal(item.getLineTotal()));
        }
        return new Totals(totalQuantity.stripTrailingZeros(), totalValue.stripTrailingZeros());
    }

    private BigDecimal defaultDecimal(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private Set<String> resolveWarehouseScope() {
        return warehousePermissionScopeService.isAdmin() ? Set.of() : warehousePermissionScopeService.getManagedWarehouseIds();
    }

    private void assertWarehouseAccess(String warehouseId) {
        warehousePermissionScopeService.assertWarehouseAccess(
                warehouseId,
                "Ban khong co quyen truy cap phieu nhap cua kho nay"
        );
    }

    private String normalizeNullable(String value) {
        String normalized = trim(value);
        return normalized == null || normalized.isBlank() ? null : normalized;
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private record Totals(BigDecimal totalQuantity, BigDecimal totalValue) {
    }
}
