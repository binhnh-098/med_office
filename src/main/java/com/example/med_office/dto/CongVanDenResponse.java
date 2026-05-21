package com.example.med_office.dto;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Schema(name = "CongVanDenResponse")
public class CongVanDenResponse {

    private final Long id;
    private final String soCongVan;
    private final String soDen;
    private final String tieuDe;
    private final String noiDungTomTat;
    private final Integer donViGuiId;
    private final String donViGui;
    private final String nguoiKy;
    private final LocalDate ngayVanBan;
    private final LocalDate ngayNhan;
    private final String mucDoKhan;
    private final String mucDoMat;
    private final Integer phongBanXuLyId;
    private final Integer nguoiXuLyId;
    private final String nguonNhan;
    private final LocalDate hanXuLy;
    private final String doKhanXuLy;
    private final Integer loaiVanBanId;
    private final Integer linhVucId;
    private final Integer hoSoId;
    private final Integer soTrang;
    private final Integer soBan;
    private final String trichYeu;
    private final String ghiChu;
    @JsonProperty("yKienChiDao")
    private final String yKienChiDao;
    private final String tepDinhKemChinh;
    private final String trangThai;
    private final LocalDateTime ngayTao;
    private final LocalDateTime ngayCapNhat;
    private final Boolean daDoc;
    private final Boolean daXuLy;
    private final Boolean isDeleted;
    private final Integer nguoiTaoId;
    private final Integer nguoiCapNhatId;

    public CongVanDenResponse(
            Long id,
            String soCongVan,
            String soDen,
            String tieuDe,
            String noiDungTomTat,
            Integer donViGuiId,
            String donViGui,
            String nguoiKy,
            LocalDate ngayVanBan,
            LocalDate ngayNhan,
            String mucDoKhan,
            String mucDoMat,
            Integer phongBanXuLyId,
            Integer nguoiXuLyId,
            String nguonNhan,
            LocalDate hanXuLy,
            String doKhanXuLy,
            Integer loaiVanBanId,
            Integer linhVucId,
            Integer hoSoId,
            Integer soTrang,
            Integer soBan,
            String trichYeu,
            String ghiChu,
            String yKienChiDao,
            String tepDinhKemChinh,
            String trangThai,
            LocalDateTime ngayTao,
            LocalDateTime ngayCapNhat,
            Boolean daDoc,
            Boolean daXuLy,
            Boolean isDeleted,
            Integer nguoiTaoId,
            Integer nguoiCapNhatId
    ) {
        this.id = id;
        this.soCongVan = soCongVan;
        this.soDen = soDen;
        this.tieuDe = tieuDe;
        this.noiDungTomTat = noiDungTomTat;
        this.donViGuiId = donViGuiId;
        this.donViGui = donViGui;
        this.nguoiKy = nguoiKy;
        this.ngayVanBan = ngayVanBan;
        this.ngayNhan = ngayNhan;
        this.mucDoKhan = mucDoKhan;
        this.mucDoMat = mucDoMat;
        this.phongBanXuLyId = phongBanXuLyId;
        this.nguoiXuLyId = nguoiXuLyId;
        this.nguonNhan = nguonNhan;
        this.hanXuLy = hanXuLy;
        this.doKhanXuLy = doKhanXuLy;
        this.loaiVanBanId = loaiVanBanId;
        this.linhVucId = linhVucId;
        this.hoSoId = hoSoId;
        this.soTrang = soTrang;
        this.soBan = soBan;
        this.trichYeu = trichYeu;
        this.ghiChu = ghiChu;
        this.yKienChiDao = yKienChiDao;
        this.tepDinhKemChinh = tepDinhKemChinh;
        this.trangThai = trangThai;
        this.ngayTao = ngayTao;
        this.ngayCapNhat = ngayCapNhat;
        this.daDoc = daDoc;
        this.daXuLy = daXuLy;
        this.isDeleted = isDeleted;
        this.nguoiTaoId = nguoiTaoId;
        this.nguoiCapNhatId = nguoiCapNhatId;
    }
















}
