package com.example.med_office.config;

import com.example.med_office.entity.Permission;
import com.example.med_office.entity.Role;
import com.example.med_office.entity.RolePermission;
import com.example.med_office.entity.RolePermissionId;
import com.example.med_office.repository.PermissionRepository;
import com.example.med_office.repository.RolePermissionRepository;
import com.example.med_office.repository.RoleRepository;
import com.example.med_office.security.PermissionCatalog;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
public class BusinessTripSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    public BusinessTripSeeder(
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            RolePermissionRepository rolePermissionRepository
    ) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("=== Running BusinessTripSeeder ===");

        // 1. Sync Business Trip permissions to database
        syncPermission(PermissionCatalog.EMPLOYEES_BUSINESS_TRIP_VIEW, "human-resources", "Nhân sự", "Xem thông tin công tác");
        syncPermission(PermissionCatalog.EMPLOYEES_BUSINESS_TRIP_MANAGE, "human-resources", "Nhân sự", "Quản lý và đề xuất công tác");

        // Sync Asset permissions to database
        syncPermission(PermissionCatalog.CATALOGS_ASSET_VIEW, "catalogs", "Danh mục", "Xem danh mục tài sản");
        syncPermission(PermissionCatalog.CATALOGS_ASSET_MANAGE, "catalogs", "Danh mục", "Quản lý danh mục tài sản");

        // Sync base Catalog view permission
        syncPermission(PermissionCatalog.CATALOGS_VIEW, "catalogs", "Danh mục", "Xem danh sách danh mục");

        Optional<Permission> viewPermOpt = permissionRepository.findByCode(PermissionCatalog.EMPLOYEES_BUSINESS_TRIP_VIEW);
        Optional<Permission> managePermOpt = permissionRepository.findByCode(PermissionCatalog.EMPLOYEES_BUSINESS_TRIP_MANAGE);
        Optional<Permission> assetViewPermOpt = permissionRepository.findByCode(PermissionCatalog.CATALOGS_ASSET_VIEW);
        Optional<Permission> assetManagePermOpt = permissionRepository.findByCode(PermissionCatalog.CATALOGS_ASSET_MANAGE);
        Optional<Permission> catalogsViewPermOpt = permissionRepository.findByCode(PermissionCatalog.CATALOGS_VIEW);

        if (viewPermOpt.isEmpty() || managePermOpt.isEmpty() || assetViewPermOpt.isEmpty() || assetManagePermOpt.isEmpty() || catalogsViewPermOpt.isEmpty()) {
            System.err.println("Error: Permissions could not be synced.");
            return;
        }

        Permission viewPerm = viewPermOpt.get();
        Permission managePerm = managePermOpt.get();
        Permission assetViewPerm = assetViewPermOpt.get();
        Permission assetManagePerm = assetManagePermOpt.get();
        Permission catalogsViewPerm = catalogsViewPermOpt.get();

        // 2. Assign view permissions to all standard roles
        List<String> allRoles = List.of("ADMIN", "NHAN_SU", "VAN_THU", "BAC_SI", "DIEU_DUONG", "DINH_DUONG");
        for (String roleCode : allRoles) {
            roleRepository.findByCodeIgnoreCase(roleCode).ifPresent(role -> {
                assignPermissionToRole(role, viewPerm);
                assignPermissionToRole(role, assetViewPerm);
                assignPermissionToRole(role, catalogsViewPerm);
            });
        }

        // 3. Assign manage permissions to ADMIN and NHAN_SU roles
        List<String> manageRoles = List.of("ADMIN", "NHAN_SU");
        for (String roleCode : manageRoles) {
            roleRepository.findByCodeIgnoreCase(roleCode).ifPresent(role -> {
                assignPermissionToRole(role, managePerm);
                assignPermissionToRole(role, assetManagePerm);
            });
        }

        System.out.println("=== BusinessTripSeeder Completed successfully ===");
    }

    private void syncPermission(String code, String moduleCode, String moduleName, String name) {
        if (permissionRepository.findByCode(code).isEmpty()) {
            Permission permission = new Permission();
            permission.setCode(code);
            permission.setModuleCode(moduleCode);
            permission.setModuleName(moduleName);
            permission.setName(name);
            permission.setDescription(name);
            permissionRepository.save(permission);
            System.out.println("Synced business trip permission to DB: " + code);
        }
    }

    private void assignPermissionToRole(Role role, Permission permission) {
        RolePermissionId id = new RolePermissionId(role.getId(), permission.getId());
        if (!rolePermissionRepository.existsById(id)) {
            RolePermission rp = new RolePermission();
            rp.setId(id);
            rolePermissionRepository.save(rp);
            System.out.println("Assigned permission " + permission.getCode() + " to role " + role.getCode());
        }
    }
}
