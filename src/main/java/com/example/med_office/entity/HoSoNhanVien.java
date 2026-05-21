package com.example.med_office.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nguoi_dung_id", unique = true)
    private Long nguoiDungId;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "gender")
    private Integer gender;

    @Column(name = "identity_number", length = 50)
    private String identityNumber;

    @Column(name = "social_insurance", length = 50)
    private String socialInsurance;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "degree", length = 100)
    private String degree;

    @Column(name = "specialty", length = 255)
    private String specialty;

    @Column(name = "specialty_name", length = 255)
    private String specialtyName;

    @Column(name = "academic_title", length = 100)
    private String academicTitle;

    @Column(name = "academic_title_name", length = 255)
    private String academicTitleName;

    @Column(name = "certificate", length = 100)
    private String certificate;

    @Column(name = "position_code", length = 100)
    private String position;

    @Column(name = "position_name", length = 255)
    private String positionName;

    @Column(name = "honor_title", length = 255)
    private String honorTitle;

    @Column(name = "signing_pin", length = 255)
    private String signingPin;

    @Column(name = "signing_account", length = 255)
    private String signingAccount;

    @Column(name = "signing_otp", length = 255)
    private String signingOtp;

    @Column(name = "invoice_password", length = 255)
    private String invoicePassword;

    @Lob
    @Column(name = "avatar_image")
    private String avatarImage;

    @Lob
    @Column(name = "signature_image")
    private String signatureImage;

    @Column(name = "locked_from")
    private LocalDate lockedFrom;

    @Column(name = "locked_to")
    private LocalDate lockedTo;

    @Column(name = "prescription_account", length = 255)
    private String prescriptionAccount;

    @Column(name = "prescription_password", length = 255)
    private String prescriptionPassword;

    @Column(name = "online_booking")
    private Boolean onlineBooking;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "note", length = 2000)
    private String note;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
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
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
