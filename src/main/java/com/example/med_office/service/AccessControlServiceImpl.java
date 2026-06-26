package com.example.med_office.service;

import com.example.med_office.dto.NguoiDungResponse;
import com.example.med_office.dto.PermissionModuleResponse;
import com.example.med_office.dto.PermissionResponse;
import com.example.med_office.dto.RolePermissionsResponse;
import com.example.med_office.dto.RolePermissionsUpdateRequest;
import com.example.med_office.dto.RoleResponse;
import com.example.med_office.entity.Permission;
import com.example.med_office.entity.Role;
import com.example.med_office.entity.RolePermission;
import com.example.med_office.entity.RolePermissionId;
import com.example.med_office.entity.UserRole;
import com.example.med_office.entity.UserRoleId;
import com.example.med_office.repository.NguoiDungRepository;
import com.example.med_office.repository.PermissionRepository;
import com.example.med_office.repository.RolePermissionRepository;
import com.example.med_office.repository.RoleRepository;
import com.example.med_office.repository.UserRoleRepository;
import com.example.med_office.security.AppRoles;
import com.example.med_office.security.PermissionCatalog;
import com.example.med_office.security.PermissionDefinition;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AccessControlServiceImpl implements AccessControlService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserRoleRepository userRoleRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final NguoiDungService nguoiDungService;

    public AccessControlServiceImpl(
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            RolePermissionRepository rolePermissionRepository,
            UserRoleRepository userRoleRepository,
            NguoiDungRepository nguoiDungRepository,
            NguoiDungService nguoiDungService
    ) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.userRoleRepository = userRoleRepository;
        this.nguoiDungRepository = nguoiDungRepository;
        this.nguoiDungService = nguoiDungService;
    }

    @Override
    public List<RoleResponse> getRoles() {
        return roleRepository.findAll().stream()
                .sorted(Comparator.comparing(Role::getCode, String.CASE_INSENSITIVE_ORDER))
                .map(this::toRoleResponse)
                .toList();
    }

    @Override
    public List<PermissionModuleResponse> getPermissionsByModule() {
        syncCatalogPermissions(permissionCodesFromCatalog());
        Map<String, List<Permission>> permissionsByModule = permissionRepository.findAll().stream()
                .sorted(Comparator.comparing(Permission::getModuleCode).thenComparing(Permission::getCode))
                .collect(Collectors.groupingBy(
                        Permission::getModuleCode,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        return permissionsByModule.values().stream()
                .map(permissions -> {
                    Permission first = permissions.get(0);
                    return new PermissionModuleResponse(
                            first.getModuleCode(),
                            first.getModuleName(),
                            permissions.stream().map(this::toPermissionResponse).toList()
                    );
                })
                .toList();
    }

    @Override
    public RolePermissionsResponse getRolePermissions(String roleId) {
        requireRole(roleId);
        List<String> permissionCodes = permissionCodesByRoleIds(List.of(roleId));
        return new RolePermissionsResponse(roleId, permissionCodes);
    }

    @Override
    @Transactional
    public void updateRolePermissions(String roleId, RolePermissionsUpdateRequest request) {
        requireRole(roleId);
        List<String> requestedCodes = (request.permissionCodes() == null ? List.<String>of() : request.permissionCodes()).stream()
                .filter(code -> code != null && !code.isBlank())
                .map(String::trim)
                .distinct()
                .toList();
        syncCatalogPermissions(requestedCodes);
        Map<String, Permission> permissionsByCode = permissionRepository.findByCodeIn(requestedCodes).stream()
                .collect(Collectors.toMap(Permission::getCode, Function.identity()));
        List<String> missingCodes = requestedCodes.stream()
                .filter(code -> !permissionsByCode.containsKey(code))
                .toList();
        if (!missingCodes.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Permission does not exist: " + missingCodes.get(0));
        }

        rolePermissionRepository.deleteByIdRoleId(roleId);
        List<RolePermission> rolePermissions = requestedCodes.stream()
                .map(permissionsByCode::get)
                .map(permission -> {
                    RolePermission rolePermission = new RolePermission();
                    rolePermission.setId(new RolePermissionId(roleId, permission.getId()));
                    return rolePermission;
                })
                .toList();
        rolePermissionRepository.saveAll(rolePermissions);
    }

    @Override
    public List<NguoiDungResponse> getUsersByRole(String roleId) {
        requireRole(roleId);
        LinkedHashSet<String> userIds = userRoleRepository.findByIdRoleId(roleId).stream()
                .map(userRole -> userRole.getId().getUserId())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        return nguoiDungService.getUsers().stream()
                .filter(user -> userIds.contains(user.id()))
                .toList();
    }

    @Override
    public List<RoleResponse> getUserRoles(String userId) {
        return resolveRolesForUser(userId, null).stream()
                .map(this::toRoleResponse)
                .toList();
    }

    @Override
    public List<String> getUserPermissionCodes(String userId) {
        List<String> roleIds = resolveRolesForUser(userId, null).stream()
                .map(Role::getId)
                .toList();
        return permissionCodesByRoleIds(roleIds);
    }

    @Override
    public List<String> getPermissionCodesForRoles(List<Role> roles) {
        return permissionCodesByRoleIds(roles.stream().map(Role::getId).toList());
    }

    @Override
    public List<Role> resolveRolesForUser(String userId, String legacyPositionCode) {
        List<String> roleIds = userRoleRepository.findByIdUserId(userId).stream()
                .map(userRole -> userRole.getId().getRoleId())
                .toList();
        if (!roleIds.isEmpty()) {
            Map<String, Role> rolesById = roleRepository.findAllById(roleIds).stream()
                    .collect(Collectors.toMap(Role::getId, Function.identity()));
            return roleIds.stream()
                    .map(rolesById::get)
                    .filter(role -> role != null)
                    .toList();
        }

        String fallbackRoleCode = AppRoles.toPermissionRoleCode(legacyPositionCode);
        return roleRepository.findByCodeIgnoreCase(fallbackRoleCode)
                .map(role -> List.of(role))
                .orElseGet(() -> List.of());
    }

    @Override
    @Transactional
    public void replaceUserRoles(String userId, List<String> roleCodes) {
        if (!nguoiDungRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist");
        }
        List<String> normalizedCodes = roleCodes.stream()
                .filter(code -> code != null && !code.isBlank())
                .map(AppRoles::normalizeRoleCode)
                .distinct()
                .toList();
        List<Role> roles = roleRepository.findByCodeIn(normalizedCodes);
        Map<String, Role> rolesByCode = roles.stream()
                .collect(Collectors.toMap(Role::getCode, Function.identity()));
        List<String> missingCodes = normalizedCodes.stream()
                .filter(code -> !rolesByCode.containsKey(code))
                .toList();
        if (!missingCodes.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role does not exist: " + missingCodes.get(0));
        }

        userRoleRepository.deleteByIdUserId(userId);
        userRoleRepository.saveAll(normalizedCodes.stream()
                .map(rolesByCode::get)
                .map(role -> {
                    UserRole userRole = new UserRole();
                    userRole.setId(new UserRoleId(userId, role.getId()));
                    return userRole;
                })
                .toList());
    }

    private List<String> permissionCodesByRoleIds(List<String> roleIds) {
        if (roleIds.isEmpty()) {
            return List.of();
        }
        List<String> permissionIds = rolePermissionRepository.findByIdRoleIdIn(roleIds).stream()
                .map(rolePermission -> rolePermission.getId().getPermissionId())
                .distinct()
                .toList();
        if (permissionIds.isEmpty()) {
            return List.of();
        }
        Map<String, Permission> permissionsById = permissionRepository.findAllById(permissionIds).stream()
                .collect(Collectors.toMap(Permission::getId, Function.identity()));
        return permissionIds.stream()
                .map(permissionsById::get)
                .filter(permission -> permission != null)
                .map(Permission::getCode)
                .distinct()
                .sorted()
                .toList();
    }

    private Role requireRole(String roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role does not exist"));
    }

    private void syncCatalogPermissions(Collection<String> requestedCodes) {
        if (requestedCodes == null || requestedCodes.isEmpty()) {
            return;
        }

        Map<String, PermissionDefinition> definitionsByCode = PermissionCatalog.definitions().stream()
                .filter(definition -> requestedCodes.contains(definition.code()))
                .collect(Collectors.toMap(PermissionDefinition::code, Function.identity()));
        if (definitionsByCode.isEmpty()) {
            return;
        }

        Map<String, Permission> existingPermissionsByCode = permissionRepository.findByCodeIn(definitionsByCode.keySet()).stream()
                .collect(Collectors.toMap(Permission::getCode, Function.identity()));
        
        List<Permission> toSave = new java.util.ArrayList<>();
        definitionsByCode.forEach((code, definition) -> {
            Permission existing = existingPermissionsByCode.get(code);
            if (existing == null) {
                toSave.add(newPermissionFromDefinition(definition));
            } else {
                boolean updated = false;
                if (!definition.moduleName().equals(existing.getModuleName())) {
                    existing.setModuleName(definition.moduleName());
                    updated = true;
                }
                if (!definition.name().equals(existing.getName())) {
                    existing.setName(definition.name());
                    updated = true;
                }
                if (!definition.description().equals(existing.getDescription())) {
                    existing.setDescription(definition.description());
                    updated = true;
                }
                if (!definition.moduleCode().equals(existing.getModuleCode())) {
                    existing.setModuleCode(definition.moduleCode());
                    updated = true;
                }
                if (updated) {
                    toSave.add(existing);
                }
            }
        });
        
        if (!toSave.isEmpty()) {
            permissionRepository.saveAll(toSave);
        }
    }

    private List<String> permissionCodesFromCatalog() {
        return PermissionCatalog.definitions().stream()
                .map(PermissionDefinition::code)
                .toList();
    }

    private Permission newPermissionFromDefinition(PermissionDefinition definition) {
        Permission permission = new Permission();
        permission.setCode(definition.code());
        permission.setModuleCode(definition.moduleCode());
        permission.setModuleName(definition.moduleName());
        permission.setName(definition.name());
        permission.setDescription(definition.description());
        return permission;
    }

    private RoleResponse toRoleResponse(Role role) {
        return new RoleResponse(role.getId(), role.getCode(), role.getName(), role.getDescription());
    }

    private PermissionResponse toPermissionResponse(Permission permission) {
        return new PermissionResponse(
                permission.getId(),
                permission.getCode(),
                permission.getName(),
                permission.getDescription()
        );
    }
}
