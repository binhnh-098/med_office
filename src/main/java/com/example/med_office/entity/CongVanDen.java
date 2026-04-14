package com.example.med_office.entity;

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

    public String getSoDen() {
        return soDen;
    }

    public void setSoDen(String soDen) {
        this.soDen = soDen;
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

    public String getDonViGui() {
        return donViGui;
    }

    public void setDonViGui(String donViGui) {
        this.donViGui = donViGui;
    }

    public Integer getDonViGuiId() {
        return donViGuiId;
    }

    public void setDonViGuiId(Integer donViGuiId) {
        this.donViGuiId = donViGuiId;
    }

    public String getNguoiKy() {
        return nguoiKy;
    }

    public void setNguoiKy(String nguoiKy) {
        this.nguoiKy = nguoiKy;
    }

    public LocalDate getNgayVanBan() {
        return ngayVanBan;
    }

    public void setNgayVanBan(LocalDate ngayVanBan) {
        this.ngayVanBan = ngayVanBan;
    }

    public LocalDate getNgayNhan() {
        return ngayNhan;
    }

    public void setNgayNhan(LocalDate ngayNhan) {
        this.ngayNhan = ngayNhan;
    }

    public String getMucDoKhan() {
        return mucDoKhan;
    }

    public void setMucDoKhan(String mucDoKhan) {
        this.mucDoKhan = mucDoKhan;
    }

    public String getMucDoMat() {
        return mucDoMat;
    }

    public void setMucDoMat(String mucDoMat) {
        this.mucDoMat = mucDoMat;
    }

    public String getNguonNhan() {
        return nguonNhan;
    }

    public void setNguonNhan(String nguonNhan) {
        this.nguonNhan = nguonNhan;
    }

    public Integer getPhongBanXuLyId() {
        return phongBanXuLyId;
    }

    public void setPhongBanXuLyId(Integer phongBanXuLyId) {
        this.phongBanXuLyId = phongBanXuLyId;
    }

    public Integer getNguoiXuLyId() {
        return nguoiXuLyId;
    }

    public void setNguoiXuLyId(Integer nguoiXuLyId) {
        this.nguoiXuLyId = nguoiXuLyId;
    }

    public LocalDate getHanXuLy() {
        return hanXuLy;
    }

    public void setHanXuLy(LocalDate hanXuLy) {
        this.hanXuLy = hanXuLy;
    }

    public String getDoKhanXuLy() {
        return doKhanXuLy;
    }

    public void setDoKhanXuLy(String doKhanXuLy) {
        this.doKhanXuLy = doKhanXuLy;
    }

    public Integer getLoaiVanBanId() {
        return loaiVanBanId;
    }

    public void setLoaiVanBanId(Integer loaiVanBanId) {
        this.loaiVanBanId = loaiVanBanId;
    }

    public Integer getLinhVucId() {
        return linhVucId;
    }

    public void setLinhVucId(Integer linhVucId) {
        this.linhVucId = linhVucId;
    }

    public Integer getHoSoId() {
        return hoSoId;
    }

    public void setHoSoId(Integer hoSoId) {
        this.hoSoId = hoSoId;
    }

    public Integer getSoTrang() {
        return soTrang;
    }

    public void setSoTrang(Integer soTrang) {
        this.soTrang = soTrang;
    }

    public Integer getSoBan() {
        return soBan;
    }

    public void setSoBan(Integer soBan) {
        this.soBan = soBan;
    }

    public String getTrichYeu() {
        return trichYeu;
    }

    public void setTrichYeu(String trichYeu) {
        this.trichYeu = trichYeu;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public String getYKienChiDao() {
        return yKienChiDao;
    }

    public void setYKienChiDao(String yKienChiDao) {
        this.yKienChiDao = yKienChiDao;
    }

    public String getTepDinhKemChinh() {
        return tepDinhKemChinh;
    }

    public void setTepDinhKemChinh(String tepDinhKemChinh) {
        this.tepDinhKemChinh = tepDinhKemChinh;
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

    public Boolean getDaDoc() {
        return daDoc;
    }

    public void setDaDoc(Boolean daDoc) {
        this.daDoc = daDoc;
    }

    public Boolean getDaXuLy() {
        return daXuLy;
    }

    public void setDaXuLy(Boolean daXuLy) {
        this.daXuLy = daXuLy;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public Integer getNguoiTaoId() {
        return nguoiTaoId;
    }

    public void setNguoiTaoId(Integer nguoiTaoId) {
        this.nguoiTaoId = nguoiTaoId;
    }

    public Integer getNguoiCapNhatId() {
        return nguoiCapNhatId;
    }

    public void setNguoiCapNhatId(Integer nguoiCapNhatId) {
        this.nguoiCapNhatId = nguoiCapNhatId;
    }
}
