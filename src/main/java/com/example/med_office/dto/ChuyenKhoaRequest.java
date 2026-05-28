package com.example.med_office.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "ChuyenKhoaRequest")
public record ChuyenKhoaRequest(
        @NotBlank(message = "Ten chuyen khoa khong duoc de trong")
        @Size(max = 255, message = "Ten chuyen khoa must not exceed 255 characters")
        @JsonAlias({"ten_chuyen_khoa", "name"})
        String tenChuyenKhoa,

        @NotBlank(message = "User id khong duoc de trong")
        @JsonAlias({"user_id", "userid", "nguoi_dung_id"})
        String userId
) {
}
