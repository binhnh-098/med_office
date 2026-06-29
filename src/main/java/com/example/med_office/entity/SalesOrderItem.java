package com.example.med_office.entity;

import com.example.med_office.utils.UuidUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(
        name = "sales_order_items",
        indexes = {
                @Index(name = "idx_sales_order_items_order_id", columnList = "sales_order_id"),
                @Index(name = "idx_sales_order_items_item_code", columnList = "item_code"),
                @Index(name = "idx_sales_order_items_item_name", columnList = "item_name")
        }
)
public class SalesOrderItem {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sales_order_id", nullable = false)
    private SalesOrder salesOrder;

    @Column(name = "item_id", length = 100)
    private String itemId;

    @Column(name = "item_code", length = 100)
    private String itemCode;

    @Column(name = "item_name", nullable = false, length = 255)
    private String itemName;

    @Column(name = "unit", length = 100)
    private String unit;

    @Column(name = "quantity", nullable = false, precision = 18, scale = 2)
    private BigDecimal quantity;

    @Column(name = "unit_price", nullable = false, precision = 18, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "line_total_before_tax", nullable = false, precision = 18, scale = 2)
    private BigDecimal lineTotalBeforeTax;

    @Column(name = "vat_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal vatRate = BigDecimal.ZERO;

    @Column(name = "tax_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "line_total_after_tax", nullable = false, precision = 18, scale = 2)
    private BigDecimal lineTotalAfterTax;

    @Column(name = "batch_number", length = 100)
    private String batchNumber;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "note", length = 1000)
    private String note;

    @PrePersist
    public void prePersist() {
        if (id == null || id.isBlank()) {
            id = UuidUtils.newUuid();
        }
    }
}
