package com.example.med_office.entity;

import com.example.med_office.utils.UuidUtils;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "mon_an_bac_si")
public class DoctorMealDish {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "nam_tuan", nullable = false)
    private Integer weekYear;

    @Column(name = "so_tuan", nullable = false)
    private Integer weekNumber;

    @Column(name = "thu_trong_tuan", nullable = false, length = 32)
    private String dayOfWeek;

    @Column(name = "ngay_an", nullable = false)
    private LocalDate date;

    @Column(name = "ma_bua_an", nullable = false, length = 32)
    private String mealId;

    @Column(name = "ten_bua_an", nullable = false, length = 32)
    private String mealLabel;

    @Column(name = "ten_mon_an", nullable = false, length = 255)
    private String name;

    @Column(name = "gia", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "don_gia", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "calo")
    private Integer calories;

    @Column(name = "gio_phuc_vu", length = 32)
    private String servingTime;

    @Column(name = "ghi_chu", length = 1000)
    private String note;

    @Column(name = "nguoi_tao", nullable = false, length = 128)
    private String createdBy;

    @Column(name = "ngay_tao", nullable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (id == null || id.isBlank()) {
            id = UuidUtils.newUuid();
        }
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
