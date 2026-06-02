package com.example.med_office.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record NguoiDungResponse(
        String id,
        String hoSoNhanVienId,
        String username,
        String departmentId,
        String hoSoNhanVienCode,
        LocalDate birthDate,
        Integer gender,
        String identityNumber,
        String socialInsurance,
        String degree,
        String specialty,
        String academicTitle,
        String academicTitleName,
        String certificate,
        String honorTitle,
        String avatarImage,
        Boolean onlineBooking,
        Boolean profileActive,
        String chucVuId,
        String maChucVu,
        String tenChucVu,
        List<String> roles,
        List<String> modules,
        String status,
        LocalDateTime lastLoginAt,
        LocalDateTime createdAt
) {
}
