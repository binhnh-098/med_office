package com.example.med_office.dto;

import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RowboatMessage {

    @NotBlank(message = "Role must not be blank")
    private String role;

    private String content;

    private String agenticResponseType;

    private String agenticSender;
}
