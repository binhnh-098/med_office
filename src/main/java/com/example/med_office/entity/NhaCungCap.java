package com.example.med_office.entity;

import com.example.med_office.utils.UuidUtils;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "nha_cung_cap")
public class NhaCungCap {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "ma_nha_cung_cap", length = 50)
    private String maNhaCungCap;

    @Column(name = "ten_nha_cung_cap", nullable = false, length = 255)
    private String tenNhaCungCap;

    @Column(name = "trang_thai", length = 50)
    private String trangThai;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @PrePersist
    public void prePersist() {
        if (id == null || id.isBlank()) {
            id = UuidUtils.newUuid();
        }
        if (ngayTao == null) {
            ngayTao = LocalDateTime.now();
        }
    }
}
