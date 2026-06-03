package com.example.med_office.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "warehouse_managers")
public class WarehouseManager {

    @EmbeddedId
    private WarehouseManagerId id;
}
