package com.example.med_office.entity;

import com.example.med_office.utils.UuidUtils;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "doctor_meal_dishes")
public class DoctorMealDish {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "week_year", nullable = false)
    private Integer weekYear;

    @Column(name = "week_number", nullable = false)
    private Integer weekNumber;

    @Column(name = "day_of_week", nullable = false, length = 32)
    private String dayOfWeek;

    @Column(name = "meal_date", nullable = false)
    private LocalDate date;

    @Column(name = "meal_id", nullable = false, length = 32)
    private String mealId;

    @Column(name = "meal_label", nullable = false, length = 32)
    private String mealLabel;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "calories")
    private Integer calories;

    @Column(name = "serving_time", length = 32)
    private String servingTime;

    @Column(name = "note", length = 1000)
    private String note;

    @Column(name = "created_by", nullable = false, length = 128)
    private String createdBy;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (id == null || id.isBlank()) {
            id = UuidUtils.newUuid();
        }
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
