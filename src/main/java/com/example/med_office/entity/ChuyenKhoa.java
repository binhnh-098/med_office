package com.example.med_office.entity;

import com.example.med_office.utils.UuidUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "chuyen_khoa")
public class ChuyenKhoa {

    @Id
    @Column(name = "id_chuyen_khoa", length = 36)
    private String idChuyenKhoa;

    @Column(name = "ten_chuyen_khoa", nullable = false, length = 255)
    private String tenChuyenKhoa;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @PrePersist
    public void prePersist() {
        if (idChuyenKhoa == null || idChuyenKhoa.isBlank()) {
            idChuyenKhoa = UuidUtils.newUuid();
        }
    }
}
