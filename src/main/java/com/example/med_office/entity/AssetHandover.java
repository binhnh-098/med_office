package com.example.med_office.entity;

import com.example.med_office.utils.UuidUtils;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "asset_handovers")
public class AssetHandover {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "asset_id", nullable = false, length = 36)
    private String assetId;

    @Column(name = "type", nullable = false, length = 50)
    private String type; // HANDOVER, TRANSFER, RECLAIM

    @Column(name = "from_employee_id", length = 36)
    private String fromEmployeeId;

    @Column(name = "to_employee_id", length = 36)
    private String toEmployeeId;

    @Column(name = "from_department", length = 255)
    private String fromDepartment;

    @Column(name = "to_department", length = 255)
    private String toDepartment;

    @Column(name = "handover_date", nullable = false)
    private LocalDate handoverDate;

    @Column(name = "document_number", length = 100)
    private String documentNumber;

    @Column(name = "note", length = 1000)
    private String note;

    @Column(name = "status", nullable = false, length = 50)
    private String status; // PENDING, COMPLETED, CANCELLED

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", insertable = false, updatable = false)
    private Asset asset;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_employee_id", insertable = false, updatable = false)
    private HoSoNhanVien fromEmployee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_employee_id", insertable = false, updatable = false)
    private HoSoNhanVien toEmployee;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UuidUtils.newUuid();
        }
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = "COMPLETED";
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
