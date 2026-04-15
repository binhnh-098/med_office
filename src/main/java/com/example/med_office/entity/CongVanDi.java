package com.example.med_office.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cong_van_di")
public class CongVanDi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Column(name = "nguoi_ky_id")
    private Integer nguoiKyId;

    @Column(name = "trang_thai", length = 50)
    private String trangThai;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSoCongVan() {
        return soCongVan;
    }

    public void setSoCongVan(String soCongVan) {
        this.soCongVan = soCongVan;
    }

    public String getTieuDe() {
        return tieuDe;
    }

    public void setTieuDe(String tieuDe) {
        this.tieuDe = tieuDe;
    }

    public String getNoiDungTomTat() {
        return noiDungTomTat;
    }

    public void setNoiDungTomTat(String noiDungTomTat) {
        this.noiDungTomTat = noiDungTomTat;
    }

    public String getDonViNhan() {
        return donViNhan;
    }

    public void setDonViNhan(String donViNhan) {
        this.donViNhan = donViNhan;
    }

    public LocalDate getNgayBanHanh() {
        return ngayBanHanh;
    }

    public void setNgayBanHanh(LocalDate ngayBanHanh) {
        this.ngayBanHanh = ngayBanHanh;
    }

    public Integer getNguoiKyId() {
        return nguoiKyId;
    }

    public void setNguoiKyId(Integer nguoiKyId) {
        this.nguoiKyId = nguoiKyId;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }

    public LocalDateTime getNgayCapNhat() {
        return ngayCapNhat;
    }

    public void setNgayCapNhat(LocalDateTime ngayCapNhat) {
        this.ngayCapNhat = ngayCapNhat;
    }
}
