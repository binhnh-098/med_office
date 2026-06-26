package com.example.med_office.entity;

import com.example.med_office.utils.UuidUtils;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "asset_liquidations")
public class AssetLiquidation {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "asset_id", nullable = false, length = 36)
    private String assetId;

    @Column(name = "liquidation_date", nullable = false)
    private LocalDate liquidationDate;

    @Column(name = "price", precision = 15, scale = 2)
    private BigDecimal price;

    @Column(name = "document_number", length = 100)
    private String documentNumber;

    @Column(name = "reason", nullable = false, length = 1000)
    private String reason;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "prior_status", nullable = false, length = 50)
    private String priorStatus;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", insertable = false, updatable = false)
    private Asset asset;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UuidUtils.newUuid();
        }
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
