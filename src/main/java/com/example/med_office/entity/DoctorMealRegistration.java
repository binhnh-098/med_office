package com.example.med_office.entity;

import com.example.med_office.utils.UuidUtils;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "dang_ky_bua_an_bac_si")
public class DoctorMealRegistration {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "nam_tuan", nullable = false)
    private Integer weekYear;

    @Column(name = "so_tuan", nullable = false)
    private Integer weekNumber;

    @Column(name = "nhan_tuan", length = 64)
    private String weekLabel;

    @Column(name = "ngay_bat_dau_tuan")
    private LocalDate weekStartDate;

    @Column(name = "ngay_ket_thuc_tuan")
    private LocalDate weekEndDate;

    @Column(name = "ten_dang_nhap", nullable = false, length = 128)
    private String username;

    @Column(name = "ten_dang_nhap_nguoi_dang_ky", length = 128)
    private String requesterUsername;

    @Column(name = "ho_ten_nguoi_dang_ky", length = 255)
    private String requesterFullName;

    @Column(name = "phong_ban_nguoi_dang_ky", length = 255)
    private String requesterDepartment;

    @Column(name = "vai_tro_nguoi_dang_ky", length = 128)
    private String requesterRole;

    @Column(name = "tong_so_luong")
    private Integer totalQuantity = 0;

    @Column(name = "tong_tien", precision = 12, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "du_lieu_json", nullable = false, columnDefinition = "LONGTEXT")
    private String payloadJson;

    @Column(name = "ngay_tao", nullable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "registration", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DoctorMealRegistrationItem> items = new ArrayList<>();

    public void addItem(DoctorMealRegistrationItem item) {
        item.setRegistration(this);
        items.add(item);
    }

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
