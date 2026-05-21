package com.example.med_office.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(name = "HoSoNhanVienRequest")
public record HoSoNhanVienRequest(
        @Positive(message = "Nguoi dung id must be greater than 0")
        @JsonAlias({"nguoi_dung_id", "user_id"})
        Long nguoiDungId,

        @NotBlank
        @Size(max = 50, message = "Code must not exceed 50 characters")
        @JsonAlias({"ho_so_nhan_vien_code", "ma_nhan_vien"})
        String code,

        @NotBlank
        @Size(max = 255, message = "Name must not exceed 255 characters")
        @JsonAlias({"ho_so_nhan_vien_name", "ho_ten"})
        String name,

        @JsonAlias({"birth_date", "ngay_sinh"})
        LocalDate birthDate,

        @Min(value = 0, message = "Gender must be greater than or equal to 0")
        @Max(value = 2, message = "Gender must be less than or equal to 2")
        @JsonAlias("gioi_tinh")
        Integer gender,

        @Size(max = 50, message = "Identity number must not exceed 50 characters")
        @JsonAlias({"identity_number", "cccd", "cmnd"})
        String identityNumber,

        @Size(max = 50, message = "Social insurance must not exceed 50 characters")
        @JsonAlias({"social_insurance", "bhxh"})
        String socialInsurance,

        @Email(message = "Email is invalid")
        @Size(max = 255, message = "Email must not exceed 255 characters")
        String email,

        @Size(max = 20, message = "Phone must not exceed 20 characters")
        @JsonAlias({"phone_number", "so_dien_thoai"})
        String phone,

        @Size(max = 100, message = "Degree must not exceed 100 characters")
        String degree,

        @Size(max = 255, message = "Specialty must not exceed 255 characters")
        String specialty,

        @Size(max = 255, message = "Specialty name must not exceed 255 characters")
        @JsonAlias("specialty_name")
        String specialtyName,

        @Size(max = 100, message = "Academic title must not exceed 100 characters")
        @JsonAlias("academic_title")
        String academicTitle,

        @Size(max = 255, message = "Academic title name must not exceed 255 characters")
        @JsonAlias("academic_title_name")
        String academicTitleName,

        @Size(max = 100, message = "Certificate must not exceed 100 characters")
        String certificate,

        @Size(max = 100, message = "Position must not exceed 100 characters")
        String position,

        @Size(max = 255, message = "Position name must not exceed 255 characters")
        @JsonAlias("position_name")
        String positionName,

        @Size(max = 255, message = "Honor title must not exceed 255 characters")
        @JsonAlias("honor_title")
        String honorTitle,

        @Size(max = 255, message = "Signing pin must not exceed 255 characters")
        @JsonAlias("signing_pin")
        String signingPin,

        @Size(max = 255, message = "Signing account must not exceed 255 characters")
        @JsonAlias("signing_account")
        String signingAccount,

        @Size(max = 255, message = "Signing otp must not exceed 255 characters")
        @JsonAlias("signing_otp")
        String signingOtp,

        @Size(max = 255, message = "Invoice password must not exceed 255 characters")
        @JsonAlias("invoice_password")
        String invoicePassword,

        @JsonAlias("avatar_image")
        String avatarImage,

        @JsonAlias("signature_image")
        String signatureImage,

        @JsonAlias("locked_from")
        LocalDate lockedFrom,

        @JsonAlias("locked_to")
        LocalDate lockedTo,

        @Size(max = 255, message = "Prescription account must not exceed 255 characters")
        @JsonAlias("prescription_account")
        String prescriptionAccount,

        @Size(max = 255, message = "Prescription password must not exceed 255 characters")
        @JsonAlias("prescription_password")
        String prescriptionPassword,

        @JsonAlias("online_booking")
        Boolean onlineBooking,

        Boolean active,

        @Size(max = 2000, message = "Note must not exceed 2000 characters")
        String note
) {
}
