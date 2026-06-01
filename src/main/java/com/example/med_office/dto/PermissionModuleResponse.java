package com.example.med_office.dto;

import java.util.List;

public record PermissionModuleResponse(
        String moduleCode,
        String moduleName,
        List<PermissionResponse> permissions
) {
}
