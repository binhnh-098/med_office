package com.example.med_office.service;

import com.example.med_office.dto.RolePermissionsUpdateRequest;
import com.example.med_office.entity.Permission;
import com.example.med_office.entity.Role;
import com.example.med_office.entity.RolePermission;
import com.example.med_office.repository.NguoiDungRepository;
import com.example.med_office.repository.PermissionRepository;
import com.example.med_office.repository.RolePermissionRepository;
import com.example.med_office.repository.RoleRepository;
import com.example.med_office.repository.UserRoleRepository;
import com.example.med_office.security.PermissionCatalog;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccessControlServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private RolePermissionRepository rolePermissionRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private NguoiDungRepository nguoiDungRepository;

    @Mock
    private NguoiDungService nguoiDungService;

    @InjectMocks
    private AccessControlServiceImpl accessControlService;

    @Test
    void updateRolePermissionsAutoCreatesCatalogPermissionWhenMissingInDatabase() {
        Role role = new Role();
        role.setId("role-1");
        role.setCode("ADMIN");

        when(roleRepository.findById("role-1")).thenReturn(Optional.of(role));
        when(permissionRepository.findByCodeIn(anyCollection()))
                .thenReturn(List.of())
                .thenAnswer(invocation -> {
                    Permission permission = new Permission();
                    permission.setId("perm-catalogs-view");
                    permission.setCode(PermissionCatalog.CATALOGS_VIEW);
                    permission.setModuleCode("catalogs");
                    permission.setModuleName("Danh muc");
                    permission.setName("Xem danh muc");
                    permission.setDescription("Xem danh muc");
                    return List.of(permission);
                });

        accessControlService.updateRolePermissions(
                "role-1",
                new RolePermissionsUpdateRequest(List.of(PermissionCatalog.CATALOGS_VIEW))
        );

        ArgumentCaptor<List<Permission>> permissionCaptor = ArgumentCaptor.forClass(List.class);
        verify(permissionRepository).saveAll(permissionCaptor.capture());
        assertThat(permissionCaptor.getValue()).hasSize(1);
        assertThat(permissionCaptor.getValue().getFirst().getCode()).isEqualTo(PermissionCatalog.CATALOGS_VIEW);

        ArgumentCaptor<List<RolePermission>> rolePermissionCaptor = ArgumentCaptor.forClass(List.class);
        verify(rolePermissionRepository).saveAll(rolePermissionCaptor.capture());
        assertThat(rolePermissionCaptor.getValue()).hasSize(1);
        assertThat(rolePermissionCaptor.getValue().getFirst().getId().getRoleId()).isEqualTo("role-1");
        assertThat(rolePermissionCaptor.getValue().getFirst().getId().getPermissionId()).isEqualTo("perm-catalogs-view");
    }
}
