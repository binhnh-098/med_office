package com.example.med_office.dto;

import lombok.Getter;
import lombok.Setter;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@Schema(name = "LoginRequest")
public class LoginRequest {

    @NotBlank(message = "Username must not be blank")
    @Schema(example = "reception")
    private String username;

    @NotBlank(message = "Password must not be blank")
    @Schema(example = "clinic123")
    private String password;
}
