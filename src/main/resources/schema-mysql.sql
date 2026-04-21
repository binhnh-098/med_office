CREATE DATABASE IF NOT EXISTS med_office
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE med_office;

CREATE TABLE IF NOT EXISTS chuc_vu (
    id BIGINT NOT NULL,
    ma_chuc_vu VARCHAR(50) NOT NULL,
    ten_chuc_vu VARCHAR(255) NOT NULL,
    cap_bac INT NULL,
    mo_ta VARCHAR(1000) NULL,
    ngay_tao DATETIME NULL,
    ngay_cap_nhat DATETIME NULL,
    PRIMARY KEY (id),
    KEY idx_chuc_vu_ma_chuc_vu (ma_chuc_vu)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS nha_cung_cap (
    id BIGINT NOT NULL,
    ma_nha_cung_cap VARCHAR(50) NULL,
    ten_nha_cung_cap VARCHAR(255) NOT NULL,
    trang_thai VARCHAR(50) NULL,
    ngay_tao DATETIME NULL,
    PRIMARY KEY (id),
    KEY idx_nha_cung_cap_ma_nha_cung_cap (ma_nha_cung_cap)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS nguoi_dung (
    id BIGINT NOT NULL AUTO_INCREMENT,
    ten_dang_nhap VARCHAR(100) NOT NULL,
    mat_khau_ma_hoa VARCHAR(255) NOT NULL,
    ho_ten VARCHAR(255) NOT NULL,
    email VARCHAR(255) NULL,
    so_dien_thoai VARCHAR(20) NULL,
    phong_ban_id BIGINT NULL,
    chuc_vu_id BIGINT NULL,
    trang_thai VARCHAR(50) NOT NULL,
    lan_dang_nhap_cuoi DATETIME NULL,
    ngay_tao DATETIME NOT NULL,
    ngay_cap_nhat DATETIME NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_nguoi_dung_ten_dang_nhap (ten_dang_nhap),
    KEY idx_nguoi_dung_trang_thai (trang_thai),
    KEY idx_nguoi_dung_chuc_vu_id (chuc_vu_id)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS cong_van_den (
    id BIGINT NOT NULL AUTO_INCREMENT,
    so_cong_van VARCHAR(100) NOT NULL,
    so_den VARCHAR(100) NOT NULL,
    tieu_de VARCHAR(500) NOT NULL,
    noi_dung_tom_tat VARCHAR(2000) NULL,
    don_vi_gui VARCHAR(255) NULL,
    don_vi_gui_id INT NULL,
    nguoi_ky VARCHAR(255) NULL,
    ngay_van_ban DATE NULL,
    ngay_nhan DATE NULL,
    muc_do_khan VARCHAR(50) NULL,
    muc_do_mat VARCHAR(50) NULL,
    phong_ban_xu_ly_id INT NULL,
    nguoi_xu_ly_id INT NULL,
    nguon_nhan VARCHAR(100) NULL,
    han_xu_ly DATE NULL,
    do_khan_xu_ly VARCHAR(50) NULL,
    loai_van_ban_id INT NULL,
    linh_vuc_id INT NULL,
    ho_so_id INT NULL,
    so_trang INT NULL,
    so_ban INT NULL,
    trich_yeu VARCHAR(2000) NULL,
    ghi_chu VARCHAR(2000) NULL,
    y_kien_chi_dao VARCHAR(2000) NULL,
    tep_dinh_kem_chinh VARCHAR(1000) NULL,
    trang_thai VARCHAR(50) NULL,
    ngay_tao DATETIME NULL,
    ngay_cap_nhat DATETIME NULL,
    da_doc BIT(1) NULL,
    da_xu_ly BIT(1) NULL,
    is_deleted BIT(1) NULL,
    nguoi_tao_id INT NULL,
    nguoi_cap_nhat_id INT NULL,
    PRIMARY KEY (id),
    KEY idx_cong_van_den_so_cong_van (so_cong_van),
    KEY idx_cong_van_den_so_den (so_den),
    KEY idx_cong_van_den_trang_thai (trang_thai),
    KEY idx_cong_van_den_ngay_nhan (ngay_nhan)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS cong_van_di (
    id BIGINT NOT NULL AUTO_INCREMENT,
    so_cong_van VARCHAR(100) NOT NULL,
    tieu_de VARCHAR(500) NOT NULL,
    noi_dung_tom_tat VARCHAR(2000) NULL,
    don_vi_nhan VARCHAR(255) NULL,
    ngay_ban_hanh DATE NULL,
    nguoi_ky_id INT NULL,
    trang_thai VARCHAR(50) NULL,
    ngay_tao DATETIME NULL,
    ngay_cap_nhat DATETIME NULL,
    PRIMARY KEY (id),
    KEY idx_cong_van_di_so_cong_van (so_cong_van),
    KEY idx_cong_van_di_trang_thai (trang_thai),
    KEY idx_cong_van_di_ngay_ban_hanh (ngay_ban_hanh)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS rowboat_chat_histories (
    id BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR(100) NULL,
    user_message VARCHAR(4000) NULL,
    assistant_message VARCHAR(4000) NULL,
    request_state_json LONGTEXT NULL,
    response_state_json LONGTEXT NULL,
    status VARCHAR(20) NOT NULL,
    error_message VARCHAR(2000) NULL,
    created_at DATETIME NOT NULL,
    PRIMARY KEY (id),
    KEY idx_rowboat_chat_histories_username (username),
    KEY idx_rowboat_chat_histories_status (status),
    KEY idx_rowboat_chat_histories_created_at (created_at)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;
