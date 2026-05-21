package com.example.med_office.dto;

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
}
