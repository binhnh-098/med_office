package com.example.med_office.dto;

import java.util.List;

public record NguoiDungResponse(
        String id,
        String username,
        String fullName,
        String email,
        String phoneNumber,
        String chucVuId,
        String maChucVu,
        String tenChucVu,
        List<String> roles,
        List<String> modules,
        String status
) {
}
