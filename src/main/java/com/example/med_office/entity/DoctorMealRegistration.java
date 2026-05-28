package com.example.med_office.entity;

import com.example.med_office.utils.UuidUtils;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "doctor_meal_registrations")
public class DoctorMealRegistration {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "week_year", nullable = false)
    private Integer weekYear;

    @Column(name = "week_number", nullable = false)
    private Integer weekNumber;

    @Column(name = "week_label", length = 64)
    private String weekLabel;

    @Column(name = "week_start_date")
    private LocalDate weekStartDate;

    @Column(name = "week_end_date")
    private LocalDate weekEndDate;

    @Column(name = "username", nullable = false, length = 128)
    private String username;

    @Column(name = "requester_username", length = 128)
    private String requesterUsername;

    @Column(name = "requester_full_name", length = 255)
    private String requesterFullName;

    @Column(name = "requester_department", length = 255)
    private String requesterDepartment;

    @Column(name = "requester_role", length = 128)
    private String requesterRole;

    @Column(name = "total_quantity")
    private Integer totalQuantity = 0;

    @Column(name = "total_amount", precision = 12, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "payload_json", nullable = false, columnDefinition = "LONGTEXT")
    private String payloadJson;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "registration", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DoctorMealRegistrationItem> items = new ArrayList<>();

    public void addItem(DoctorMealRegistrationItem item) {
        item.setRegistration(this);
        items.add(item);
    }

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
