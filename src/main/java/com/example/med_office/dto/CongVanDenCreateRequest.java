package com.example.med_office.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonAlias;
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

    @NotBlank
    @Size(max = 100, message = "Nguon nhan must not exceed 100 characters")
    @JsonAlias("nguon_nhan")
    private String nguonNhan;

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
}
