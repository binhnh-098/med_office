package com.example.med_office.repository;

import com.example.med_office.entity.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VehicleRepository extends JpaRepository<Vehicle, String> {

    @Query("SELECT v FROM Vehicle v WHERE " +
           "LOWER(v.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(v.licensePlate) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(v.driverName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(v.driverPhone) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Vehicle> search(@Param("keyword") String keyword, Pageable pageable);
}
