package com.example.med_office.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RowboatMessage {

    @NotBlank(message = "Role must not be blank")
    private String role;

    private String content;

    private String agenticResponseType;

    private String agenticSender;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAgenticResponseType() {
        return agenticResponseType;
    }

    public void setAgenticResponseType(String agenticResponseType) {
        this.agenticResponseType = agenticResponseType;
    }

    public String getAgenticSender() {
        return agenticSender;
    }

    public void setAgenticSender(String agenticSender) {
        this.agenticSender = agenticSender;
    }
}
