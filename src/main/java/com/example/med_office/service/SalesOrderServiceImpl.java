package com.example.med_office.service;

import com.example.med_office.dto.SalesOrderDetailResponse;
import com.example.med_office.dto.SalesOrderListItemResponse;
import com.example.med_office.dto.SalesOrderMutationResponse;
import com.example.med_office.dto.SalesOrderPageResponse;
import com.example.med_office.dto.SalesOrderUpsertRequest;
import com.example.med_office.dto.WarehouseOutboundUpsertRequest;
import com.example.med_office.entity.SalesOrder;
import com.example.med_office.entity.SalesOrderItem;
import com.example.med_office.entity.SalesOrderStatus;
import com.example.med_office.entity.Warehouse;
import com.example.med_office.entity.WarehouseOutbound;
import com.example.med_office.entity.WarehouseOutboundItem;
import com.example.med_office.entity.WarehouseOutboundStatus;
import com.example.med_office.repository.SalesOrderRepository;
import com.example.med_office.repository.WarehouseOutboundRepository;
import com.example.med_office.repository.WarehouseRepository;
import com.example.med_office.service.einvoice.EInvoiceService;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class SalesOrderServiceImpl implements SalesOrderService {

    private static final Logger log = LoggerFactory.getLogger(SalesOrderServiceImpl.class);

    private final SalesOrderRepository salesOrderRepository;
    private final WarehouseRepository warehouseRepository;
    private final WarehouseOutboundRepository outboundRepository;
    private final WarehousePermissionScopeService warehousePermissionScopeService;
    private final WarehouseInventoryService warehouseInventoryService;
    private final EInvoiceService eInvoiceService;

    public SalesOrderServiceImpl(
            SalesOrderRepository salesOrderRepository,
            WarehouseRepository warehouseRepository,
            WarehouseOutboundRepository outboundRepository,
            WarehousePermissionScopeService warehousePermissionScopeService,
            WarehouseInventoryService warehouseInventoryService,
            EInvoiceService eInvoiceService
    ) {
        this.salesOrderRepository = salesOrderRepository;
        this.warehouseRepository = warehouseRepository;
        this.outboundRepository = outboundRepository;
        this.warehousePermissionScopeService = warehousePermissionScopeService;
        this.warehouseInventoryService = warehouseInventoryService;
        this.eInvoiceService = eInvoiceService;
    }

    @Override
    @Transactional(readOnly = true)
    public SalesOrderPageResponse findAll(
            int page,
            int size,
            String keyword,
            String status,
            String warehouseId,
            String paymentStatus,
            LocalDate fromDate,
            LocalDate toDate
    ) {
        validatePagination(page, size);
        String normalizedKeyword = normalizeKeyword(keyword);
        String normalizedWarehouseId = normalizeId(warehouseId);
        String normalizedStatus = normalizeKeyword(status);
        String normalizedPayment = normalizeKeyword(paymentStatus);
        Set<String> allowedWarehouseIds = warehousePermissionScopeService.resolveAllowedWarehouseIds();

        if (allowedWarehouseIds.isEmpty()) {
            return new SalesOrderPageResponse(List.of(), page, size, 0, 0);
        }

        if (normalizedWarehouseId != null && !allowedWarehouseIds.contains(normalizedWarehouseId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ban khong co quyen truy cap kho nay");
        }

        Specification<SalesOrder> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (normalizedWarehouseId != null) {
                predicates.add(cb.equal(root.get("warehouseId"), normalizedWarehouseId));
            } else {
                predicates.add(root.get("warehouseId").in(allowedWarehouseIds));
            }

            if (normalizedStatus != null) {
                try {
                    SalesOrderStatus enumStatus = SalesOrderStatus.valueOf(normalizedStatus.toUpperCase(Locale.ROOT));
                    predicates.add(cb.equal(root.get("status"), enumStatus));
                } catch (IllegalArgumentException ignored) {}
            }

            if (normalizedPayment != null) {
                predicates.add(cb.equal(cb.lower(root.get("paymentStatus")), normalizedPayment.toLowerCase(Locale.ROOT)));
            }

            if (fromDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("orderDate"), fromDate));
            }
            if (toDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("orderDate"), toDate));
            }

            if (normalizedKeyword != null) {
                String match = "%" + normalizedKeyword + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("code")), match),
                        cb.like(cb.lower(root.get("buyerCompany")), match),
                        cb.like(cb.lower(root.get("buyerName")), match)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<SalesOrder> resultPage = salesOrderRepository.findAll(
                spec,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        );

        List<SalesOrderListItemResponse> list = resultPage.getContent().stream()
                .map(this::toListItemResponse)
                .toList();

        return new SalesOrderPageResponse(
                list,
                resultPage.getNumber(),
                resultPage.getSize(),
                resultPage.getTotalElements(),
                resultPage.getTotalPages()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public SalesOrderDetailResponse findById(String id) {
        SalesOrder salesOrder = requireAccessibleOrder(id);
        return toDetailResponse(salesOrder);
    }

    @Override
    @Transactional
    public SalesOrderMutationResponse create(SalesOrderUpsertRequest request) {
        String resolvedCode = generateSalesOrderCode();
        SalesOrder salesOrder = new SalesOrder();
        updateOrderFromRequest(salesOrder, request, resolvedCode);
        SalesOrder saved = salesOrderRepository.save(salesOrder);
        return toMutationResponse(saved);
    }

    @Override
    @Transactional
    public SalesOrderMutationResponse updateDraft(String id, SalesOrderUpsertRequest request) {
        SalesOrder salesOrder = requireAccessibleOrderForUpdate(id);
        if (salesOrder.getStatus() != SalesOrderStatus.DRAFT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chi co the cap nhat don hang o trang thai NHAP");
        }
        updateOrderFromRequest(salesOrder, request, salesOrder.getCode());
        SalesOrder saved = salesOrderRepository.save(salesOrder);
        return toMutationResponse(saved);
    }

    @Override
    @Transactional
    public SalesOrderMutationResponse complete(String id) {
        SalesOrder salesOrder = requireAccessibleOrderForUpdate(id);
        if (salesOrder.getStatus() != SalesOrderStatus.DRAFT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chi co the hoan tat don hang o trang thai NHAP");
        }

        // 1. Verify stock availability
        List<WarehouseOutboundUpsertRequest.WarehouseOutboundItemRequest> outboundItemRequests = toOutboundItemRequests(salesOrder);
        warehouseInventoryService.assertSufficientAvailability(salesOrder.getWarehouseId(), outboundItemRequests, salesOrder.getOrderDate());

        // 2. Transition sales order state
        salesOrder.setStatus(SalesOrderStatus.COMPLETED);
        salesOrder.setPaymentStatus("PAID");

        // 3. Automatically create a completed WarehouseOutbound receipt to deduct stock
        WarehouseOutbound outbound = new WarehouseOutbound();
        outbound.setCode("PXK-" + salesOrder.getCode());
        outbound.setOutboundDate(salesOrder.getOrderDate());
        outbound.setStatus(WarehouseOutboundStatus.COMPLETED);
        outbound.setWarehouseId(salesOrder.getWarehouseId());
        outbound.setWarehouseName(salesOrder.getWarehouseName());
        outbound.setDestinationName("Bán hàng - " + (salesOrder.getBuyerCompany() != null ? salesOrder.getBuyerCompany() : salesOrder.getBuyerName()));
        outbound.setReceivedBy(salesOrder.getBuyerName());
        outbound.setNote("Tự động xuất kho cho đơn hàng " + salesOrder.getCode());
        outbound.setCompletedAt(Instant.now());
        outbound.setCreatedAt(Instant.now());
        outbound.setUpdatedAt(Instant.now());

        for (SalesOrderItem item : salesOrder.getItems()) {
            WarehouseOutboundItem outboundItem = new WarehouseOutboundItem();
            outboundItem.setItemId(item.getItemId());
            outboundItem.setItemCode(item.getItemCode());
            outboundItem.setItemName(item.getItemName());
            outboundItem.setUnit(item.getUnit());
            outboundItem.setQuantity(item.getQuantity());
            outboundItem.setUnitPrice(item.getUnitPrice());
            outboundItem.setLineTotal(item.getQuantity().multiply(item.getUnitPrice()));
            outboundItem.setBatchNumber(item.getBatchNumber());
            outboundItem.setExpiryDate(item.getExpiryDate());
            outboundItem.setNote(item.getNote());
            outbound.addItem(outboundItem);
        }

        WarehouseOutbound savedOutbound = outboundRepository.save(outbound);
        salesOrder.setWarehouseOutboundId(savedOutbound.getId());

        SalesOrder saved = salesOrderRepository.save(salesOrder);

        // 4. Trigger async MISA E-Invoice issuance
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(500);
                eInvoiceService.issueInvoice(saved);
            } catch (Exception e) {
                log.error("Failed to trigger async e-invoice issuance", e);
            }
        });

        return toMutationResponse(saved);
    }

    @Override
    @Transactional
    public SalesOrderMutationResponse issueInvoiceManually(String id) {
        SalesOrder salesOrder = requireAccessibleOrderForUpdate(id);
        if (salesOrder.getStatus() != SalesOrderStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chi phat hanh duoc hoa don cho don hang da hoan tat");
        }
        if ("PROCESSING".equals(salesOrder.getEInvoiceStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hoa don dang duoc xu ly, vui long cho trong giay lat");
        }

        salesOrder.setEInvoiceStatus("PROCESSING");
        SalesOrder saved = salesOrderRepository.save(salesOrder);

        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(100);
                eInvoiceService.issueInvoice(saved);
            } catch (Exception e) {
                log.error("Failed to trigger manual e-invoice issuance", e);
            }
        });

        return toMutationResponse(saved);
    }

    @Override
    @Transactional
    public void delete(String id) {
        SalesOrder salesOrder = requireAccessibleOrderForUpdate(id);
        if (salesOrder.getStatus() != SalesOrderStatus.DRAFT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chi co the xoa don hang o trang thai NHAP");
        }
        salesOrderRepository.delete(salesOrder);
    }

    private SalesOrder requireAccessibleOrder(String id) {
        SalesOrder salesOrder = salesOrderRepository.findById(trim(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Khong tim thay don ban hang"));
        Set<String> allowedWarehouseIds = warehousePermissionScopeService.resolveAllowedWarehouseIds();
        if (!allowedWarehouseIds.contains(salesOrder.getWarehouseId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ban khong co quyen truy cap don hang cua kho nay");
        }
        return salesOrder;
    }

    private SalesOrder requireAccessibleOrderForUpdate(String id) {
        SalesOrder salesOrder = salesOrderRepository.findByIdForUpdate(trim(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Khong tim thay don ban hang"));
        Set<String> allowedWarehouseIds = warehousePermissionScopeService.resolveAllowedWarehouseIds();
        if (!allowedWarehouseIds.contains(salesOrder.getWarehouseId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ban khong co quyen cap nhat don hang cua kho nay");
        }
        return salesOrder;
    }

    private void updateOrderFromRequest(SalesOrder salesOrder, SalesOrderUpsertRequest request, String resolvedCode) {
        Warehouse warehouse = warehouseRepository.findById(trim(request.warehouseId()))
                .filter(w -> "ACTIVE".equals(w.getStatus()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kho hang khong ton tai hoac da bi khoa"));

        salesOrder.setCode(trim(resolvedCode));
        salesOrder.setOrderDate(request.orderDate());
        salesOrder.setWarehouseId(warehouse.getId());
        salesOrder.setWarehouseName(warehouse.getName());
        salesOrder.setBuyerName(trim(request.buyerName()));
        salesOrder.setTaxCode(trim(request.taxCode()));
        salesOrder.setBuyerCompany(trim(request.buyerCompany()));
        salesOrder.setBuyerAddress(trim(request.buyerAddress()));
        salesOrder.setBuyerEmail(trim(request.buyerEmail()));
        salesOrder.setPaymentMethod(trim(request.paymentMethod()));
        salesOrder.setPaymentStatus(trim(request.paymentStatus()));
        salesOrder.setNote(trim(request.note()));

        salesOrder.clearItems();
        BigDecimal totalBeforeTax = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;
        BigDecimal totalAfterTax = BigDecimal.ZERO;

        for (SalesOrderUpsertRequest.SalesOrderItemRequest itemRequest : request.items()) {
            SalesOrderItem item = new SalesOrderItem();
            item.setItemId(normalizeNullable(itemRequest.itemId()));
            item.setItemCode(normalizeNullable(itemRequest.itemCode()));
            item.setItemName(trim(itemRequest.itemName()));
            item.setQuantity(itemRequest.quantity().stripTrailingZeros());
            item.setUnitPrice(itemRequest.unitPrice().stripTrailingZeros());
            item.setUnit(trim(itemRequest.unit()));
            item.setBatchNumber(trim(itemRequest.batchNumber()));
            item.setExpiryDate(itemRequest.expiryDate());
            item.setNote(trim(itemRequest.note()));

            BigDecimal vatRate = itemRequest.vatRate() != null ? itemRequest.vatRate() : BigDecimal.ZERO;
            item.setVatRate(vatRate);

            BigDecimal lineBeforeTax = item.getQuantity().multiply(item.getUnitPrice()).stripTrailingZeros();
            item.setLineTotalBeforeTax(lineBeforeTax);

            BigDecimal lineTax = lineBeforeTax.multiply(vatRate.divide(BigDecimal.valueOf(100))).stripTrailingZeros();
            item.setTaxAmount(lineTax);

            BigDecimal lineAfterTax = lineBeforeTax.add(lineTax).stripTrailingZeros();
            item.setLineTotalAfterTax(lineAfterTax);

            totalBeforeTax = totalBeforeTax.add(lineBeforeTax);
            totalTax = totalTax.add(lineTax);
            totalAfterTax = totalAfterTax.add(lineAfterTax);

            salesOrder.addItem(item);
        }

        salesOrder.setTotalAmountBeforeTax(totalBeforeTax.stripTrailingZeros());
        salesOrder.setTotalTaxAmount(totalTax.stripTrailingZeros());
        salesOrder.setTotalAmountAfterTax(totalAfterTax.stripTrailingZeros());
    }

    private String generateSalesOrderCode() {
        String dateStr = LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyMMdd"));
        String prefix = "DH" + dateStr;
        String maxCode = salesOrderRepository.findMaxCodeByPrefix(prefix);
        int nextSeq = 1;
        if (maxCode != null && maxCode.length() > prefix.length()) {
            try {
                String seqPart = maxCode.substring(prefix.length());
                nextSeq = Integer.parseInt(seqPart) + 1;
            } catch (NumberFormatException ignored) {}
        }
        return prefix + String.format("%04d", nextSeq);
    }

    private SalesOrderListItemResponse toListItemResponse(SalesOrder salesOrder) {
        return new SalesOrderListItemResponse(
                salesOrder.getId(),
                salesOrder.getCode(),
                salesOrder.getOrderDate().atStartOfDay().toInstant(ZoneOffset.UTC),
                salesOrder.getStatus(),
                salesOrder.getWarehouseId(),
                salesOrder.getWarehouseName(),
                salesOrder.getBuyerCompany(),
                salesOrder.getBuyerName(),
                salesOrder.getPaymentStatus(),
                salesOrder.getTotalAmountAfterTax(),
                salesOrder.getEInvoiceStatus()
        );
    }

    private SalesOrderMutationResponse toMutationResponse(SalesOrder salesOrder) {
        return new SalesOrderMutationResponse(
                salesOrder.getId(),
                salesOrder.getCode(),
                salesOrder.getStatus()
        );
    }

    private SalesOrderDetailResponse toDetailResponse(SalesOrder salesOrder) {
        return new SalesOrderDetailResponse(
                salesOrder.getId(),
                salesOrder.getCode(),
                salesOrder.getOrderDate(),
                salesOrder.getStatus(),
                salesOrder.getWarehouseId(),
                salesOrder.getWarehouseName(),
                salesOrder.getBuyerName(),
                salesOrder.getTaxCode(),
                salesOrder.getBuyerCompany(),
                salesOrder.getBuyerAddress(),
                salesOrder.getBuyerEmail(),
                salesOrder.getPaymentMethod(),
                salesOrder.getPaymentStatus(),
                salesOrder.getTotalAmountBeforeTax(),
                salesOrder.getTotalTaxAmount(),
                salesOrder.getTotalAmountAfterTax(),
                salesOrder.getEInvoiceStatus(),
                salesOrder.getEInvoiceNumber(),
                salesOrder.getEInvoiceLookupCode(),
                salesOrder.getEInvoiceUrl(),
                salesOrder.getEInvoiceErrorMessage(),
                salesOrder.getNote(),
                salesOrder.getWarehouseOutboundId(),
                salesOrder.getItems().stream()
                        .sorted((left, right) -> left.getId().compareTo(right.getId()))
                        .map(item -> new SalesOrderDetailResponse.SalesOrderItemDetailResponse(
                                item.getId(),
                                item.getItemId(),
                                item.getItemCode(),
                                item.getItemName(),
                                item.getUnit(),
                                item.getQuantity(),
                                item.getUnitPrice().stripTrailingZeros(),
                                item.getLineTotalBeforeTax().stripTrailingZeros(),
                                item.getVatRate().stripTrailingZeros(),
                                item.getTaxAmount().stripTrailingZeros(),
                                item.getLineTotalAfterTax().stripTrailingZeros(),
                                item.getBatchNumber(),
                                item.getExpiryDate(),
                                item.getNote()
                        ))
                        .toList()
        );
    }

    private List<WarehouseOutboundUpsertRequest.WarehouseOutboundItemRequest> toOutboundItemRequests(SalesOrder salesOrder) {
        return salesOrder.getItems().stream().map(item -> new WarehouseOutboundUpsertRequest.WarehouseOutboundItemRequest(
                item.getItemId(),
                item.getItemCode(),
                item.getItemName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getUnit(),
                item.getBatchNumber(),
                item.getExpiryDate(),
                item.getNote()
        )).toList();
    }

    private void validatePagination(int page, int size) {
        if (page < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page index must not be less than zero");
        }
        if (size <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page size must be greater than zero");
        }
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null || keyword.isBlank() || "ALL".equalsIgnoreCase(keyword)) {
            return null;
        }
        return keyword.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeId(String id) {
        if (id == null || id.isBlank() || "ALL".equalsIgnoreCase(id)) {
            return null;
        }
        return id.trim();
    }

    private String trim(String input) {
        if (input == null) return null;
        String val = input.trim();
        return val.isEmpty() ? null : val;
    }

    private String normalizeNullable(String input) {
        if (input == null) return null;
        String val = input.trim();
        return val.isEmpty() || "null".equalsIgnoreCase(val) ? null : val;
    }
}
