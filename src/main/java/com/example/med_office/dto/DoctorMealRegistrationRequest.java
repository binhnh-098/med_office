package com.example.med_office.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoctorMealRegistrationRequest {

    @Valid
    @NotNull(message = "week is required")
    private WeekRequest week;

    @Valid
    private RequesterRequest requester;

    @Valid
    @NotEmpty(message = "items must contain at least one item")
    private List<ItemRequest> items;

    private SummaryRequest summary;

    private Instant generatedAt;

    @Getter
    @Setter
    public static class WeekRequest {
        @NotNull(message = "week.year is required")
        @Min(value = 1, message = "week.year must be a positive integer")
        private Integer year;

        @NotNull(message = "week.number is required")
        @Min(value = 1, message = "week.number must be a positive integer")
        private Integer number;

        private String label;

        private LocalDate startDate;

        private LocalDate endDate;
    }

    @Getter
    @Setter
    public static class RequesterRequest {
        private String username;

        private String fullName;

        private String department;

        private String role;
    }

    @Getter
    @Setter
    public static class SummaryRequest {
        private Integer totalSlots;

        private Integer selectedSlotCount;

        private Integer totalQuantity;

        private BigDecimal totalAmount;

        private Integer itemCount;
    }

    @Getter
    @Setter
    public static class ItemRequest {
        @NotNull(message = "item date is required")
        private LocalDate date;

        private String dayOfWeek;

        private String mealId;

        private String mealLabel;

        @Min(value = 0, message = "mealQuantity must be greater than or equal to 0")
        private Integer mealQuantity;

        private BigDecimal mealAmount;

        @Valid
        @JsonAlias("dishes")
        private List<MealSnapshotRequest> mealSnapshots;
    }

    @Getter
    @Setter
    public static class MealSnapshotRequest {
        private String name;

        private String servingTime;

        @Min(value = 0, message = "quantity must be greater than or equal to 0")
        private Integer quantity;

        private BigDecimal unitPrice;

        private BigDecimal lineTotal;
    }
}
