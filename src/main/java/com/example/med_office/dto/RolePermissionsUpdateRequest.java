package com.example.med_office.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RolePermissionsUpdateRequest(
        @NotNull(message = "permissionCodes is required")
        List<String> permissionCodes
) {
}
