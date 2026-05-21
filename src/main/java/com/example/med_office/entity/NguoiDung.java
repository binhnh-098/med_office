package com.example.med_office.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "nguoi_dung")
public class NguoiDung {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ten_dang_nhap", nullable = false, unique = true, length = 100)
    private String tenDangNhap;

    @Column(name = "mat_khau_ma_hoa", nullable = false, length = 255)
    private String matKhauMaHoa;

    @Column(name = "ho_ten", nullable = false, length = 255)
    private String hoTen;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "so_dien_thoai", length = 20)
    private String soDienThoai;

    @Column(name = "phong_ban_id")
    private Long phongBanId;

    @Column(name = "chuc_vu_id")
    private Long chucVuId;

    @Column(name = "trang_thai", nullable = false, length = 50)
    private String trangThai;

    @Column(name = "lan_dang_nhap_cuoi")
    private LocalDateTime lanDangNhapCuoi;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @PrePersist
    public void prePersist() {
        if (trangThai == null || trangThai.isBlank()) {
            trangThai = "ACTIVE";
        }
        if (ngayTao == null) {
            ngayTao = LocalDateTime.now();
        }
        if (ngayCapNhat == null) {
            ngayCapNhat = ngayTao;
        }
    }
}
