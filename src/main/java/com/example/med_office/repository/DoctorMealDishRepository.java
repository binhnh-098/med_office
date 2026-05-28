package com.example.med_office.repository;

import java.util.List;

import com.example.med_office.entity.DoctorMealDish;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorMealDishRepository extends JpaRepository<DoctorMealDish, String> {

    List<DoctorMealDish> findByWeekYearAndWeekNumberOrderByDateAscMealIdAscIdAsc(
            Integer weekYear,
            Integer weekNumber
    );

    List<DoctorMealDish> findByWeekYearAndWeekNumberAndDayOfWeekOrderByMealIdAscIdAsc(
            Integer weekYear,
            Integer weekNumber,
            String dayOfWeek
    );
}
