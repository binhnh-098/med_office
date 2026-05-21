package com.example.med_office.dto;

import lombok.Getter;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Schema(name = "LoginResponse")
public class LoginResponse {

    @Schema(example = "1")
    private final Long id;

    @Schema(example = "10")
    private final Long hoSoNhanVienId;

    @Schema(example = "reception")
    private final String username;

    @Schema(example = "Reception Staff")
    private final String fullName;

    @Schema(example = "ACTIVE")
    private final String status;

    @Schema(example = "reception@med-office.local")
    private final String email;

    @Schema(example = "0901234567")
    private final String phoneNumber;

    @Schema(example = "1")
    private final Long departmentId;

    @Schema(example = "2")
    private final Long positionId;

    @Schema(example = "Bac si")
    private final String positionName;

    @Schema(example = "2026-04-13T11:45:00")
    private final LocalDateTime lastLoginAt;

    public LoginResponse(
            Long id,
            Long hoSoNhanVienId,
            String username,
            String fullName,
            String status,
            String email,
            String phoneNumber,
            Long departmentId,
            Long positionId,
            String positionName,
            LocalDateTime lastLoginAt
    ) {
        this.id = id;
        this.hoSoNhanVienId = hoSoNhanVienId;
        this.username = username;
        this.fullName = fullName;
        this.status = status;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.departmentId = departmentId;
        this.positionId = positionId;
        this.positionName = positionName;
        this.lastLoginAt = lastLoginAt;
    }




}
