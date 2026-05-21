package com.example.med_office.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.Min;

@Getter
@Setter
public class DoctorMealDishUpdateRequest {

    private Integer weekYear;

    @Min(value = 1, message = "weekNumber must be greater than 0")
    private Integer weekNumber;

    private String dayOfWeek;

    private LocalDate date;

    private String mealId;

    private String mealLabel;

    private String name;

    private BigDecimal price;

    private BigDecimal unitPrice;

    @Min(value = 0, message = "calories must be greater than or equal to 0")
    private Integer calories;

    private String servingTime;

    private String note;
}
