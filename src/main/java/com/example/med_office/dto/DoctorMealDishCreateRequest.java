package com.example.med_office.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

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

    public Integer getWeekYear() {
        return weekYear;
    }

    public void setWeekYear(Integer weekYear) {
        this.weekYear = weekYear;
    }

    public Integer getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(Integer weekNumber) {
        this.weekNumber = weekNumber;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getMealId() {
        return mealId;
    }

    public void setMealId(String mealId) {
        this.mealId = mealId;
    }

    public String getMealLabel() {
        return mealLabel;
    }

    public void setMealLabel(String mealLabel) {
        this.mealLabel = mealLabel;
    }

    public List<DishRequest> getDishes() {
        return dishes;
    }

    public void setDishes(List<DishRequest> dishes) {
        this.dishes = dishes;
    }

    public static class DishRequest {

        @NotBlank(message = "dish name is required")
        private String name;

        private BigDecimal price;

        private BigDecimal unitPrice;

        @Min(value = 0, message = "calories must be greater than or equal to 0")
        private Integer calories;

        private String servingTime;

        private String note;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public BigDecimal getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
        }

        public Integer getCalories() {
            return calories;
        }

        public void setCalories(Integer calories) {
            this.calories = calories;
        }

        public String getServingTime() {
            return servingTime;
        }

        public void setServingTime(String servingTime) {
            this.servingTime = servingTime;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }
    }
}
