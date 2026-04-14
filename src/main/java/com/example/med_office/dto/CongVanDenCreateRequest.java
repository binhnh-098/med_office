package com.example.med_office.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(name = "CongVanDenCreateRequest")
public class CongVanDenCreateRequest {

    @NotBlank
    @Size(max = 100, message = "So cong van must not exceed 100 characters")
    @JsonAlias("so_cong_van")
    private String soCongVan;

    @NotBlank
    @Size(max = 100, message = "So den must not exceed 100 characters")
    @JsonAlias("so_den")
    private String soDen;

    @NotBlank
    @Size(max = 500, message = "Tieu de must not exceed 500 characters")
    @JsonAlias("tieu_de")
    private String tieuDe;

    @Size(max = 2000, message = "Noi dung tom tat must not exceed 2000 characters")
    @JsonAlias("noi_dung_tom_tat")
    private String noiDungTomTat;

    @NotNull
    @Positive(message = "Don vi gui id must be greater than 0")
    @JsonAlias("don_vi_gui_id")
    private Integer donViGuiId;

    @Size(max = 255, message = "Nguoi ky must not exceed 255 characters")
    @JsonAlias("nguoi_ky")
    private String nguoiKy;

    @NotNull
    @JsonAlias("ngay_van_ban")
    private LocalDate ngayVanBan;

    @NotNull
    @JsonAlias("ngay_nhan")
    private LocalDate ngayNhan;

    @NotBlank
    @Size(max = 50, message = "Muc do khan must not exceed 50 characters")
    @JsonAlias("muc_do_khan")
    private String mucDoKhan;

    @NotBlank
    @Size(max = 50, message = "Muc do mat must not exceed 50 characters")
    @JsonAlias("muc_do_mat")
    private String mucDoMat;

    @JsonAlias("phong_ban_xu_ly_id")
    private Integer phongBanXuLyId;

    @JsonAlias("nguoi_xu_ly_id")
    private Integer nguoiXuLyId;

    @NotBlank
    @Size(max = 100, message = "Nguon nhan must not exceed 100 characters")
    @JsonAlias("nguon_nhan")
    private String nguonNhan;

    @JsonAlias("han_xu_ly")
    private LocalDate hanXuLy;

    @Size(max = 50, message = "Do khan xu ly must not exceed 50 characters")
    @JsonAlias("do_khan_xu_ly")
    private String doKhanXuLy;

    @JsonAlias("loai_van_ban_id")
    private Integer loaiVanBanId;

    @JsonAlias("linh_vuc_id")
    private Integer linhVucId;

    @JsonAlias("ho_so_id")
    private Integer hoSoId;

    @JsonAlias("so_trang")
    private Integer soTrang;

    @JsonAlias("so_ban")
    private Integer soBan;

    @Size(max = 2000, message = "Trich yeu must not exceed 2000 characters")
    @JsonAlias("trich_yeu")
    private String trichYeu;

    @Size(max = 2000, message = "Ghi chu must not exceed 2000 characters")
    @JsonAlias("ghi_chu")
    private String ghiChu;

    @Size(max = 2000, message = "Y kien chi dao must not exceed 2000 characters")
    @JsonProperty("yKienChiDao")
    @JsonAlias("y_kien_chi_dao")
    private String yKienChiDao;

    @Size(max = 1000, message = "Tep dinh kem chinh must not exceed 1000 characters")
    @JsonAlias("tep_dinh_kem_chinh")
    private String tepDinhKemChinh;

    @JsonAlias("da_doc")
    private Boolean daDoc;

    @JsonAlias("da_xu_ly")
    private Boolean daXuLy;

    @JsonAlias("is_deleted")
    private Boolean isDeleted;

    @JsonAlias("nguoi_tao_id")
    private Integer nguoiTaoId;

    @JsonAlias("nguoi_cap_nhat_id")
    private Integer nguoiCapNhatId;

    @Size(max = 50, message = "Trang thai must not exceed 50 characters")
    @JsonAlias("trang_thai")
    private String trangThai;

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

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}
