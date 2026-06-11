package com.example.med_office.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RolePermissionsUpdateRequest {

    @NotNull(message = "permissionCodes is required")
    private final List<String> permissionCodes;

    public RolePermissionsUpdateRequest(List<String> permissionCodes) {
        this.permissionCodes = normalize(permissionCodes);
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public RolePermissionsUpdateRequest(Object payload) {
        this.permissionCodes = extractPermissionCodes(payload);
    }

    public List<String> permissionCodes() {
        return permissionCodes;
    }

    public List<String> getPermissionCodes() {
        return permissionCodes;
    }

    private static List<String> extractPermissionCodes(Object payload) {
        if (payload == null) {
            return null;
        }
        if (payload instanceof List<?> listPayload) {
            return normalize(extractCodes(listPayload));
        }
        if (!(payload instanceof Map<?, ?> payloadMap)) {
            if (payload instanceof String stringPayload) {
                return normalize(List.of(stringPayload));
            }
            return null;
        }

        Object codesNode = firstPresent(
                payloadMap.get("permissionCodes"),
                payloadMap.get("permissions"),
                payloadMap.get("codes")
        );
        if (codesNode == null) {
            return null;
        }
        return normalize(extractCodes(codesNode));
    }

    private static List<String> extractCodes(Object node) {
        if (node == null) {
            return null;
        }
        if (node instanceof String stringNode) {
            return List.of(stringNode);
        }
        if (node instanceof Map<?, ?> mapNode) {
            Object codeNode = firstPresent(
                    mapNode.get("code"),
                    mapNode.get("permissionCode"),
                    mapNode.get("value")
            );
            if (codeNode instanceof String stringCodeNode) {
                return List.of(stringCodeNode);
            }
            return null;
        }
        if (!(node instanceof List<?> listNode)) {
            return null;
        }

        List<String> codes = new ArrayList<>();
        for (Object item : listNode) {
            List<String> extracted = extractCodes(item);
            if (extracted != null) {
                codes.addAll(extracted);
            }
        }
        return codes;
    }

    private static Object firstPresent(Object... candidates) {
        for (Object candidate : candidates) {
            if (candidate != null) {
                return candidate;
            }
        }
        return null;
    }

    private static List<String> normalize(List<String> permissionCodes) {
        if (permissionCodes == null) {
            return null;
        }
        Set<String> normalizedCodes = new LinkedHashSet<>();
        for (String permissionCode : permissionCodes) {
            if (permissionCode == null) {
                continue;
            }
            String trimmedCode = permissionCode.trim();
            if (!trimmedCode.isEmpty()) {
                normalizedCodes.add(trimmedCode);
            }
        }
        return List.copyOf(normalizedCodes);
    }
}
