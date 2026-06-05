package com.example.med_office.dto;

public record HoSoNhanVienOptionResponse(
        String id,
        String hoSoNhanVienId,
        String code,
        String name,
        String position,
        String positionName,
        Boolean active
) {
}
