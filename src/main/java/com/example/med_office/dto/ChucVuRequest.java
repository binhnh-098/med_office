package com.example.med_office.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "ChucVuRequest")
public record ChucVuRequest(
        @NotBlank(message = "Ma chuc vu khong duoc de trong")
        @Size(max = 50, message = "Ma chuc vu must not exceed 50 characters")
        @JsonAlias({"ma_chuc_vu", "code"})
        String maChucVu,

        @NotBlank(message = "Ten chuc vu khong duoc de trong")
        @Size(max = 255, message = "Ten chuc vu must not exceed 255 characters")
        @JsonAlias({"ten_chuc_vu", "name"})
        String tenChucVu,

        @NotBlank(message = "User id khong duoc de trong")
        @JsonAlias({"user_id", "userid", "nguoi_dung_id"})
        String userId
) {
}
