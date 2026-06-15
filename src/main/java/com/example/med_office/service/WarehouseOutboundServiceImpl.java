package com.example.med_office.service;

import com.example.med_office.dto.WarehouseOutboundAction;
import com.example.med_office.dto.WarehouseOutboundApprovalRequest;
import com.example.med_office.dto.WarehouseOutboundCreateRequest;
import com.example.med_office.dto.WarehouseOutboundDetailResponse;
import com.example.med_office.dto.WarehouseOutboundListItemResponse;
import com.example.med_office.dto.WarehouseOutboundMutationResponse;
import com.example.med_office.dto.WarehouseOutboundPageResponse;
import com.example.med_office.dto.WarehouseOutboundRejectRequest;
import com.example.med_office.dto.WarehouseOutboundUpsertRequest;
import com.example.med_office.entity.Warehouse;
import com.example.med_office.entity.WarehouseOutbound;
import com.example.med_office.entity.WarehouseOutboundItem;
import com.example.med_office.entity.WarehouseOutboundStatus;
import com.example.med_office.repository.WarehouseOutboundRepository;
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
public class WarehouseOutboundServiceImpl implements WarehouseOutboundService {

    private final WarehouseOutboundRepository warehouseOutboundRepository;
    private final WarehouseRepository warehouseRepository;
    private final WarehousePermissionScopeService warehousePermissionScopeService;
    private final WarehouseInventoryService warehouseInventoryService;

    public WarehouseOutboundServiceImpl(
            WarehouseOutboundRepository warehouseOutboundRepository,
            WarehouseRepository warehouseRepository,
            WarehousePermissionScopeService warehousePermissionScopeService,
            WarehouseInventoryService warehouseInventoryService
    ) {
        this.warehouseOutboundRepository = warehouseOutboundRepository;
        this.warehouseRepository = warehouseRepository;
        this.warehousePermissionScopeService = warehousePermissionScopeService;
        this.warehouseInventoryService = warehouseInventoryService;
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseOutboundPageResponse findAll(
            int page,
            int size,
            String keyword,
            String status,
            String warehouseId,
            LocalDate fromDate,
            LocalDate toDate
    ) {
        validateDateRange(fromDate, toDate);
        Set<String> warehouseScope = warehousePermissionScopeService.resolveWarehouseScope();
        if (!warehousePermissionScopeService.isAdmin() && warehouseScope.isEmpty()) {
            return new WarehouseOutboundPageResponse(List.of(), page, size, 0, 0);
        }

        Page<WarehouseOutbound> result = warehouseOutboundRepository.findAll(
                buildSpecification(keyword, status, warehouseId, fromDate, toDate, warehouseScope),
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "outboundDate", "createdAt", "id"))
        );

