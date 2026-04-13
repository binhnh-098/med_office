package com.example.med_office.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "LoginRequest")
public class LoginRequest {

    @NotBlank(message = "Username must not be blank")
    @Schema(example = "reception")
    private String username;

    @NotBlank(message = "Password must not be blank")
    @Schema(example = "clinic123")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
