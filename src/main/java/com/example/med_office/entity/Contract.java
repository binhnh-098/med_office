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
@Table(name = "contracts")
public class Contract {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "ho_so_nhan_vien_id", nullable = false, length = 36)
    private String hoSoNhanVienId;

    @Column(name = "contract_number", nullable = false, unique = true, length = 100)
    private String contractNumber;

    @Column(name = "contract_type", nullable = false, length = 100)
    private String contractType; // e.g. THU_VIEC, XAC_DINH_THOI_HAN, KHONG_THOI_HAN

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate; // null for permanent contracts

    @Column(name = "salary", nullable = false, precision = 18, scale = 2)
    private BigDecimal salary;

    @Column(name = "status", nullable = false, length = 50)
    private String status; // ACTIVE, EXPIRED, EXPIRING_SOON

    @Column(name = "note", length = 2000)
    private String note;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UuidUtils.newUuid();
        }
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = "ACTIVE";
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
