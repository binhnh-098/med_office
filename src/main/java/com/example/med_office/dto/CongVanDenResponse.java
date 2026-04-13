package com.example.med_office.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

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
    private final String nguonNhan;
    private final String trangThai;
    private final LocalDateTime ngayTao;

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
            String nguonNhan,
            String trangThai,
            LocalDateTime ngayTao
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
        this.nguonNhan = nguonNhan;
        this.trangThai = trangThai;
        this.ngayTao = ngayTao;
    }

    public Long getId() { return id; }
    public String getSoCongVan() { return soCongVan; }
    public String getSoDen() { return soDen; }
    public String getTieuDe() { return tieuDe; }
    public String getNoiDungTomTat() { return noiDungTomTat; }
    public Integer getDonViGuiId() { return donViGuiId; }
    public String getDonViGui() { return donViGui; }
    public String getNguoiKy() { return nguoiKy; }
    public LocalDate getNgayVanBan() { return ngayVanBan; }
    public LocalDate getNgayNhan() { return ngayNhan; }
    public String getMucDoKhan() { return mucDoKhan; }
    public String getMucDoMat() { return mucDoMat; }
    public String getNguonNhan() { return nguonNhan; }
    public String getTrangThai() { return trangThai; }
    public LocalDateTime getNgayTao() { return ngayTao; }
}
