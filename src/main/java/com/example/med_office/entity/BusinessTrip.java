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
@Table(name = "business_trips")
public class BusinessTrip {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "ho_so_nhan_vien_id", nullable = false, length = 36)
    private String hoSoNhanVienId;

    @Column(name = "destination", nullable = false, length = 255)
    private String destination;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "purpose", nullable = false, length = 1000)
    private String purpose;

    @Column(name = "status", nullable = false, length = 50)
    private String status; // DRAFT, PENDING_APPROVAL, APPROVED, REJECTED

    @Column(name = "approver_id", length = 36)
    private String approverId;

    @Column(name = "reject_reason", length = 1000)
    private String rejectReason;

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
            this.status = "DRAFT";
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
