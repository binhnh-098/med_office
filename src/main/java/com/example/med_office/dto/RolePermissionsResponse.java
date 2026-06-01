package com.example.med_office.dto;

import java.util.List;

public record RolePermissionsResponse(
        String roleId,
        List<String> permissionCodes
) {
}
