package com.example.med_office.repository;

import java.util.List;
import java.util.Optional;

import com.example.med_office.entity.DoctorMealRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorMealRegistrationRepository extends JpaRepository<DoctorMealRegistration, Long> {

    Optional<DoctorMealRegistration> findFirstByWeekYearAndWeekNumberAndUsernameOrderByIdDesc(
            Integer weekYear,
            Integer weekNumber,
            String username
    );

    List<DoctorMealRegistration> findByWeekYearAndWeekNumberAndUsernameOrderByIdDesc(
            Integer weekYear,
            Integer weekNumber,
            String username
    );

    Optional<DoctorMealRegistration> findByIdAndUsername(Long id, String username);
}
