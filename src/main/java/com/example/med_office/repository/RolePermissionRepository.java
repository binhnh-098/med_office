package com.example.med_office.repository;

import com.example.med_office.entity.RolePermission;
import com.example.med_office.entity.RolePermissionId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface RolePermissionRepository extends JpaRepository<RolePermission, RolePermissionId> {

    List<RolePermission> findByIdRoleId(String roleId);

    List<RolePermission> findByIdRoleIdIn(Collection<String> roleIds);

    void deleteByIdRoleId(String roleId);
}
