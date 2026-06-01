package com.example.med_office.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(name = "NguoiDungRoleUpdateRequest")
public class NguoiDungRoleUpdateRequest {

    @JsonAlias({"position_id", "chuc_vu_id"})
    @Schema(example = "11111111-1111-1111-1111-111111111113")
    private String chucVuId;

    @Size(max = 50, message = "Position code must be at most 50 characters")
    @JsonAlias({"position_code", "ma_chuc_vu"})
    @Schema(example = "BAC_SI")
    private String maChucVu;

    @JsonAlias({"role_codes", "vai_tro"})
    @Schema(example = "[\"BAC_SI\", \"DINH_DUONG\"]")
    private List<@Size(max = 50, message = "Role code must be at most 50 characters") String> roleCodes;
}
