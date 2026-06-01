package com.example.med_office.entity;

import com.example.med_office.utils.UuidUtils;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "cong_van_di")
public class CongVanDi {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "so_cong_van", nullable = false, length = 100)
    private String soCongVan;

    @Column(name = "tieu_de", nullable = false, length = 500)
    private String tieuDe;

    @Column(name = "noi_dung_tom_tat", length = 2000)
    private String noiDungTomTat;

    @Column(name = "don_vi_nhan", length = 255)
    private String donViNhan;

    @Column(name = "ngay_ban_hanh")
    private LocalDate ngayBanHanh;

    @Column(name = "nguoi_ky_id", length = 36)
    private String nguoiKyId;

    @Column(name = "trang_thai", length = 50)
    private String trangThai;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (id == null || id.isBlank()) {
            id = UuidUtils.newUuid();
        }
        if (ngayTao == null) {
            ngayTao = now;
        }
        if (ngayCapNhat == null) {
            ngayCapNhat = now;
        }
    }

    @PreUpdate
    public void preUpdate() {
        ngayCapNhat = LocalDateTime.now();
    }
}
