package com.example.med_office.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ChuyenKhoaResponse")
public record ChuyenKhoaResponse(
        @JsonProperty("id_chuyen_khoa")
        String idChuyenKhoa,

        @JsonProperty("ten_chuyen_khoa")
        String tenChuyenKhoa,

        @JsonProperty("nguoi_dung_id")
        String userId
) {
}
