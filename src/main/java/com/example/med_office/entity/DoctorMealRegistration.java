package com.example.med_office.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "doctor_meal_registrations")
public class DoctorMealRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "week_year", nullable = false)
    private Integer weekYear;

    @Column(name = "week_number", nullable = false)
    private Integer weekNumber;

    @Column(name = "username", nullable = false, length = 128)
    private String username;

    @Column(name = "payload_json", nullable = false, columnDefinition = "LONGTEXT")
    private String payloadJson;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPayloadJson() {
        return payloadJson;
    }

    public void setPayloadJson(String payloadJson) {
        this.payloadJson = payloadJson;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
