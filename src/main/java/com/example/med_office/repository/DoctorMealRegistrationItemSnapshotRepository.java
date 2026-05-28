package com.example.med_office.repository;

import java.util.List;

import com.example.med_office.entity.DoctorMealRegistrationItemSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorMealRegistrationItemSnapshotRepository extends JpaRepository<DoctorMealRegistrationItemSnapshot, String> {

    List<DoctorMealRegistrationItemSnapshot> findByRegistrationItemIdOrderByIdAsc(String registrationItemId);
}
