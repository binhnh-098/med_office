package com.example.med_office.entity;

import com.example.med_office.utils.UuidUtils;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "chuc_vu")
public class ChucVu {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "ma_chuc_vu", nullable = false, length = 50)
    private String maChucVu;

    @Column(name = "ten_chuc_vu", nullable = false, length = 255)
    private String tenChucVu;

    @Column(name = "cap_bac")
    private Integer capBac;

    @Column(name = "mo_ta", length = 1000)
    private String moTa;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (id == null || id.isBlank()) {
            id = UuidUtils.newUuid();
        }
        if (ngayTao == null) {
            ngayTao = now;
        }
        if (ngayCapNhat == null) {
            ngayCapNhat = now;
        }
    }

    @PreUpdate
    public void preUpdate() {
        ngayCapNhat = LocalDateTime.now();
    }
}
