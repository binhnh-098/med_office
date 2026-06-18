package com.example.med_office.service;

import com.example.med_office.dto.WarehouseInventoryAggregateRow;
import com.example.med_office.dto.WarehouseInventoryListItemResponse;
import com.example.med_office.dto.WarehouseInventoryPageResponse;
import com.example.med_office.dto.WarehouseOutboundUpsertRequest;
import com.example.med_office.entity.WarehouseInboundStatus;
import com.example.med_office.entity.WarehouseOutboundStatus;
import com.example.med_office.repository.WarehouseInboundItemRepository;
import com.example.med_office.repository.WarehouseOutboundItemRepository;
import com.example.med_office.utils.WarehouseInventoryKeyUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
public class WarehouseInventoryServiceImpl implements WarehouseInventoryService {

    private static final List<WarehouseInboundStatus> EFFECTIVE_INBOUND_STATUSES = List.of(
            WarehouseInboundStatus.APPROVED,
            WarehouseInboundStatus.COMPLETED
    );

    private static final List<WarehouseInboundStatus> RESERVED_INBOUND_STATUSES = List.of(
            WarehouseInboundStatus.PENDING_APPROVAL
    );

    private static final List<WarehouseOutboundStatus> EFFECTIVE_OUTBOUND_STATUSES = List.of(
            WarehouseOutboundStatus.APPROVED,
            WarehouseOutboundStatus.COMPLETED
    );

    private static final List<WarehouseOutboundStatus> RESERVED_OUTBOUND_STATUSES = List.of(
            WarehouseOutboundStatus.PENDING_APPROVAL
    );

    private static final List<WarehouseOutboundStatus> EFFECTIVE_TRANSFER_INBOUND_STATUSES = List.of(
            WarehouseOutboundStatus.COMPLETED
    );

    private final WarehouseInboundItemRepository warehouseInboundItemRepository;
    private final WarehouseOutboundItemRepository warehouseOutboundItemRepository;
    private final WarehousePermissionScopeService warehousePermissionScopeService;
    private final WarehouseInventoryMinQuantityService warehouseInventoryMinQuantityService;

