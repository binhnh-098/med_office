package com.example.med_office.repository;

import com.example.med_office.entity.UserRole;
import com.example.med_office.entity.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {

    List<UserRole> findByIdUserId(String userId);

    List<UserRole> findByIdUserIdIn(Collection<String> userIds);

    List<UserRole> findByIdRoleId(String roleId);

    void deleteByIdUserId(String userId);
}
