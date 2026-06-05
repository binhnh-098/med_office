package com.example.med_office.repository;

import com.example.med_office.entity.NhaCungCap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface NhaCungCapRepository extends JpaRepository<NhaCungCap, String>, JpaSpecificationExecutor<NhaCungCap> {

    List<NhaCungCap> findTop20ByTenNhaCungCapContainingIgnoreCaseOrderByTenNhaCungCapAsc(String keyword);

    List<NhaCungCap> findTop20ByOrderByTenNhaCungCapAsc();
}
