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

    Optional<DoctorMealRegistration> findFirstByWeekYearAndWeekNumberAndRequesterUsernameOrderByIdDesc(
            Integer weekYear,
            Integer weekNumber,
            String requesterUsername
    );

    List<DoctorMealRegistration> findByWeekYearAndWeekNumberAndUsernameOrderByIdDesc(
            Integer weekYear,
            Integer weekNumber,
            String username
    );

    List<DoctorMealRegistration> findByWeekYearAndWeekNumberAndRequesterUsernameOrderByIdDesc(
            Integer weekYear,
            Integer weekNumber,
            String requesterUsername
    );

    Optional<DoctorMealRegistration> findByIdAndUsername(Long id, String username);

    Optional<DoctorMealRegistration> findByIdAndRequesterUsername(Long id, String requesterUsername);
}