        return new WarehouseOutboundPageResponse(
                result.getContent().stream().map(this::toListItemResponse).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseOutboundDetailResponse findById(String id) {
        return toDetailResponse(requireAccessibleOutbound(id));
    }

    @Override
    @Transactional
    public WarehouseOutboundMutationResponse create(WarehouseOutboundCreateRequest request) {
        WarehouseOutboundUpsertRequest payload = request.toUpsertRequest();
        validateCreatePayload(payload);
        validateUniqueCode(payload.code(), null);

        WarehouseOutbound warehouseOutbound = new WarehouseOutbound();
        applyPayload(warehouseOutbound, payload, request.action());
        WarehouseOutbound saved = warehouseOutboundRepository.save(warehouseOutbound);
        return toMutationResponse(saved);
    }

    @Override
    @Transactional
    public WarehouseOutboundMutationResponse updateDraft(String id, WarehouseOutboundUpsertRequest request) {
        WarehouseOutbound warehouseOutbound = requireAccessibleOutboundForUpdate(id);
        requireStatus(warehouseOutbound, WarehouseOutboundStatus.DRAFT, "Chi duoc cap nhat phieu xuat kho o trang thai DRAFT");
        validateUniqueCode(request.code(), id);

        applyPayload(warehouseOutbound, request, WarehouseOutboundAction.SAVE_DRAFT);
        WarehouseOutbound saved = warehouseOutboundRepository.save(warehouseOutbound);
        return toMutationResponse(saved);
    }

    @Override
    @Transactional
    public WarehouseOutboundMutationResponse submit(String id) {
        WarehouseOutbound warehouseOutbound = requireAccessibleOutboundForUpdate(id);
        requireStatus(warehouseOutbound, WarehouseOutboundStatus.DRAFT, "Phieu xuat kho khong o trang thai hop le de gui duyet");
        validateInventoryTransitionWarehouses(warehouseOutbound);
        warehouseInventoryService.assertSufficientAvailability(
                warehouseOutbound.getWarehouseId(),
                toItemRequests(warehouseOutbound),
                warehouseOutbound.getOutboundDate()
        );
        warehouseOutbound.setStatus(WarehouseOutboundStatus.PENDING_APPROVAL);
        WarehouseOutbound saved = warehouseOutboundRepository.save(warehouseOutbound);
        return toMutationResponse(saved);
    }

    @Override
    @Transactional
    public WarehouseOutboundMutationResponse approve(String id, WarehouseOutboundApprovalRequest request) {
        WarehouseOutbound warehouseOutbound = requireAccessibleOutboundForUpdate(id);
        requireStatus(warehouseOutbound, WarehouseOutboundStatus.PENDING_APPROVAL, "Chi duyet duoc phieu xuat kho o trang thai PENDING_APPROVAL");
        validateInventoryTransitionWarehouses(warehouseOutbound);
        List<WarehouseOutboundUpsertRequest.WarehouseOutboundItemRequest> itemRequests = toItemRequests(warehouseOutbound);
        warehouseInventoryService.assertSufficientAvailability(
                warehouseOutbound.getWarehouseId(),
                itemRequests,
                warehouseOutbound.getOutboundDate(),
                itemRequests
        );
        warehouseOutbound.setStatus(WarehouseOutboundStatus.APPROVED);
        warehouseOutbound.setApprovalNote(trim(request.note()));
        WarehouseOutbound saved = warehouseOutboundRepository.save(warehouseOutbound);
        return toMutationResponse(saved);
    }

    @Override
    @Transactional
    public WarehouseOutboundMutationResponse reject(String id, WarehouseOutboundRejectRequest request) {
        WarehouseOutbound warehouseOutbound = requireAccessibleOutboundForUpdate(id);
        requireStatus(warehouseOutbound, WarehouseOutboundStatus.PENDING_APPROVAL, "Chi tu choi duoc phieu xuat kho o trang thai PENDING_APPROVAL");
        validateInventoryTransitionWarehouses(warehouseOutbound);
        warehouseOutbound.setStatus(WarehouseOutboundStatus.REJECTED);
        warehouseOutbound.setRejectionReason(trim(request.reason()));
        WarehouseOutbound saved = warehouseOutboundRepository.save(warehouseOutbound);
        return toMutationResponse(saved);
    }

    @Override
    @Transactional
    public WarehouseOutboundMutationResponse complete(String id) {
        WarehouseOutbound warehouseOutbound = requireAccessibleOutboundForUpdate(id);
        requireStatus(warehouseOutbound, WarehouseOutboundStatus.APPROVED, "Chi hoan tat duoc phieu xuat kho o trang thai APPROVED");
        validateInventoryTransitionWarehouses(warehouseOutbound);
        warehouseOutbound.setStatus(WarehouseOutboundStatus.COMPLETED);
        warehouseOutbound.setCompletedAt(Instant.now());
        WarehouseOutbound saved = warehouseOutboundRepository.save(warehouseOutbound);
        return toMutationResponse(saved);
    }

    private Specification<WarehouseOutbound> buildSpecification(
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
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("destinationName")), normalizedKeyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("warehouseName")), normalizedKeyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("receivedBy")), normalizedKeyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("requestedBy")), normalizedKeyword)
                ));
            }

            if (status != null && !status.isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("status"), parseStatus(status)));
            }

            if (warehouseId != null && !warehouseId.isBlank()) {
                String normalizedWarehouseId = warehouseId.trim();
                warehousePermissionScopeService.assertWarehouseAccess(normalizedWarehouseId, "Ban khong co quyen truy cap kho nay");
                predicates.add(criteriaBuilder.equal(root.get("warehouseId"), normalizedWarehouseId));
            }

            if (fromDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("outboundDate"), fromDate));
            }

            if (toDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("outboundDate"), toDate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private void applyPayload(WarehouseOutbound warehouseOutbound, WarehouseOutboundUpsertRequest request, WarehouseOutboundAction action) {
        Warehouse warehouse = requireActiveWarehouse(request.warehouseId(), "Kho xuat khong ton tai", "Kho xuat khong hoat dong");
        assertWarehouseAccess(warehouse.getId());
        lockActiveWarehouseForInventoryTransition(warehouse.getId(), "Kho xuat khong ton tai", "Kho xuat khong hoat dong");
        Warehouse destinationWarehouse = resolveDestinationWarehouse(request, warehouse);
        validateItems(request.items());
        warehouseInventoryService.assertSufficientAvailability(warehouse.getId(), request.items(), request.outboundDate());

        warehouseOutbound.setCode(trim(request.code()));
        warehouseOutbound.setOutboundDate(request.outboundDate());
        warehouseOutbound.setWarehouseId(warehouse.getId());
        warehouseOutbound.setWarehouseName(warehouse.getName());
        warehouseOutbound.setDestinationWarehouseId(destinationWarehouse == null ? null : destinationWarehouse.getId());
        warehouseOutbound.setDestinationName(resolveDestinationName(request, destinationWarehouse));
        warehouseOutbound.setReceivedBy(trim(request.receivedBy()));
        warehouseOutbound.setRequestedBy(trim(request.requestedBy()));
        warehouseOutbound.setNote(trim(request.note()));
        warehouseOutbound.setApprovalNote(null);
        warehouseOutbound.setRejectionReason(null);
        warehouseOutbound.setCompletedAt(null);
        warehouseOutbound.setStatus(action == WarehouseOutboundAction.SUBMIT_FOR_APPROVAL
                ? WarehouseOutboundStatus.PENDING_APPROVAL
                : WarehouseOutboundStatus.DRAFT);

        warehouseOutbound.clearItems();
        for (WarehouseOutboundUpsertRequest.WarehouseOutboundItemRequest itemRequest : request.items()) {
            WarehouseOutboundItem item = new WarehouseOutboundItem();
            item.setItemId(normalizeNullable(itemRequest.itemId()));
            item.setItemCode(normalizeNullable(itemRequest.itemCode()));
            item.setItemName(trim(itemRequest.itemName()));
            item.setQuantity(itemRequest.quantity().stripTrailingZeros());
            item.setUnit(trim(itemRequest.unit()));
            item.setBatchNumber(trim(itemRequest.batchNumber()));
            item.setExpiryDate(itemRequest.expiryDate());
            item.setNote(trim(itemRequest.note()));
            warehouseOutbound.addItem(item);
        }
    }

    private void validateItems(List<WarehouseOutboundUpsertRequest.WarehouseOutboundItemRequest> items) {
        if (items == null || items.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phieu xuat kho phai co it nhat 1 dong vat tu");
        }

        for (int index = 0; index < items.size(); index++) {
            WarehouseOutboundUpsertRequest.WarehouseOutboundItemRequest item = items.get(index);
            if (isBlank(item.itemName())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ten vat tu o dong thu " + (index + 1) + " khong duoc de trong");
            }
            if (item.quantity() == null || item.quantity().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "So luong o dong thu " + (index + 1) + " phai lon hon 0");
            }
            if (isBlank(item.itemId()) && isBlank(item.itemCode())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dong vat tu thu " + (index + 1) + " phai co itemId hoac itemCode");
            }
        }
    }

    private void validateCreatePayload(WarehouseOutboundUpsertRequest payload) {
        if (payload == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Thong tin phieu xuat khong duoc de trong");
        }
        if (isBlank(payload.code())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ma phieu xuat khong duoc de trong");
        }
        if (payload.outboundDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ngay xuat khong duoc de trong");
        }
        if (isBlank(payload.warehouseId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kho xuat khong duoc de trong");
        }
        if (isBlank(payload.destinationWarehouseId()) && isBlank(payload.destinationName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kho nhan khong duoc de trong");
        }
        validateItems(payload.items());
    }

    private void validateUniqueCode(String code, String currentId) {
        String normalizedCode = trim(code);
        boolean exists = currentId == null
                ? warehouseOutboundRepository.existsByCodeIgnoreCase(normalizedCode)
                : warehouseOutboundRepository.existsByCodeIgnoreCaseAndIdNot(normalizedCode, currentId);
        if (exists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ma phieu xuat kho da ton tai");
        }
    }

    private Warehouse requireActiveWarehouse(String id, String notFoundMessage, String inactiveMessage) {
        return warehouseRepository.findById(trim(id))
                .filter(this::isWarehouseActive)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, resolveWarehouseErrorMessage(id, notFoundMessage, inactiveMessage)));
    }

    private WarehouseOutbound requireOutbound(String id) {
        return warehouseOutboundRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Khong tim thay phieu xuat kho"));
    }

    private WarehouseOutbound requireOutboundForUpdate(String id) {
        return warehouseOutboundRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Khong tim thay phieu xuat kho"));
    }

    private WarehouseOutbound requireAccessibleOutbound(String id) {
        WarehouseOutbound warehouseOutbound = requireOutbound(id);
        assertWarehouseAccess(warehouseOutbound.getWarehouseId());
        return warehouseOutbound;
    }

    private WarehouseOutbound requireAccessibleOutboundForUpdate(String id) {
        WarehouseOutbound warehouseOutbound = requireOutboundForUpdate(id);
        assertWarehouseAccess(warehouseOutbound.getWarehouseId());
        return warehouseOutbound;
    }

    private void requireStatus(WarehouseOutbound warehouseOutbound, WarehouseOutboundStatus expectedStatus, String message) {
        if (warehouseOutbound.getStatus() != expectedStatus) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    private WarehouseOutboundStatus parseStatus(String value) {
        try {
            return WarehouseOutboundStatus.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trang thai phieu xuat kho khong hop le");
        }
    }

    private void validateDateRange(LocalDate fromDate, LocalDate toDate) {
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Khoang ngay xuat khong hop le");
        }
    }

    private WarehouseOutboundListItemResponse toListItemResponse(WarehouseOutbound warehouseOutbound) {
        BigDecimal totalQuantity = warehouseOutbound.getItems().stream()
                .map(WarehouseOutboundItem::getQuantity)
                .filter(quantity -> quantity != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .stripTrailingZeros();
        return new WarehouseOutboundListItemResponse(
                warehouseOutbound.getId(),
                warehouseOutbound.getCode(),
                warehouseOutbound.getOutboundDate().atStartOfDay().toInstant(ZoneOffset.UTC),
                warehouseOutbound.getStatus(),
                warehouseOutbound.getWarehouseId(),
                warehouseOutbound.getWarehouseName(),
                warehouseOutbound.getDestinationWarehouseId(),
                resolveDestinationWarehouseName(warehouseOutbound),
                warehouseOutbound.getDestinationName(),
                warehouseOutbound.getReceivedBy(),
                warehouseOutbound.getRequestedBy(),
                warehouseOutbound.getItems().size(),
                totalQuantity
        );
    }

    private WarehouseOutboundMutationResponse toMutationResponse(WarehouseOutbound warehouseOutbound) {
        return new WarehouseOutboundMutationResponse(
                warehouseOutbound.getId(),
                warehouseOutbound.getCode(),
                warehouseOutbound.getStatus()
        );
    }

    private WarehouseOutboundDetailResponse toDetailResponse(WarehouseOutbound warehouseOutbound) {
        BigDecimal totalQuantity = warehouseOutbound.getItems().stream()
                .map(WarehouseOutboundItem::getQuantity)
                .filter(quantity -> quantity != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .stripTrailingZeros();
        return new WarehouseOutboundDetailResponse(
                warehouseOutbound.getId(),
                warehouseOutbound.getCode(),
                warehouseOutbound.getOutboundDate(),
                warehouseOutbound.getStatus(),
                warehouseOutbound.getWarehouseId(),
                warehouseOutbound.getWarehouseName(),
                warehouseOutbound.getDestinationWarehouseId(),
                resolveDestinationWarehouseName(warehouseOutbound),
                warehouseOutbound.getDestinationName(),
                warehouseOutbound.getReceivedBy(),
                warehouseOutbound.getRequestedBy(),
                warehouseOutbound.getNote(),
                warehouseOutbound.getApprovalNote(),
                warehouseOutbound.getRejectionReason(),
                warehouseOutbound.getItems().size(),
                totalQuantity,
                warehouseOutbound.getItems().stream()
                        .sorted((left, right) -> left.getId().compareTo(right.getId()))
                        .map(item -> new WarehouseOutboundDetailResponse.WarehouseOutboundItemDetailResponse(
                                item.getId(),
                                item.getItemId(),
                                item.getItemCode(),
                                item.getItemName(),
                                item.getUnit(),
                                item.getQuantity(),
                                item.getBatchNumber(),
                                item.getExpiryDate(),
                                item.getNote()
                        ))
                        .toList()
        );
    }

    private List<WarehouseOutboundUpsertRequest.WarehouseOutboundItemRequest> toItemRequests(WarehouseOutbound warehouseOutbound) {
        return warehouseOutbound.getItems().stream().map(item -> new WarehouseOutboundUpsertRequest.WarehouseOutboundItemRequest(
                item.getItemId(),
                item.getItemCode(),
                item.getItemName(),
                item.getQuantity(),
                item.getUnit(),
                item.getBatchNumber(),
                item.getExpiryDate(),
                item.getNote()
        )).toList();
    }

    private Warehouse lockActiveWarehouseForInventoryTransition(String warehouseId, String notFoundMessage, String inactiveMessage) {
        return warehouseRepository.findByIdForUpdate(trim(warehouseId))
                .filter(this::isWarehouseActive)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, resolveWarehouseErrorMessage(warehouseId, notFoundMessage, inactiveMessage)));
    }

    private void validateInventoryTransitionWarehouses(WarehouseOutbound warehouseOutbound) {
        lockActiveWarehouseForInventoryTransition(warehouseOutbound.getWarehouseId(), "Kho xuat khong ton tai", "Kho xuat khong hoat dong");
        if (!isBlank(warehouseOutbound.getDestinationWarehouseId())) {
            if (warehouseOutbound.getWarehouseId().equals(trim(warehouseOutbound.getDestinationWarehouseId()))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kho nhan phai khac kho xuat");
            }
            Warehouse destinationWarehouse = lockActiveWarehouseForInventoryTransition(
                    warehouseOutbound.getDestinationWarehouseId(),
                    "Kho nhan khong ton tai",
                    "Kho nhan khong hoat dong"
            );
            warehouseOutbound.setDestinationName(destinationWarehouse.getName());
        }
    }

    private void assertWarehouseAccess(String warehouseId) {
        warehousePermissionScopeService.assertWarehouseAccess(warehouseId, "Ban khong co quyen truy cap phieu xuat cua kho nay");
    }

    private Warehouse resolveDestinationWarehouse(WarehouseOutboundUpsertRequest request, Warehouse sourceWarehouse) {
        String destinationWarehouseId = normalizeNullable(request.destinationWarehouseId());
        if (destinationWarehouseId == null) {
            return null;
        }
        if (sourceWarehouse.getId().equals(destinationWarehouseId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kho nhan phai khac kho xuat");
        }
        return requireActiveWarehouse(destinationWarehouseId, "Kho nhan khong ton tai", "Kho nhan khong hoat dong");
    }

    private String resolveDestinationName(WarehouseOutboundUpsertRequest request, Warehouse destinationWarehouse) {
        if (destinationWarehouse != null) {
            return destinationWarehouse.getName();
        }
        return trim(request.destinationName());
    }

    private String resolveDestinationWarehouseName(WarehouseOutbound warehouseOutbound) {
        if (isBlank(warehouseOutbound.getDestinationWarehouseId())) {
            return null;
        }
        return warehouseOutbound.getDestinationName();
    }

    private boolean isWarehouseActive(Warehouse warehouse) {
        return warehouse != null && !isBlank(warehouse.getStatus()) && "ACTIVE".equalsIgnoreCase(warehouse.getStatus());
    }

    private String resolveWarehouseErrorMessage(String warehouseId, String notFoundMessage, String inactiveMessage) {
        return warehouseRepository.findById(trim(warehouseId))
                .map(warehouse -> isWarehouseActive(warehouse) ? notFoundMessage : inactiveMessage)
                .orElse(notFoundMessage);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String normalizeNullable(String value) {
        String normalized = trim(value);
        return normalized == null || normalized.isBlank() ? null : normalized;
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}
