package com.example.med_office.entity;

import com.example.med_office.utils.UuidUtils;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "mon_an_trong_dang_ky_bua_an_bac_si")
public class DoctorMealRegistrationItemSnapshot {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chi_tiet_dang_ky_id", nullable = false)
    private DoctorMealRegistrationItem registrationItem;

    @Column(name = "ten_mon_an", nullable = false, length = 255)
    private String name;

    @Column(name = "gio_phuc_vu", length = 32)
    private String servingTime;

    @Column(name = "so_luong", nullable = false)
    private Integer quantity;

    @Column(name = "don_gia", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "thanh_tien", nullable = false, precision = 12, scale = 2)
    private BigDecimal lineTotal;

    @PrePersist
    public void prePersist() {
        if (id == null || id.isBlank()) {
            id = UuidUtils.newUuid();
        }
    }
}
