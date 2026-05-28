package com.example.med_office.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "HoSoNhanVienResponse")
public record HoSoNhanVienResponse(
        String id,
        String nguoiDungId,
        String code,
        String name,
        LocalDate birthDate,
        Integer gender,
        String identityNumber,
        String socialInsurance,
        String email,
        String phone,
        String degree,
        String specialty,
        String specialtyName,
        String academicTitle,
        String academicTitleName,
        String certificate,
        String position,
        String positionName,
        String honorTitle,
        String signingPin,
        String signingAccount,
        String signingOtp,
        String invoicePassword,
        String avatarImage,
        String signatureImage,
        LocalDate lockedFrom,
        LocalDate lockedTo,
        String prescriptionAccount,
        String prescriptionPassword,
        Boolean onlineBooking,
        Boolean active,
        String note
) {
}
