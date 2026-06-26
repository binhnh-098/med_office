package com.example.med_office.config;

import com.example.med_office.entity.Permission;
import com.example.med_office.entity.Role;
import com.example.med_office.entity.RolePermission;
import com.example.med_office.entity.RolePermissionId;
import com.example.med_office.repository.PermissionRepository;
import com.example.med_office.repository.RolePermissionRepository;
import com.example.med_office.repository.RoleRepository;
import com.example.med_office.security.PermissionCatalog;
import com.example.med_office.security.PermissionDefinition;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
public class LeaveRequestSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    public LeaveRequestSeeder(
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
        System.out.println("=== Running LeaveRequestSeeder ===");

        // 1. Sync Leave catalog definitions to permissions table
        syncPermission(PermissionCatalog.EMPLOYEES_LEAVE_VIEW, "human-resources", "Nhân sự", "Xem nghỉ phép");
        syncPermission(PermissionCatalog.EMPLOYEES_LEAVE_MANAGE, "human-resources", "Nhân sự", "Quản lý nghỉ phép");

        // Get permissions from DB
        Optional<Permission> viewPermOpt = permissionRepository.findByCode(PermissionCatalog.EMPLOYEES_LEAVE_VIEW);
        Optional<Permission> managePermOpt = permissionRepository.findByCode(PermissionCatalog.EMPLOYEES_LEAVE_MANAGE);

        if (viewPermOpt.isEmpty() || managePermOpt.isEmpty()) {
            System.err.println("Error: Leave permissions could not be synced.");
            return;
        }

        Permission viewPerm = viewPermOpt.get();
        Permission managePerm = managePermOpt.get();

        // 2. Assign EMPLOYEES_LEAVE_VIEW to all standard roles
        List<String> allRoles = List.of("ADMIN", "NHAN_SU", "VAN_THU", "BAC_SI", "DIEU_DUONG", "DINH_DUONG");
        for (String roleCode : allRoles) {
            roleRepository.findByCodeIgnoreCase(roleCode).ifPresent(role -> {
                assignPermissionToRole(role, viewPerm);
            });
        }

        // 3. Assign EMPLOYEES_LEAVE_MANAGE to ADMIN and NHAN_SU roles
        List<String> manageRoles = List.of("ADMIN", "NHAN_SU");
        for (String roleCode : manageRoles) {
            roleRepository.findByCodeIgnoreCase(roleCode).ifPresent(role -> {
                assignPermissionToRole(role, managePerm);
            });
        }

        System.out.println("=== LeaveRequestSeeder Completed successfully ===");
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
            System.out.println("Synced leave permission to DB: " + code);
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
