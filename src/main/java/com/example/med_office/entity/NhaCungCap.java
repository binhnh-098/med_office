package com.example.med_office.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "nha_cung_cap")
public class NhaCungCap {

    @Id
    private Long id;

    @Column(name = "ma_nha_cung_cap", length = 50)
    private String maNhaCungCap;

    @Column(name = "ten_nha_cung_cap", nullable = false, length = 255)
    private String tenNhaCungCap;

    @Column(name = "trang_thai", length = 50)
    private String trangThai;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;
}
