package com.example.med_office.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    public Integer getPhongBanXuLyId() { return phongBanXuLyId; }
    public Integer getNguoiXuLyId() { return nguoiXuLyId; }
    public String getNguonNhan() { return nguonNhan; }
    public LocalDate getHanXuLy() { return hanXuLy; }
    public String getDoKhanXuLy() { return doKhanXuLy; }
    public Integer getLoaiVanBanId() { return loaiVanBanId; }
    public Integer getLinhVucId() { return linhVucId; }
    public Integer getHoSoId() { return hoSoId; }
    public Integer getSoTrang() { return soTrang; }
    public Integer getSoBan() { return soBan; }
    public String getTrichYeu() { return trichYeu; }
    public String getGhiChu() { return ghiChu; }
    public String getYKienChiDao() { return yKienChiDao; }
    public String getTepDinhKemChinh() { return tepDinhKemChinh; }
    public String getTrangThai() { return trangThai; }
    public LocalDateTime getNgayTao() { return ngayTao; }
    public LocalDateTime getNgayCapNhat() { return ngayCapNhat; }
    public Boolean getDaDoc() { return daDoc; }
    public Boolean getDaXuLy() { return daXuLy; }
    public Boolean getIsDeleted() { return isDeleted; }
    public Integer getNguoiTaoId() { return nguoiTaoId; }
    public Integer getNguoiCapNhatId() { return nguoiCapNhatId; }
}
