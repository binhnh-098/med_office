package com.example.med_office.security;

public record PermissionDefinition(
        String code,
        String moduleCode,
        String moduleName,
        String name,
        String description
) {
}
