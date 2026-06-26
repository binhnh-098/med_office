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
@Table(name = "assets")
public class Asset {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "category", nullable = false, length = 100)
    private String category;

    @Column(name = "unit", nullable = false, length = 50)
    private String unit;

    @Column(name = "model", length = 100)
    private String model;

    @Column(name = "serial_number", length = 100)
    private String serialNumber;

    @Column(name = "brand", length = 100)
    private String brand;

    @Column(name = "manufacturer", length = 100)
    private String manufacturer;

    @Column(name = "image", columnDefinition = "LONGTEXT")
    private String image;

    @Column(name = "specification", length = 1000)
    private String specification;

    @Column(name = "purchase_price", precision = 18, scale = 2)
    private BigDecimal purchasePrice;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column(name = "status", nullable = false, length = 50)
    private String status; // e.g. ACTIVE, INACTIVE, MAINTENANCE, LIQUIDATED

    @Column(name = "current_employee_id", length = 36)
    private String currentEmployeeId;

    @Column(name = "current_department", length = 255)
    private String currentDepartment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_employee_id", insertable = false, updatable = false)
    private HoSoNhanVien currentEmployee;

    @Column(name = "description", length = 1000)
    private String description;

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
