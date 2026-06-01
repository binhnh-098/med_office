package com.example.med_office.repository;

import com.example.med_office.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, String> {

    Optional<Role> findByCodeIgnoreCase(String code);

    List<Role> findByCodeIn(Collection<String> codes);
}
