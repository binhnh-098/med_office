package com.example.med_office.dto;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
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




}
