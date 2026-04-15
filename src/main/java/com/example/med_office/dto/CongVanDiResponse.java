package com.example.med_office.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CongVanDiResponse")
public class CongVanDiResponse {

    private final Long id;
    private final String soCongVan;
    private final String tieuDe;
    private final String noiDungTomTat;
    private final String donViNhan;
    private final LocalDate ngayBanHanh;
    private final Integer nguoiKyId;
    private final String trangThai;
    private final LocalDateTime ngayTao;
    private final LocalDateTime ngayCapNhat;

    public CongVanDiResponse(
            Long id,
            String soCongVan,
            String tieuDe,
            String noiDungTomTat,
            String donViNhan,
            LocalDate ngayBanHanh,
            Integer nguoiKyId,
            String trangThai,
            LocalDateTime ngayTao,
            LocalDateTime ngayCapNhat
    ) {
        this.id = id;
        this.soCongVan = soCongVan;
        this.tieuDe = tieuDe;
        this.noiDungTomTat = noiDungTomTat;
        this.donViNhan = donViNhan;
        this.ngayBanHanh = ngayBanHanh;
        this.nguoiKyId = nguoiKyId;
        this.trangThai = trangThai;
        this.ngayTao = ngayTao;
        this.ngayCapNhat = ngayCapNhat;
    }

    public Long getId() {
        return id;
    }

    public String getSoCongVan() {
        return soCongVan;
    }

    public String getTieuDe() {
        return tieuDe;
    }

    public String getNoiDungTomTat() {
        return noiDungTomTat;
    }

    public String getDonViNhan() {
        return donViNhan;
    }

    public LocalDate getNgayBanHanh() {
        return ngayBanHanh;
    }

    public Integer getNguoiKyId() {
        return nguoiKyId;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public LocalDateTime getNgayCapNhat() {
        return ngayCapNhat;
    }
}
