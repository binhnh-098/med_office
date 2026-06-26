package com.example.med_office.entity;

import com.example.med_office.utils.UuidUtils;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "vehicle_requests")
public class VehicleRequest {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "ho_so_nhan_vien_id", nullable = false, length = 36)
    private String hoSoNhanVienId;

    @Column(name = "vehicle_id", length = 36)
    private String vehicleId;

    @Column(name = "vehicle_type", nullable = false, length = 50)
    private String vehicleType;

    @Column(name = "departure_time", nullable = false)
    private LocalDateTime departureTime;

    @Column(name = "return_time", nullable = false)
    private LocalDateTime returnTime;

    @Column(name = "route_description", nullable = false, length = 1000)
    private String routeDescription;

    @Column(name = "passenger_count", nullable = false)
    private Integer passengerCount;

    @Column(name = "purpose", nullable = false, length = 1000)
    private String purpose;

    @Column(name = "status", nullable = false, length = 50)
    private String status; // DRAFT, PENDING_APPROVAL, APPROVED, REJECTED

    @Column(name = "approver_id", length = 36)
    private String approverId;

    @Column(name = "driver_name", length = 255)
    private String driverName;

    @Column(name = "driver_phone", length = 50)
    private String driverPhone;

    @Column(name = "license_plate", length = 50)
    private String licensePlate;

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
        if (this.passengerCount == null) {
            this.passengerCount = 1;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
