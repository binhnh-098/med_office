package com.example.med_office.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "doctor_meal_registration_items")
public class DoctorMealRegistrationItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "registration_id", nullable = false)
    private DoctorMealRegistration registration;

    @Column(name = "meal_date", nullable = false)
    private LocalDate date;

    @Column(name = "day_of_week", length = 32)
    private String dayOfWeek;

    @Column(name = "meal_id", length = 32)
    private String mealId;

    @Column(name = "meal_label", length = 32)
    private String mealLabel;

    @Column(name = "meal_quantity", nullable = false)
    private Integer mealQuantity;

    @Column(name = "meal_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal mealAmount;

    @OneToMany(mappedBy = "registrationItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DoctorMealRegistrationItemSnapshot> mealSnapshots = new ArrayList<>();

    public void addMealSnapshot(DoctorMealRegistrationItemSnapshot snapshot) {
        snapshot.setRegistrationItem(this);
        mealSnapshots.add(snapshot);
    }
}
