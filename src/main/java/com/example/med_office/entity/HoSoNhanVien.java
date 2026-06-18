package com.example.med_office.entity;

import com.example.med_office.utils.UuidUtils;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "ho_so_nhan_vien")
public class HoSoNhanVien {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "nguoi_dung_id", unique = true, length = 36)
    private String nguoiDungId;

    @Column(name = "ma_nhan_vien", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "ten_nhan_vien", nullable = false, length = 255)
    private String name;

    @Column(name = "ngay_sinh")
    private LocalDate birthDate;

    @Column(name = "gioi_tinh")
    private Integer gender;

    @Column(name = "so_dinh_danh", length = 50)
    private String identityNumber;

    @Column(name = "so_bao_hiem_xa_hoi", length = 50)
    private String socialInsurance;

    @Column(name = "thu_dien_tu", length = 255)
    private String email;

    @Column(name = "so_dien_thoai", length = 20)
    private String phone;

    @Column(name = "bang_cap", length = 100)
    private String degree;

    @Column(name = "chuyen_khoa", length = 255)
    private String specialty;

    @Column(name = "hoc_ham", length = 100)
    private String academicTitle;

    @Column(name = "ten_hoc_ham", length = 255)
    private String academicTitleName;

    @Column(name = "chung_chi", length = 100)
    private String certificate;

    @Column(name = "ma_chuc_vu", length = 100)
    private String position;

    @Column(name = "cap_tren_truc_tiep_id", length = 36)
    private String directManagerId;

    @Column(name = "danh_hieu", length = 255)
    private String honorTitle;

    @Column(name = "ma_pin_ky", length = 255)
    private String signingPin;

    @Column(name = "tai_khoan_ky", length = 255)
    private String signingAccount;

    @Column(name = "otp_ky", length = 255)
    private String signingOtp;

    @Column(name = "mat_khau_hoa_don", length = 255)
    private String invoicePassword;

    @Lob
    @Column(name = "anh_dai_dien", columnDefinition = "LONGTEXT")
    private String avatarImage;

    @Lob
    @Column(name = "anh_chu_ky", columnDefinition = "LONGTEXT")
    private String signatureImage;

    @Column(name = "khoa_tu_ngay")
    private LocalDate lockedFrom;

    @Column(name = "khoa_den_ngay")
    private LocalDate lockedTo;

    @Column(name = "tai_khoan_ke_don", length = 255)
    private String prescriptionAccount;

    @Column(name = "mat_khau_ke_don", length = 255)
    private String prescriptionPassword;

    @Column(name = "dat_lich_truc_tuyen")
    private Boolean onlineBooking;

    @Column(name = "dang_hoat_dong")
    private Boolean active;

    @Column(name = "tong_nghi_phep_nam")
    private Double annualLeaveTotal;

    @Column(name = "da_nghi_phep_nam")
    private Double annualLeaveUsed;

    @Column(name = "ghi_chu", length = 2000)
    private String note;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (id == null || id.isBlank()) {
            id = UuidUtils.newUuid();
        }
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
        if (onlineBooking == null) {
            onlineBooking = false;
        }
        if (active == null) {
            active = true;
        }
        if (annualLeaveTotal == null) {
            annualLeaveTotal = 12.0;
        }
        if (annualLeaveUsed == null) {
            annualLeaveUsed = 0.0;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
