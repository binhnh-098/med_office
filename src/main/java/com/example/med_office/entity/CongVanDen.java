package com.example.med_office.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "cong_van_den")
public class CongVanDen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "so_cong_van", nullable = false, length = 100)
    private String soCongVan;

    @Column(name = "so_den", nullable = false, length = 100)
    private String soDen;

    @Column(name = "tieu_de", nullable = false, length = 500)
    private String tieuDe;

    @Column(name = "noi_dung_tom_tat", length = 2000)
    private String noiDungTomTat;

    @Column(name = "don_vi_gui", length = 255)
    private String donViGui;

    @Column(name = "don_vi_gui_id")
    private Integer donViGuiId;

    @Column(name = "nguoi_ky", length = 255)
    private String nguoiKy;

    @Column(name = "ngay_van_ban")
    private LocalDate ngayVanBan;

    @Column(name = "ngay_nhan")
    private LocalDate ngayNhan;

    @Column(name = "muc_do_khan", length = 50)
    private String mucDoKhan;

    @Column(name = "muc_do_mat", length = 50)
    private String mucDoMat;

    @Column(name = "phong_ban_xu_ly_id")
    private Integer phongBanXuLyId;

    @Column(name = "nguoi_xu_ly_id")
    private Integer nguoiXuLyId;

    @Column(name = "nguon_nhan", length = 100)
    private String nguonNhan;

    @Column(name = "han_xu_ly")
    private LocalDate hanXuLy;

    @Column(name = "do_khan_xu_ly", length = 50)
    private String doKhanXuLy;

    @Column(name = "loai_van_ban_id")
    private Integer loaiVanBanId;

    @Column(name = "linh_vuc_id")
    private Integer linhVucId;

    @Column(name = "ho_so_id")
    private Integer hoSoId;

    @Column(name = "so_trang")
    private Integer soTrang;

    @Column(name = "so_ban")
    private Integer soBan;

    @Column(name = "trich_yeu", length = 2000)
    private String trichYeu;

    @Column(name = "ghi_chu", length = 2000)
    private String ghiChu;

    @Column(name = "y_kien_chi_dao", length = 2000)
    private String yKienChiDao;

    @Column(name = "tep_dinh_kem_chinh", length = 1000)
    private String tepDinhKemChinh;

    @Column(name = "trang_thai", length = 50)
    private String trangThai;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @Column(name = "da_doc")
    private Boolean daDoc;

    @Column(name = "da_xu_ly")
    private Boolean daXuLy;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "nguoi_tao_id")
    private Integer nguoiTaoId;

    @Column(name = "nguoi_cap_nhat_id")
    private Integer nguoiCapNhatId;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (trangThai == null || trangThai.isBlank()) {
            trangThai = "MOI_TIEP_NHAN";
        }
        if (ngayTao == null) {
            ngayTao = now;
        }
        if (ngayCapNhat == null) {
            ngayCapNhat = now;
        }
        if (daDoc == null) {
            daDoc = false;
        }
        if (daXuLy == null) {
            daXuLy = false;
        }
        if (isDeleted == null) {
            isDeleted = false;
        }
    }

    @PreUpdate
    public void preUpdate() {
        ngayCapNhat = LocalDateTime.now();
    }
}
