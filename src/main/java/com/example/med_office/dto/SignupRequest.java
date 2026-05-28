package com.example.med_office.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@Schema(name = "SignupRequest")
public class SignupRequest {

    @NotBlank(message = "Username must not be blank")
    @Size(max = 100, message = "Username must be at most 100 characters")
    @Schema(example = "reception")
    private String username;

    @NotBlank(message = "Password must not be blank")
    @Size(min = 6, max = 255, message = "Password must be between 6 and 255 characters")
    @Schema(example = "clinic123")
    private String password;

    @Size(max = 255, message = "Full name must be at most 255 characters")
    @JsonAlias({"full_name", "ho_ten"})
    @Schema(example = "Nguyen Van A")
    private String fullName;

    @Size(max = 255, message = "Email must be at most 255 characters")
    @Schema(example = "nguyenvana@example.com")
    private String email;

    @Size(max = 20, message = "Phone number must be at most 20 characters")
    @JsonAlias({"phone_number", "so_dien_thoai"})
    @Schema(example = "0900000000")
    private String phoneNumber;

    @JsonAlias({"position_id", "chuc_vu_id"})
    @Schema(example = "11111111-1111-1111-1111-111111111113")
    private String chucVuId;

    @Size(max = 50, message = "Position code must be at most 50 characters")
    @JsonAlias({"position_code", "ma_chuc_vu"})
    @Schema(example = "BAC_SI")
    private String maChucVu;
}
