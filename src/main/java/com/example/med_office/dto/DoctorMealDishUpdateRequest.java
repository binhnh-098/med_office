package com.example.med_office.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.Min;

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
