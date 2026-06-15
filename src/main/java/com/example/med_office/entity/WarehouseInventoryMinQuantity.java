package com.example.med_office.entity;

import com.example.med_office.utils.UuidUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(
        name = "warehouse_inventory_min_quantities",
        indexes = {
                @Index(name = "idx_warehouse_inventory_min_quantities_warehouse_id", columnList = "warehouse_id"),
                @Index(name = "idx_warehouse_inventory_min_quantities_inventory_key", columnList = "inventory_key", unique = true)
        }
)
public class WarehouseInventoryMinQuantity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "inventory_key", nullable = false, unique = true, length = 512)
    private String inventoryKey;

    @Column(name = "warehouse_id", nullable = false, length = 36)
    private String warehouseId;

    @Column(name = "item_id", length = 100)
    private String itemId;

    @Column(name = "item_code", length = 100)
    private String itemCode;

    @Column(name = "batch_number", length = 100)
    private String batchNumber;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "unit", length = 100)
    private String unit;

    @Column(name = "min_quantity", precision = 18, scale = 2)
    private BigDecimal minQuantity;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        if (id == null || id.isBlank()) {
            id = UuidUtils.newUuid();
        }
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }
}
