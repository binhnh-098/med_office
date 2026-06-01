package com.example.med_office.controller;

import com.example.med_office.dto.ApiResponse;
import com.example.med_office.dto.MessageResponse;
import com.example.med_office.dto.NguoiDungResponse;
import com.example.med_office.dto.PermissionModuleResponse;
import com.example.med_office.dto.RolePermissionsResponse;
import com.example.med_office.dto.RolePermissionsUpdateRequest;
import com.example.med_office.dto.RoleResponse;
import com.example.med_office.service.AccessControlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Phan quyen", description = "Quan ly vai tro va quyen")
public class AdminPermissionController {

    private final AccessControlService accessControlService;

    public AdminPermissionController(AccessControlService accessControlService) {
        this.accessControlService = accessControlService;
    }

    @Operation(summary = "Lay danh sach vai tro")
    @GetMapping(path = "/roles", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<RoleResponse>>> getRoles() {
        return ResponseEntity.ok(ApiResponse.success("Lay danh sach vai tro thanh cong", accessControlService.getRoles()));
    }

    @Operation(summary = "Lay danh sach quyen theo module")
    @GetMapping(path = "/permissions", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<PermissionModuleResponse>>> getPermissions() {
        return ResponseEntity.ok(ApiResponse.success("Lay danh sach quyen thanh cong", accessControlService.getPermissionsByModule()));
    }

    @Operation(summary = "Lay quyen cua mot vai tro")
    @GetMapping(path = "/roles/{roleId}/permissions", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<RolePermissionsResponse>> getRolePermissions(@PathVariable String roleId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lay quyen cua vai tro thanh cong",
                accessControlService.getRolePermissions(roleId)
        ));
    }

    @Operation(summary = "Cap nhat quyen cua vai tro")
    @PutMapping(
            path = "/roles/{roleId}/permissions",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<MessageResponse>> updateRolePermissions(
            @PathVariable String roleId,
            @Valid @RequestBody RolePermissionsUpdateRequest request
    ) {
        accessControlService.updateRolePermissions(roleId, request);
        return ResponseEntity.ok(ApiResponse.success(
                "Cap nhat phan quyen thanh cong",
                new MessageResponse("Cập nhật phân quyền thành công")
        ));
    }

    @Operation(summary = "Lay danh sach tai khoan thuoc vai tro")
    @GetMapping(path = "/roles/{roleId}/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<NguoiDungResponse>>> getUsersByRole(@PathVariable String roleId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lay danh sach tai khoan theo vai tro thanh cong",
                accessControlService.getUsersByRole(roleId)
        ));
    }
}
