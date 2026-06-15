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

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(
        name = "warehouse_outbounds",
        indexes = {
                @Index(name = "idx_warehouse_outbounds_warehouse_id", columnList = "warehouse_id"),
                @Index(name = "idx_warehouse_outbounds_destination_warehouse_id", columnList = "destination_warehouse_id"),
                @Index(name = "idx_warehouse_outbounds_status", columnList = "status"),
                @Index(name = "idx_warehouse_outbounds_outbound_date", columnList = "outbound_date")
        }
)
public class WarehouseOutbound {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "outbound_date", nullable = false)
    private LocalDate outboundDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private WarehouseOutboundStatus status;

    @Column(name = "warehouse_id", nullable = false, length = 36)
    private String warehouseId;

    @Column(name = "warehouse_name", nullable = false, length = 255)
    private String warehouseName;

    @Column(name = "destination_warehouse_id", length = 36)
    private String destinationWarehouseId;

    @Column(name = "destination_name", length = 255)
    private String destinationName;

    @Column(name = "received_by", length = 255)
    private String receivedBy;

    @Column(name = "requested_by", length = 255)
    private String requestedBy;

    @Column(name = "note", length = 2000)
    private String note;

    @Column(name = "approval_note", length = 2000)
    private String approvalNote;

    @Column(name = "rejection_reason", length = 2000)
    private String rejectionReason;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(mappedBy = "warehouseOutbound", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WarehouseOutboundItem> items = new ArrayList<>();

    public void addItem(WarehouseOutboundItem item) {
        item.setWarehouseOutbound(this);
        items.add(item);
    }

    public void clearItems() {
        items.clear();
    }

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        if (id == null || id.isBlank()) {
            id = UuidUtils.newUuid();
        }
        if (status == null) {
            status = WarehouseOutboundStatus.DRAFT;
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
