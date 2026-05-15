CREATE DATABASE IF NOT EXISTS med_office
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'med_office'@'localhost' IDENTIFIED BY 'MedOffice@2026!';
ALTER USER 'med_office'@'localhost' IDENTIFIED BY 'MedOffice@2026!';
GRANT ALL PRIVILEGES ON med_office.* TO 'med_office'@'localhost';

CREATE USER IF NOT EXISTS 'med_office'@'127.0.0.1' IDENTIFIED BY 'MedOffice@2026!';
ALTER USER 'med_office'@'127.0.0.1' IDENTIFIED BY 'MedOffice@2026!';
GRANT ALL PRIVILEGES ON med_office.* TO 'med_office'@'127.0.0.1';

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

CREATE TABLE IF NOT EXISTS ho_so_nhan_vien (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nguoi_dung_id BIGINT NULL,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    birth_date DATE NULL,
    gender INT NULL,
    identity_number VARCHAR(50) NULL,
    social_insurance VARCHAR(50) NULL,
    email VARCHAR(255) NULL,
    phone VARCHAR(20) NULL,
    degree VARCHAR(100) NULL,
    specialty VARCHAR(255) NULL,
    specialty_name VARCHAR(255) NULL,
    academic_title VARCHAR(100) NULL,
    academic_title_name VARCHAR(255) NULL,
    certificate VARCHAR(100) NULL,
    position_code VARCHAR(100) NULL,
    position_name VARCHAR(255) NULL,
    honor_title VARCHAR(255) NULL,
    signing_pin VARCHAR(255) NULL,
    signing_account VARCHAR(255) NULL,
    signing_otp VARCHAR(255) NULL,
    invoice_password VARCHAR(255) NULL,
    avatar_image LONGTEXT NULL,
    signature_image LONGTEXT NULL,
    locked_from DATE NULL,
    locked_to DATE NULL,
    prescription_account VARCHAR(255) NULL,
    prescription_password VARCHAR(255) NULL,
    online_booking BIT(1) NOT NULL DEFAULT b'0',
    active BIT(1) NOT NULL DEFAULT b'1',
    note VARCHAR(2000) NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_ho_so_nhan_vien_code (code),
    UNIQUE KEY uk_ho_so_nhan_vien_nguoi_dung_id (nguoi_dung_id),
    KEY idx_ho_so_nhan_vien_name (name),
    KEY idx_ho_so_nhan_vien_active (active),
    KEY idx_ho_so_nhan_vien_specialty (specialty),
    CONSTRAINT fk_ho_so_nhan_vien_nguoi_dung
        FOREIGN KEY (nguoi_dung_id) REFERENCES nguoi_dung (id)
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

CREATE TABLE IF NOT EXISTS doctor_meal_registrations (
    id BIGINT NOT NULL AUTO_INCREMENT,
    week_year INT NOT NULL,
    week_number INT NOT NULL,
    username VARCHAR(128) NOT NULL,
    payload_json LONGTEXT NOT NULL,
    created_at DATETIME NOT NULL,
    PRIMARY KEY (id),
    KEY idx_doctor_meal_registrations_week_user (week_year, week_number, username),
    KEY idx_doctor_meal_registrations_created_at (created_at)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS doctor_meal_dishes (
    id BIGINT NOT NULL AUTO_INCREMENT,
    week_year INT NOT NULL,
    week_number INT NOT NULL,
    day_of_week VARCHAR(32) NOT NULL,
    meal_date DATE NOT NULL,
    meal_id VARCHAR(32) NOT NULL,
    meal_label VARCHAR(32) NOT NULL,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(12,2) NOT NULL,
    unit_price DECIMAL(12,2) NOT NULL,
    calories INT NULL,
    serving_time VARCHAR(32) NULL,
    note VARCHAR(1000) NULL,
    created_by VARCHAR(128) NOT NULL,
    created_at DATETIME NOT NULL,
    PRIMARY KEY (id),
    KEY idx_doctor_meal_dishes_week_day (week_year, week_number, day_of_week),
    KEY idx_doctor_meal_dishes_date_meal (meal_date, meal_id),
    KEY idx_doctor_meal_dishes_created_at (created_at)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;
