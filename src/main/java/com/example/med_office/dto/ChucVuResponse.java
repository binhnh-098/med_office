package com.example.med_office.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ChucVuResponse")
public record ChucVuResponse(
        String id,

        @JsonProperty("ma_chuc_vu")
        String maChucVu,

        @JsonProperty("ten_chuc_vu")
        String tenChucVu,

        @JsonProperty("user_id")
        String userId
) {
}
