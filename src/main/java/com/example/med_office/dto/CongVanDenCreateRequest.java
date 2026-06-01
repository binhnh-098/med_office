package com.example.med_office.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Getter
@Setter
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

    @NotBlank(message = "Don vi gui id khong duoc de trong")
    @JsonAlias("don_vi_gui_id")
    private String donViGuiId;

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
    private String phongBanXuLyId;

    @JsonAlias("nguoi_xu_ly_id")
    private String nguoiXuLyId;

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
    private String loaiVanBanId;

    @JsonAlias("linh_vuc_id")
    private String linhVucId;

    @JsonAlias("ho_so_id")
    private String hoSoId;

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

    @JsonAlias({"is_deleted", "da_xoa"})
    private Boolean isDeleted;

    @JsonAlias("nguoi_tao_id")
    private String nguoiTaoId;

    @JsonAlias("nguoi_cap_nhat_id")
    private String nguoiCapNhatId;

    @Size(max = 50, message = "Trang thai must not exceed 50 characters")
    @JsonAlias("trang_thai")
    private String trangThai;
}
