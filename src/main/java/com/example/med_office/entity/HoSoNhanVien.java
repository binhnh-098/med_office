package com.example.med_office.entity;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNguoiDungId() {
        return nguoiDungId;
    }

    public void setNguoiDungId(Long nguoiDungId) {
        this.nguoiDungId = nguoiDungId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getIdentityNumber() {
        return identityNumber;
    }

    public void setIdentityNumber(String identityNumber) {
        this.identityNumber = identityNumber;
    }

    public String getSocialInsurance() {
        return socialInsurance;
    }

    public void setSocialInsurance(String socialInsurance) {
        this.socialInsurance = socialInsurance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getSpecialtyName() {
        return specialtyName;
    }

    public void setSpecialtyName(String specialtyName) {
        this.specialtyName = specialtyName;
    }

    public String getAcademicTitle() {
        return academicTitle;
    }

    public void setAcademicTitle(String academicTitle) {
        this.academicTitle = academicTitle;
    }

    public String getAcademicTitleName() {
        return academicTitleName;
    }

    public void setAcademicTitleName(String academicTitleName) {
        this.academicTitleName = academicTitleName;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public String getHonorTitle() {
        return honorTitle;
    }

    public void setHonorTitle(String honorTitle) {
        this.honorTitle = honorTitle;
    }

    public String getSigningPin() {
        return signingPin;
    }

    public void setSigningPin(String signingPin) {
        this.signingPin = signingPin;
    }

    public String getSigningAccount() {
        return signingAccount;
    }

    public void setSigningAccount(String signingAccount) {
        this.signingAccount = signingAccount;
    }

    public String getSigningOtp() {
        return signingOtp;
    }

    public void setSigningOtp(String signingOtp) {
        this.signingOtp = signingOtp;
    }

    public String getInvoicePassword() {
        return invoicePassword;
    }

    public void setInvoicePassword(String invoicePassword) {
        this.invoicePassword = invoicePassword;
    }

    public String getAvatarImage() {
        return avatarImage;
    }

    public void setAvatarImage(String avatarImage) {
        this.avatarImage = avatarImage;
    }

    public String getSignatureImage() {
        return signatureImage;
    }

    public void setSignatureImage(String signatureImage) {
        this.signatureImage = signatureImage;
    }

    public LocalDate getLockedFrom() {
        return lockedFrom;
    }

    public void setLockedFrom(LocalDate lockedFrom) {
        this.lockedFrom = lockedFrom;
    }

    public LocalDate getLockedTo() {
        return lockedTo;
    }

    public void setLockedTo(LocalDate lockedTo) {
        this.lockedTo = lockedTo;
    }

    public String getPrescriptionAccount() {
        return prescriptionAccount;
    }

    public void setPrescriptionAccount(String prescriptionAccount) {
        this.prescriptionAccount = prescriptionAccount;
    }

    public String getPrescriptionPassword() {
        return prescriptionPassword;
    }

    public void setPrescriptionPassword(String prescriptionPassword) {
        this.prescriptionPassword = prescriptionPassword;
    }

    public Boolean getOnlineBooking() {
        return onlineBooking;
    }

    public void setOnlineBooking(Boolean onlineBooking) {
        this.onlineBooking = onlineBooking;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