    public WarehouseInventoryServiceImpl(
            WarehouseInboundItemRepository warehouseInboundItemRepository,
            WarehouseOutboundItemRepository warehouseOutboundItemRepository,
            WarehousePermissionScopeService warehousePermissionScopeService,
            WarehouseInventoryMinQuantityService warehouseInventoryMinQuantityService
    ) {
        this.warehouseInboundItemRepository = warehouseInboundItemRepository;
        this.warehouseOutboundItemRepository = warehouseOutboundItemRepository;
        this.warehousePermissionScopeService = warehousePermissionScopeService;
        this.warehouseInventoryMinQuantityService = warehouseInventoryMinQuantityService;
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseInventoryPageResponse findAll(int page, int size, String keyword, String warehouseId) {
        validatePagination(page, size);
        String normalizedKeyword = normalizeKeyword(keyword);
        String normalizedWarehouseId = normalizeId(warehouseId);
        Set<String> allowedWarehouseIds = warehousePermissionScopeService.resolveAllowedWarehouseIds();

        if (allowedWarehouseIds.isEmpty()) {
            return new WarehouseInventoryPageResponse(List.of(), page, size, 0, 0);
        }

        if (normalizedWarehouseId != null && !allowedWarehouseIds.contains(normalizedWarehouseId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ban khong co quyen truy cap kho nay");
        }

        List<WarehouseInventoryListItemResponse> mergedItems = mergeInventoryRows(
                warehouseInboundItemRepository.summarizeQuantities(EFFECTIVE_INBOUND_STATUSES, allowedWarehouseIds, normalizedWarehouseId, normalizedKeyword),
                warehouseOutboundItemRepository.summarizeTransferredInQuantities(EFFECTIVE_TRANSFER_INBOUND_STATUSES, allowedWarehouseIds, normalizedWarehouseId, normalizedKeyword),
                warehouseOutboundItemRepository.summarizeQuantities(EFFECTIVE_OUTBOUND_STATUSES, allowedWarehouseIds, normalizedWarehouseId, normalizedKeyword),
                warehouseOutboundItemRepository.summarizeQuantities(RESERVED_OUTBOUND_STATUSES, allowedWarehouseIds, normalizedWarehouseId, normalizedKeyword),
                warehouseInboundItemRepository.summarizeTransferredOutQuantities(EFFECTIVE_INBOUND_STATUSES, allowedWarehouseIds, normalizedWarehouseId, normalizedKeyword),
                warehouseInboundItemRepository.summarizeTransferredOutQuantities(RESERVED_INBOUND_STATUSES, allowedWarehouseIds, normalizedWarehouseId, normalizedKeyword)
        );

        int fromIndex = Math.min(page * size, mergedItems.size());
        int toIndex = Math.min(fromIndex + size, mergedItems.size());
        List<WarehouseInventoryListItemResponse> pageContent = mergedItems.subList(fromIndex, toIndex);
        int totalPages = size <= 0 ? 0 : (int) Math.ceil((double) mergedItems.size() / size);
        return new WarehouseInventoryPageResponse(pageContent, page, size, mergedItems.size(), totalPages);
    }

    @Override
    @Transactional(readOnly = true)
    public void assertSufficientAvailability(
            String warehouseId,
            List<WarehouseOutboundUpsertRequest.WarehouseOutboundItemRequest> items,
            LocalDate outboundDate
    ) {
        assertSufficientAvailability(warehouseId, items, outboundDate, List.of());
    }

    @Override
    @Transactional(readOnly = true)
    public void assertSufficientAvailability(
            String warehouseId,
            List<WarehouseOutboundUpsertRequest.WarehouseOutboundItemRequest> items,
            LocalDate outboundDate,
            List<WarehouseOutboundUpsertRequest.WarehouseOutboundItemRequest> releasedReservedItems
    ) {
        String normalizedWarehouseId = normalizeRequiredId(warehouseId, "Kho xuat khong duoc de trong");
        warehousePermissionScopeService.assertWarehouseAccess(normalizedWarehouseId, "Ban khong co quyen thao tac voi kho nay");

        Set<String> warehouseIds = Set.of(normalizedWarehouseId);
        Map<InventoryKey, BigDecimal> completedInboundMap = toQuantityMap(
                warehouseInboundItemRepository.summarizeQuantities(EFFECTIVE_INBOUND_STATUSES, warehouseIds, normalizedWarehouseId, null)
        );
        Map<InventoryKey, BigDecimal> completedTransferInboundMap = toQuantityMap(
                warehouseOutboundItemRepository.summarizeTransferredInQuantities(EFFECTIVE_TRANSFER_INBOUND_STATUSES, warehouseIds, normalizedWarehouseId, null)
        );
        Map<InventoryKey, BigDecimal> completedOutboundMap = toQuantityMap(
                warehouseOutboundItemRepository.summarizeQuantities(EFFECTIVE_OUTBOUND_STATUSES, warehouseIds, normalizedWarehouseId, null)
        );
        Map<InventoryKey, BigDecimal> completedTransferOutFromInboundMap = toQuantityMap(
                warehouseInboundItemRepository.summarizeTransferredOutQuantities(EFFECTIVE_INBOUND_STATUSES, warehouseIds, normalizedWarehouseId, null)
        );
        Map<InventoryKey, BigDecimal> reservedOutboundMap = toQuantityMap(
                warehouseOutboundItemRepository.summarizeQuantities(RESERVED_OUTBOUND_STATUSES, warehouseIds, normalizedWarehouseId, null)
        );
        Map<InventoryKey, BigDecimal> reservedTransferOutFromInboundMap = toQuantityMap(
                warehouseInboundItemRepository.summarizeTransferredOutQuantities(RESERVED_INBOUND_STATUSES, warehouseIds, normalizedWarehouseId, null)
        );
        Map<InventoryKey, BigDecimal> requestedQuantityMap = toRequestQuantityMap(normalizedWarehouseId, items);
        Map<InventoryKey, BigDecimal> releasedReservedMap = toRequestQuantityMap(normalizedWarehouseId, releasedReservedItems);
        Map<InventoryKey, String> itemLabelMap = toItemLabelMap(normalizedWarehouseId, items);

        for (Map.Entry<InventoryKey, BigDecimal> requestedEntry : requestedQuantityMap.entrySet()) {
            InventoryKey key = requestedEntry.getKey();
            BigDecimal inboundQuantity = completedInboundMap.getOrDefault(key, BigDecimal.ZERO);
            BigDecimal transferInboundQuantity = completedTransferInboundMap.getOrDefault(key, BigDecimal.ZERO);
            BigDecimal completedOutboundQuantity = completedOutboundMap.getOrDefault(key, BigDecimal.ZERO)
                    .add(completedTransferOutFromInboundMap.getOrDefault(key, BigDecimal.ZERO));
            BigDecimal reservedQuantity = reservedOutboundMap.getOrDefault(key, BigDecimal.ZERO)
                    .add(reservedTransferOutFromInboundMap.getOrDefault(key, BigDecimal.ZERO));
            BigDecimal releasedReservedQuantity = releasedReservedMap.getOrDefault(key, BigDecimal.ZERO);
            BigDecimal availableQuantity = inboundQuantity
                    .add(transferInboundQuantity)
                    .subtract(completedOutboundQuantity)
                    .subtract(reservedQuantity)
                    .add(releasedReservedQuantity);
            BigDecimal requestedQuantity = requestedEntry.getValue();
            if (availableQuantity.compareTo(requestedQuantity) < 0) {
                String itemLabel = itemLabelMap.getOrDefault(key, "vat tu");
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "So luong ton kha dung khong du de xuat cho vat tu " + itemLabel
                );
            }
        }
    }

    private Map<InventoryKey, BigDecimal> toRequestQuantityMap(
            String warehouseId,
            Collection<WarehouseOutboundUpsertRequest.WarehouseOutboundItemRequest> items
    ) {
        Map<InventoryKey, BigDecimal> quantityByKey = new LinkedHashMap<>();
        if (items == null) {
            return quantityByKey;
        }
        for (WarehouseOutboundUpsertRequest.WarehouseOutboundItemRequest item : items) {
            quantityByKey.merge(
                    InventoryKey.fromRequest(warehouseId, item),
                    defaultDecimal(item.quantity()),
                    BigDecimal::add
            );
        }
        return quantityByKey;
    }

    private Map<InventoryKey, String> toItemLabelMap(
            String warehouseId,
            Collection<WarehouseOutboundUpsertRequest.WarehouseOutboundItemRequest> items
    ) {
        Map<InventoryKey, String> itemLabelByKey = new LinkedHashMap<>();
        if (items == null) {
            return itemLabelByKey;
        }
        int index = 0;
        for (WarehouseOutboundUpsertRequest.WarehouseOutboundItemRequest item : items) {
            index++;
            InventoryKey key = InventoryKey.fromRequest(warehouseId, item);
            itemLabelByKey.putIfAbsent(key, resolveItemLabel(item, index));
        }
        return itemLabelByKey;
    }

    private String resolveItemLabel(WarehouseOutboundUpsertRequest.WarehouseOutboundItemRequest item, int index) {
        if (item.itemName() != null && !item.itemName().isBlank()) {
            return item.itemName().trim();
        }
        if (item.itemCode() != null && !item.itemCode().isBlank()) {
            return item.itemCode().trim();
        }
        return "dong vat tu thu " + index;
    }

    private List<WarehouseInventoryListItemResponse> mergeInventoryRows(
            List<WarehouseInventoryAggregateRow> inboundRows,
            List<WarehouseInventoryAggregateRow> transferInboundRows,
            List<WarehouseInventoryAggregateRow> completedOutboundRows,
            List<WarehouseInventoryAggregateRow> reservedOutboundRows,
            List<WarehouseInventoryAggregateRow> completedTransferOutInboundRows,
            List<WarehouseInventoryAggregateRow> reservedTransferOutInboundRows
    ) {
        Map<InventoryKey, InventoryAccumulator> accumulatorByKey = new LinkedHashMap<>();
        accumulateRows(accumulatorByKey, inboundRows, QuantityType.INBOUND);
        accumulateRows(accumulatorByKey, transferInboundRows, QuantityType.TRANSFER_INBOUND);
        accumulateRows(accumulatorByKey, completedOutboundRows, QuantityType.COMPLETED_OUTBOUND);
        accumulateRows(accumulatorByKey, completedTransferOutInboundRows, QuantityType.COMPLETED_OUTBOUND);
        accumulateRows(accumulatorByKey, reservedOutboundRows, QuantityType.RESERVED_OUTBOUND);
        accumulateRows(accumulatorByKey, reservedTransferOutInboundRows, QuantityType.RESERVED_OUTBOUND);
        Map<String, BigDecimal> minQuantityByKey = warehouseInventoryMinQuantityService.getMinQuantitiesByInventoryKeys(
                accumulatorByKey.keySet().stream()
                        .map(InventoryKey::stableId)
                        .toList()
        );

        List<WarehouseInventoryListItemResponse> responses = new ArrayList<>();
        for (Map.Entry<InventoryKey, InventoryAccumulator> entry : accumulatorByKey.entrySet()) {
            InventoryAccumulator accumulator = entry.getValue();
            BigDecimal totalQuantity = accumulator.inboundQuantity.subtract(accumulator.completedOutboundQuantity);
            BigDecimal reservedQuantity = accumulator.reservedQuantity;
            BigDecimal availableQuantity = totalQuantity.subtract(reservedQuantity);
            if (totalQuantity.compareTo(BigDecimal.ZERO) <= 0 && reservedQuantity.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            responses.add(new WarehouseInventoryListItemResponse(
                    entry.getKey().stableId(),
                    accumulator.itemCode,
                    accumulator.itemName,
                    accumulator.warehouseId,
                    accumulator.warehouseName,
                    accumulator.batchNumber,
                    accumulator.expiryDate,
                    accumulator.unit,
                    strip(availableQuantity),
                    strip(reservedQuantity),
                    strip(totalQuantity),
                    strip(minQuantityByKey.getOrDefault(entry.getKey().stableId(), BigDecimal.ZERO)),
                    strip(accumulator.unitPrice)
            ));
        }

        responses.sort(Comparator
                .comparing(WarehouseInventoryListItemResponse::warehouseName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                .thenComparing(WarehouseInventoryListItemResponse::itemName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                .thenComparing(WarehouseInventoryListItemResponse::itemCode, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                .thenComparing(WarehouseInventoryListItemResponse::batchNumber, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                .thenComparing(response -> response.expiryDate() == null ? LocalDate.MAX : response.expiryDate()));
        return responses;
    }

    private void accumulateRows(
            Map<InventoryKey, InventoryAccumulator> accumulatorByKey,
            Collection<WarehouseInventoryAggregateRow> rows,
            QuantityType quantityType
    ) {
        for (WarehouseInventoryAggregateRow row : rows) {
            InventoryKey key = InventoryKey.fromRow(row);
            InventoryAccumulator accumulator = accumulatorByKey.computeIfAbsent(key, ignored -> new InventoryAccumulator(row));
            accumulator.addQuantity(quantityType, defaultDecimal(row.quantity()));
            if (row.unitPrice() != null) {
                accumulator.updateUnitPrice(row.unitPrice());
            }
        }
    }

    private Map<InventoryKey, BigDecimal> toQuantityMap(Collection<WarehouseInventoryAggregateRow> rows) {
        Map<InventoryKey, BigDecimal> quantityByKey = new LinkedHashMap<>();
        for (WarehouseInventoryAggregateRow row : rows) {
            quantityByKey.merge(InventoryKey.fromRow(row), defaultDecimal(row.quantity()), BigDecimal::add);
        }
        return quantityByKey;
    }

    private BigDecimal strip(BigDecimal value) {
        return defaultDecimal(value).stripTrailingZeros();
    }

    private BigDecimal defaultDecimal(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private void validatePagination(int page, int size) {
        if (page < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "page must be >= 0");
        }
        if (size < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "size must be >= 1");
        }
        if (size > 500) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "size must be <= 500");
        }
    }

    private String normalizeId(String value) {
        if (value == null) {
            return null;
        }
        String normalizedValue = value.trim();
        return normalizedValue.isBlank() ? null : normalizedValue;
    }

    private String normalizeRequiredId(String value, String message) {
        String normalizedValue = normalizeId(value);
        if (normalizedValue == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
        return normalizedValue;
    }

    private String normalizeKeyword(String value) {
        if (value == null) {
            return null;
        }
        String normalizedValue = value.trim().toLowerCase(Locale.ROOT);
        return normalizedValue.isBlank() ? null : normalizedValue;
    }

    private enum QuantityType {
        INBOUND,
        TRANSFER_INBOUND,
        COMPLETED_OUTBOUND,
        RESERVED_OUTBOUND
    }

    private static final class InventoryAccumulator {
        private final String itemCode;
        private final String itemName;
        private final String warehouseId;
        private final String warehouseName;
        private final String batchNumber;
        private final LocalDate expiryDate;
        private final String unit;
        private BigDecimal inboundQuantity = BigDecimal.ZERO;
        private BigDecimal completedOutboundQuantity = BigDecimal.ZERO;
        private BigDecimal reservedQuantity = BigDecimal.ZERO;
        private BigDecimal unitPrice = BigDecimal.ZERO;

        private InventoryAccumulator(WarehouseInventoryAggregateRow row) {
            this.itemCode = row.itemCode();
            this.itemName = row.itemName();
            this.warehouseId = row.warehouseId();
            this.warehouseName = row.warehouseName();
            this.batchNumber = row.batchNumber();
            this.expiryDate = row.expiryDate();
            this.unit = row.unit();
            this.unitPrice = row.unitPrice() != null ? row.unitPrice() : BigDecimal.ZERO;
        }

        private void addQuantity(QuantityType quantityType, BigDecimal quantity) {
            switch (quantityType) {
                case INBOUND, TRANSFER_INBOUND -> inboundQuantity = inboundQuantity.add(quantity);
                case COMPLETED_OUTBOUND -> completedOutboundQuantity = completedOutboundQuantity.add(quantity);
                case RESERVED_OUTBOUND -> reservedQuantity = reservedQuantity.add(quantity);
            }
        }

        private void updateUnitPrice(BigDecimal price) {
            if (price != null && price.compareTo(this.unitPrice) > 0) {
                this.unitPrice = price;
            }
        }
    }

    private record InventoryKey(
            String warehouseId,
            String itemId,
            String itemCode,
            String itemName,
            String batchNumber,
            LocalDate expiryDate,
            String unit
    ) {
        private static InventoryKey fromRow(WarehouseInventoryAggregateRow row) {
            return new InventoryKey(
                    normalize(row.warehouseId()),
                    normalize(row.itemId()),
                    normalize(row.itemCode()),
                    normalize(row.itemName()),
                    normalize(row.batchNumber()),
                    row.expiryDate(),
                    normalize(row.unit())
            );
        }

        private static InventoryKey fromRequest(String warehouseId, WarehouseOutboundUpsertRequest.WarehouseOutboundItemRequest request) {
            return new InventoryKey(
                    normalize(warehouseId),
                    normalize(request.itemId()),
                    normalize(request.itemCode()),
                    normalize(request.itemName()),
                    normalize(request.batchNumber()),
                    request.expiryDate(),
                    normalize(request.unit())
            );
        }

        private String stableId() {
            return WarehouseInventoryKeyUtils.buildKey(warehouseId, itemId, itemCode, batchNumber, expiryDate, unit);
        }

        private static String normalize(String value) {
            return WarehouseInventoryKeyUtils.normalize(value);
        }
    }
}
