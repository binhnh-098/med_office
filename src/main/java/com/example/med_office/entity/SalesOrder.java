package com.example.med_office.entity;

import com.example.med_office.utils.UuidUtils;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(
        name = "sales_orders",
        indexes = {
                @Index(name = "idx_sales_orders_warehouse_id", columnList = "warehouse_id"),
                @Index(name = "idx_sales_orders_status", columnList = "status"),
                @Index(name = "idx_sales_orders_order_date", columnList = "order_date")
        }
)
public class SalesOrder {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private SalesOrderStatus status = SalesOrderStatus.DRAFT;

    @Column(name = "warehouse_id", nullable = false, length = 36)
    private String warehouseId;

    @Column(name = "warehouse_name", nullable = false, length = 255)
    private String warehouseName;

    @Column(name = "buyer_name", length = 255)
    private String buyerName;

    @Column(name = "tax_code", length = 50)
    private String taxCode;

    @Column(name = "buyer_company", length = 255)
    private String buyerCompany;

    @Column(name = "buyer_address", length = 500)
    private String buyerAddress;

    @Column(name = "buyer_email", length = 255)
    private String buyerEmail;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "payment_status", nullable = false, length = 30)
    private String paymentStatus = "UNPAID";

    @Column(name = "total_amount_before_tax", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalAmountBeforeTax = BigDecimal.ZERO;

    @Column(name = "total_tax_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalTaxAmount = BigDecimal.ZERO;

    @Column(name = "total_amount_after_tax", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalAmountAfterTax = BigDecimal.ZERO;

    @Column(name = "e_invoice_status", nullable = false, length = 30)
    private String eInvoiceStatus = "NOT_ISSUED";

    @Column(name = "e_invoice_number", length = 50)
    private String eInvoiceNumber;

    @Column(name = "e_invoice_lookup_code", length = 50)
    private String eInvoiceLookupCode;

    @Column(name = "e_invoice_url", length = 500)
    private String eInvoiceUrl;

    @Column(name = "e_invoice_error_message", length = 1000)
    private String eInvoiceErrorMessage;

    @Column(name = "note", length = 2000)
    private String note;

    @Column(name = "warehouse_outbound_id", length = 36)
    private String warehouseOutboundId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(mappedBy = "salesOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SalesOrderItem> items = new ArrayList<>();

    public void addItem(SalesOrderItem item) {
        items.add(item);
        item.setSalesOrder(this);
    }

    public void clearItems() {
        items.forEach(item -> item.setSalesOrder(null));
        items.clear();
    }

    @PrePersist
    public void prePersist() {
        if (id == null || id.isBlank()) {
            id = UuidUtils.newUuid();
        }
        createdAt = Instant.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }
}
