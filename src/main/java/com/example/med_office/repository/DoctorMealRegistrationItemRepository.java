package com.example.med_office.repository;

import java.util.List;

import com.example.med_office.entity.DoctorMealRegistrationItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorMealRegistrationItemRepository extends JpaRepository<DoctorMealRegistrationItem, Long> {

    List<DoctorMealRegistrationItem> findByRegistrationIdOrderByIdAsc(Long registrationId);
}
