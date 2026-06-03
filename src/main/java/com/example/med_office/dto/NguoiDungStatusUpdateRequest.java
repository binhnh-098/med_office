package com.example.med_office.dto;

import jakarta.validation.constraints.NotNull;

public record NguoiDungStatusUpdateRequest(
        @NotNull(message = "Active status must not be null")
        Boolean active
) {
}
