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

CREATE TABLE IF NOT EXISTS roles (
    id CHAR(36) NOT NULL DEFAULT (UUID()),
    code VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000) NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_roles_code (code)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS permissions (
    id CHAR(36) NOT NULL DEFAULT (UUID()),
    code VARCHAR(150) NOT NULL,
    module_code VARCHAR(100) NOT NULL,
    module_name VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000) NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_permissions_code (code),
    KEY idx_permissions_module_code (module_code)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS role_permissions (
    role_id CHAR(36) NOT NULL,
    permission_id CHAR(36) NOT NULL,
    created_at DATETIME NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    KEY idx_role_permissions_permission_id (permission_id),
    CONSTRAINT fk_role_permissions_role
        FOREIGN KEY (role_id) REFERENCES roles (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_role_permissions_permission
        FOREIGN KEY (permission_id) REFERENCES permissions (id)
        ON DELETE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS chuc_vu (
    id CHAR(36) NOT NULL DEFAULT (UUID()),
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
    id CHAR(36) NOT NULL DEFAULT (UUID()),
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
    id CHAR(36) NOT NULL DEFAULT (UUID()),
    ten_dang_nhap VARCHAR(100) NOT NULL,
    mat_khau_ma_hoa VARCHAR(255) NOT NULL,
    phong_ban_id CHAR(36) NULL,
    chuc_vu_id CHAR(36) NULL,
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

CREATE TABLE IF NOT EXISTS user_roles (
    user_id CHAR(36) NOT NULL,
    role_id CHAR(36) NOT NULL,
    created_at DATETIME NOT NULL,
    PRIMARY KEY (user_id, role_id),
    KEY idx_user_roles_role_id (role_id),
    CONSTRAINT fk_user_roles_user
        FOREIGN KEY (user_id) REFERENCES nguoi_dung (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role
        FOREIGN KEY (role_id) REFERENCES roles (id)
        ON DELETE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

ALTER TABLE nguoi_dung
    ADD CONSTRAINT fk_nguoi_dung_chuc_vu
        FOREIGN KEY (chuc_vu_id) REFERENCES chuc_vu (id)
        ON DELETE SET NULL;

CREATE TABLE IF NOT EXISTS ho_so_nhan_vien (
    id CHAR(36) NOT NULL DEFAULT (UUID()),
    nguoi_dung_id CHAR(36) NULL,
    ma_nhan_vien VARCHAR(50) NOT NULL,
    ten_nhan_vien VARCHAR(255) NOT NULL,
    ngay_sinh DATE NULL,
    gioi_tinh INT NULL,
    so_dinh_danh VARCHAR(50) NULL,
    so_bao_hiem_xa_hoi VARCHAR(50) NULL,
    thu_dien_tu VARCHAR(255) NULL,
    so_dien_thoai VARCHAR(20) NULL,
    bang_cap VARCHAR(100) NULL,
    chuyen_khoa VARCHAR(255) NULL,
    hoc_ham VARCHAR(100) NULL,
    ten_hoc_ham VARCHAR(255) NULL,
    chung_chi VARCHAR(100) NULL,
    ma_chuc_vu VARCHAR(100) NULL,
    danh_hieu VARCHAR(255) NULL,
    ma_pin_ky VARCHAR(255) NULL,
    tai_khoan_ky VARCHAR(255) NULL,
    otp_ky VARCHAR(255) NULL,
    mat_khau_hoa_don VARCHAR(255) NULL,
    anh_dai_dien LONGTEXT NULL,
    anh_chu_ky LONGTEXT NULL,
    khoa_tu_ngay DATE NULL,
    khoa_den_ngay DATE NULL,
    tai_khoan_ke_don VARCHAR(255) NULL,
    mat_khau_ke_don VARCHAR(255) NULL,
    dat_lich_truc_tuyen BIT(1) NOT NULL DEFAULT b'0',
    dang_hoat_dong BIT(1) NOT NULL DEFAULT b'1',
    ghi_chu VARCHAR(2000) NULL,
    ngay_tao DATETIME NOT NULL,
    ngay_cap_nhat DATETIME NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_ho_so_nhan_vien_ma_nhan_vien (ma_nhan_vien),
    UNIQUE KEY uk_ho_so_nhan_vien_nguoi_dung_id (nguoi_dung_id),
    KEY idx_ho_so_nhan_vien_ten_nhan_vien (ten_nhan_vien),
    KEY idx_ho_so_nhan_vien_dang_hoat_dong (dang_hoat_dong),
    KEY idx_ho_so_nhan_vien_chuyen_khoa (chuyen_khoa),
    CONSTRAINT fk_ho_so_nhan_vien_nguoi_dung
        FOREIGN KEY (nguoi_dung_id) REFERENCES nguoi_dung (id)
        ON DELETE SET NULL
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS chuyen_khoa (
    id_chuyen_khoa CHAR(36) NOT NULL DEFAULT (UUID()),
    ten_chuyen_khoa VARCHAR(255) NOT NULL,
    nguoi_dung_id CHAR(36) NOT NULL,
    PRIMARY KEY (id_chuyen_khoa),
    KEY idx_chuyen_khoa_nguoi_dung_id (nguoi_dung_id),
    KEY idx_chuyen_khoa_ten_chuyen_khoa (ten_chuyen_khoa),
    CONSTRAINT fk_chuyen_khoa_nguoi_dung
        FOREIGN KEY (nguoi_dung_id) REFERENCES nguoi_dung (id)
        ON DELETE RESTRICT
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS cong_van_den (
    id CHAR(36) NOT NULL DEFAULT (UUID()),
    so_cong_van VARCHAR(100) NOT NULL,
    so_den VARCHAR(100) NOT NULL,
    tieu_de VARCHAR(500) NOT NULL,
    noi_dung_tom_tat VARCHAR(2000) NULL,
    don_vi_gui VARCHAR(255) NULL,
    don_vi_gui_id CHAR(36) NULL,
    nguoi_ky VARCHAR(255) NULL,
    ngay_van_ban DATE NULL,
    ngay_nhan DATE NULL,
    muc_do_khan VARCHAR(50) NULL,
    muc_do_mat VARCHAR(50) NULL,
    phong_ban_xu_ly_id CHAR(36) NULL,
    nguoi_xu_ly_id CHAR(36) NULL,
    nguon_nhan VARCHAR(100) NULL,
    han_xu_ly DATE NULL,
    do_khan_xu_ly VARCHAR(50) NULL,
    loai_van_ban_id CHAR(36) NULL,
    linh_vuc_id CHAR(36) NULL,
    ho_so_id CHAR(36) NULL,
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
    da_xoa BIT(1) NULL,
    nguoi_tao_id CHAR(36) NULL,
    nguoi_cap_nhat_id CHAR(36) NULL,
    PRIMARY KEY (id),
    KEY idx_cong_van_den_so_cong_van (so_cong_van),
    KEY idx_cong_van_den_so_den (so_den),
    KEY idx_cong_van_den_trang_thai (trang_thai),
    KEY idx_cong_van_den_ngay_nhan (ngay_nhan),
    KEY idx_cong_van_den_don_vi_gui_id (don_vi_gui_id),
    KEY idx_cong_van_den_nguoi_xu_ly_id (nguoi_xu_ly_id),
    KEY idx_cong_van_den_ho_so_id (ho_so_id),
    KEY idx_cong_van_den_nguoi_tao_id (nguoi_tao_id),
    KEY idx_cong_van_den_nguoi_cap_nhat_id (nguoi_cap_nhat_id),
    CONSTRAINT fk_cong_van_den_don_vi_gui
        FOREIGN KEY (don_vi_gui_id) REFERENCES nha_cung_cap (id)
        ON DELETE SET NULL,
    CONSTRAINT fk_cong_van_den_nguoi_xu_ly
        FOREIGN KEY (nguoi_xu_ly_id) REFERENCES nguoi_dung (id)
        ON DELETE SET NULL,
    CONSTRAINT fk_cong_van_den_ho_so
        FOREIGN KEY (ho_so_id) REFERENCES ho_so_nhan_vien (id)
        ON DELETE SET NULL,
    CONSTRAINT fk_cong_van_den_nguoi_tao
        FOREIGN KEY (nguoi_tao_id) REFERENCES nguoi_dung (id)
        ON DELETE SET NULL,
    CONSTRAINT fk_cong_van_den_nguoi_cap_nhat
        FOREIGN KEY (nguoi_cap_nhat_id) REFERENCES nguoi_dung (id)
        ON DELETE SET NULL
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS cong_van_di (
    id CHAR(36) NOT NULL DEFAULT (UUID()),
    so_cong_van VARCHAR(100) NOT NULL,
    tieu_de VARCHAR(500) NOT NULL,
    noi_dung_tom_tat VARCHAR(2000) NULL,
    don_vi_nhan VARCHAR(255) NULL,
    ngay_ban_hanh DATE NULL,
    nguoi_ky_id CHAR(36) NULL,
    trang_thai VARCHAR(50) NULL,
    ngay_tao DATETIME NULL,
    ngay_cap_nhat DATETIME NULL,
    PRIMARY KEY (id),
    KEY idx_cong_van_di_so_cong_van (so_cong_van),
    KEY idx_cong_van_di_trang_thai (trang_thai),
    KEY idx_cong_van_di_ngay_ban_hanh (ngay_ban_hanh),
    KEY idx_cong_van_di_nguoi_ky_id (nguoi_ky_id),
    CONSTRAINT fk_cong_van_di_nguoi_ky
        FOREIGN KEY (nguoi_ky_id) REFERENCES nguoi_dung (id)
        ON DELETE SET NULL
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS lich_su_chat_rowboat (
    id CHAR(36) NOT NULL DEFAULT (UUID()),
    ten_dang_nhap VARCHAR(100) NULL,
    tin_nhan_nguoi_dung VARCHAR(4000) NULL,
    tin_nhan_tro_ly VARCHAR(4000) NULL,
    trang_thai_yeu_cau_json LONGTEXT NULL,
    trang_thai_phan_hoi_json LONGTEXT NULL,
    trang_thai VARCHAR(20) NOT NULL,
    thong_bao_loi VARCHAR(2000) NULL,
    ngay_tao DATETIME NOT NULL,
    PRIMARY KEY (id),
    KEY idx_lich_su_chat_rowboat_ten_dang_nhap (ten_dang_nhap),
    KEY idx_lich_su_chat_rowboat_trang_thai (trang_thai),
    KEY idx_lich_su_chat_rowboat_ngay_tao (ngay_tao)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS dang_ky_bua_an_bac_si (
    id CHAR(36) NOT NULL DEFAULT (UUID()),
    nam_tuan INT NOT NULL,
    so_tuan INT NOT NULL,
    nhan_tuan VARCHAR(64) NULL,
    ngay_bat_dau_tuan DATE NULL,
    ngay_ket_thuc_tuan DATE NULL,
    ten_dang_nhap VARCHAR(128) NOT NULL,
    ten_dang_nhap_nguoi_dang_ky VARCHAR(128) NULL,
    ho_ten_nguoi_dang_ky VARCHAR(255) NULL,
    phong_ban_nguoi_dang_ky VARCHAR(255) NULL,
    vai_tro_nguoi_dang_ky VARCHAR(128) NULL,
    tong_so_luong INT NULL,
    tong_tien DECIMAL(12,2) NULL,
    du_lieu_json LONGTEXT NOT NULL,
    ngay_tao DATETIME NOT NULL,
    PRIMARY KEY (id),
    KEY idx_dang_ky_bua_an_bac_si_tuan_nguoi_dung (nam_tuan, so_tuan, ten_dang_nhap),
    KEY idx_dang_ky_bua_an_bac_si_tuan_nguoi_dang_ky (nam_tuan, so_tuan, ten_dang_nhap_nguoi_dang_ky),
    KEY idx_dang_ky_bua_an_bac_si_ngay_tao (ngay_tao)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS chi_tiet_dang_ky_bua_an_bac_si (
    id CHAR(36) NOT NULL DEFAULT (UUID()),
    dang_ky_id CHAR(36) NOT NULL,
    ngay_an DATE NOT NULL,
    thu_trong_tuan VARCHAR(32) NULL,
    ma_bua_an VARCHAR(32) NULL,
    ten_bua_an VARCHAR(32) NULL,
    so_luong_bua_an INT NOT NULL,
    thanh_tien_bua_an DECIMAL(12,2) NOT NULL,
    PRIMARY KEY (id),
    KEY idx_chi_tiet_dang_ky_bua_an_bac_si_dang_ky (dang_ky_id),
    KEY idx_chi_tiet_dang_ky_bua_an_bac_si_ngay_bua_an (ngay_an, ma_bua_an),
    CONSTRAINT fk_chi_tiet_dang_ky_bua_an_bac_si_dang_ky
        FOREIGN KEY (dang_ky_id) REFERENCES dang_ky_bua_an_bac_si (id)
        ON DELETE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS mon_an_trong_dang_ky_bua_an_bac_si (
    id CHAR(36) NOT NULL DEFAULT (UUID()),
    chi_tiet_dang_ky_id CHAR(36) NOT NULL,
    ten_mon_an VARCHAR(255) NOT NULL,
    gio_phuc_vu VARCHAR(32) NULL,
    so_luong INT NOT NULL,
    don_gia DECIMAL(12,2) NOT NULL,
    thanh_tien DECIMAL(12,2) NOT NULL,
    PRIMARY KEY (id),
    KEY idx_mon_an_trong_dang_ky_bua_an_bac_si_chi_tiet (chi_tiet_dang_ky_id),
    CONSTRAINT fk_mon_an_trong_dang_ky_bua_an_bac_si_chi_tiet
        FOREIGN KEY (chi_tiet_dang_ky_id) REFERENCES chi_tiet_dang_ky_bua_an_bac_si (id)
        ON DELETE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS mon_an_bac_si (
    id CHAR(36) NOT NULL DEFAULT (UUID()),
    nam_tuan INT NOT NULL,
    so_tuan INT NOT NULL,
    thu_trong_tuan VARCHAR(32) NOT NULL,
    ngay_an DATE NOT NULL,
    ma_bua_an VARCHAR(32) NOT NULL,
    ten_bua_an VARCHAR(32) NOT NULL,
    ten_mon_an VARCHAR(255) NOT NULL,
    gia DECIMAL(12,2) NOT NULL,
    don_gia DECIMAL(12,2) NOT NULL,
    calo INT NULL,
    gio_phuc_vu VARCHAR(32) NULL,
    ghi_chu VARCHAR(1000) NULL,
    nguoi_tao VARCHAR(128) NOT NULL,
    ngay_tao DATETIME NOT NULL,
    PRIMARY KEY (id),
    KEY idx_mon_an_bac_si_tuan_thu (nam_tuan, so_tuan, thu_trong_tuan),
    KEY idx_mon_an_bac_si_ngay_bua_an (ngay_an, ma_bua_an),
    KEY idx_mon_an_bac_si_ngay_tao (ngay_tao)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;
