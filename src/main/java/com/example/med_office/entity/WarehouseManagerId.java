package com.example.med_office.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class WarehouseManagerId implements Serializable {

    @Column(name = "warehouse_id", length = 36)
    private String warehouseId;

    @Column(name = "employee_profile_id", length = 36)
    private String employeeProfileId;
}
