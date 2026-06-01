package com.example.med_office.service;

import com.example.med_office.dto.NguoiDungResponse;
import com.example.med_office.dto.PermissionModuleResponse;
import com.example.med_office.dto.RolePermissionsResponse;
import com.example.med_office.dto.RolePermissionsUpdateRequest;
import com.example.med_office.dto.RoleResponse;
import com.example.med_office.entity.Role;

import java.util.List;

public interface AccessControlService {

    List<RoleResponse> getRoles();

    List<PermissionModuleResponse> getPermissionsByModule();

    RolePermissionsResponse getRolePermissions(String roleId);

    void updateRolePermissions(String roleId, RolePermissionsUpdateRequest request);

    List<NguoiDungResponse> getUsersByRole(String roleId);

    List<RoleResponse> getUserRoles(String userId);

    List<String> getUserPermissionCodes(String userId);

    List<String> getPermissionCodesForRoles(List<Role> roles);

    List<Role> resolveRolesForUser(String userId, String legacyPositionCode);

    void replaceUserRoles(String userId, List<String> roleCodes);
}
