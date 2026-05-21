package com.example.med_office.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class DoctorMealDishCreateRequest {

    @NotNull(message = "weekYear is required")
    private Integer weekYear;

    @NotNull(message = "weekNumber is required")
    @Min(value = 1, message = "weekNumber must be greater than 0")
    private Integer weekNumber;

    @NotBlank(message = "dayOfWeek is required")
    private String dayOfWeek;

    @NotNull(message = "date is required")
    private LocalDate date;

    @NotBlank(message = "mealId is required")
    private String mealId;

    private String mealLabel;

    @Valid
    @NotEmpty(message = "dishes must contain at least one item")
    private List<DishRequest> dishes;

    @Getter
    @Setter
    public static class DishRequest {

        @NotBlank(message = "dish name is required")
        private String name;

        private BigDecimal price;

        private BigDecimal unitPrice;

        @Min(value = 0, message = "calories must be greater than or equal to 0")
        private Integer calories;

        private String servingTime;

        private String note;
    }
}
