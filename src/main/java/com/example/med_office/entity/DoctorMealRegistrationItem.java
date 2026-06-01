package com.example.med_office.entity;

import com.example.med_office.utils.UuidUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "chi_tiet_dang_ky_bua_an_bac_si")
public class DoctorMealRegistrationItem {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dang_ky_id", nullable = false)
    private DoctorMealRegistration registration;

    @Column(name = "ngay_an", nullable = false)
    private LocalDate date;

    @Column(name = "thu_trong_tuan", length = 32)
    private String dayOfWeek;

    @Column(name = "ma_bua_an", length = 32)
    private String mealId;

    @Column(name = "ten_bua_an", length = 32)
    private String mealLabel;

    @Column(name = "so_luong_bua_an", nullable = false)
    private Integer mealQuantity;

    @Column(name = "thanh_tien_bua_an", nullable = false, precision = 12, scale = 2)
    private BigDecimal mealAmount;

    @OneToMany(mappedBy = "registrationItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DoctorMealRegistrationItemSnapshot> mealSnapshots = new ArrayList<>();

    public void addMealSnapshot(DoctorMealRegistrationItemSnapshot snapshot) {
        snapshot.setRegistrationItem(this);
        mealSnapshots.add(snapshot);
    }

    @PrePersist
    public void prePersist() {
        if (id == null || id.isBlank()) {
            id = UuidUtils.newUuid();
        }
    }
}
