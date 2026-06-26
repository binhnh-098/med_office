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
    cap_tren_truc_tiep_id CHAR(36) NULL,
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
    KEY idx_ho_so_nhan_vien_cap_tren_truc_tiep_id (cap_tren_truc_tiep_id),
    CONSTRAINT fk_ho_so_nhan_vien_nguoi_dung
        FOREIGN KEY (nguoi_dung_id) REFERENCES nguoi_dung (id)
        ON DELETE SET NULL,
    CONSTRAINT fk_ho_so_nhan_vien_cap_tren_truc_tiep
        FOREIGN KEY (cap_tren_truc_tiep_id) REFERENCES ho_so_nhan_vien (id)
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

CREATE TABLE IF NOT EXISTS warehouses (
    id CHAR(36) NOT NULL DEFAULT (UUID()),
    code VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NULL,
    location VARCHAR(500) NOT NULL,
    note VARCHAR(2000) NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    parent_warehouse_id CHAR(36) NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_warehouses_code (code),
    KEY idx_warehouses_status (status),
    KEY idx_warehouses_parent (parent_warehouse_id),
    KEY idx_warehouses_created_at (created_at),
    CONSTRAINT fk_warehouses_parent
        FOREIGN KEY (parent_warehouse_id) REFERENCES warehouses (id)
        ON DELETE SET NULL
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS warehouse_managers (
    warehouse_id CHAR(36) NOT NULL,
    employee_profile_id CHAR(36) NOT NULL,
    PRIMARY KEY (warehouse_id, employee_profile_id),
    KEY idx_warehouse_managers_employee (employee_profile_id),
    CONSTRAINT fk_warehouse_managers_warehouse
        FOREIGN KEY (warehouse_id) REFERENCES warehouses (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_warehouse_managers_employee
        FOREIGN KEY (employee_profile_id) REFERENCES ho_so_nhan_vien (id)
        ON DELETE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS warehouse_inbounds (
    id CHAR(36) NOT NULL DEFAULT (UUID()),
    code VARCHAR(50) NOT NULL,
    receipt_date DATE NOT NULL,
    status VARCHAR(30) NOT NULL,
    warehouse_id CHAR(36) NOT NULL,
    warehouse_name VARCHAR(255) NOT NULL,
    supplier_id CHAR(36) NULL,
    supplier_name VARCHAR(255) NULL,
    invoice_number VARCHAR(100) NULL,
    source_document VARCHAR(100) NULL,
    delivery_by VARCHAR(255) NULL,
    received_by VARCHAR(255) NULL,
    note VARCHAR(2000) NULL,
    approval_note VARCHAR(2000) NULL,
    rejection_reason VARCHAR(2000) NULL,
    completed_at DATETIME NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_warehouse_inbounds_code (code),
    KEY idx_warehouse_inbounds_status (status),
    KEY idx_warehouse_inbounds_receipt_date (receipt_date),
    KEY idx_warehouse_inbounds_warehouse_id (warehouse_id),
    KEY idx_warehouse_inbounds_supplier_id (supplier_id),
    CONSTRAINT fk_warehouse_inbounds_warehouse
        FOREIGN KEY (warehouse_id) REFERENCES warehouses (id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_warehouse_inbounds_supplier
        FOREIGN KEY (supplier_id) REFERENCES nha_cung_cap (id)
        ON DELETE SET NULL
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS warehouse_inbound_items (
    id CHAR(36) NOT NULL DEFAULT (UUID()),
    warehouse_inbound_id CHAR(36) NOT NULL,
    item_id VARCHAR(100) NULL,
    item_code VARCHAR(100) NULL,
    item_name VARCHAR(255) NOT NULL,
    unit VARCHAR(100) NULL,
    quantity DECIMAL(18,2) NOT NULL,
    unit_price DECIMAL(18,2) NOT NULL,
    line_total DECIMAL(18,2) NOT NULL,
    batch_number VARCHAR(100) NULL,
    expiry_date DATE NULL,
    min_quantity DECIMAL(18,2) NULL,
    PRIMARY KEY (id),
    KEY idx_warehouse_inbound_items_inbound_id (warehouse_inbound_id),
    KEY idx_warehouse_inbound_items_item_code (item_code),
    KEY idx_warehouse_inbound_items_item_name (item_name),
    CONSTRAINT fk_warehouse_inbound_items_inbound
        FOREIGN KEY (warehouse_inbound_id) REFERENCES warehouse_inbounds (id)
        ON DELETE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS warehouse_inventory_min_quantities (
    id CHAR(36) NOT NULL DEFAULT (UUID()),
    inventory_key VARCHAR(512) NOT NULL,
    warehouse_id CHAR(36) NOT NULL,
    item_id VARCHAR(100) NULL,
    item_code VARCHAR(100) NULL,
    batch_number VARCHAR(100) NULL,
    expiry_date DATE NULL,
    unit VARCHAR(100) NULL,
    min_quantity DECIMAL(18,2) NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_warehouse_inventory_min_quantities_inventory_key (inventory_key),
    KEY idx_warehouse_inventory_min_quantities_warehouse_id (warehouse_id)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS warehouse_outbounds (
    id CHAR(36) NOT NULL DEFAULT (UUID()),
    code VARCHAR(50) NOT NULL,
    outbound_date DATE NOT NULL,
    status VARCHAR(30) NOT NULL,
    warehouse_id CHAR(36) NOT NULL,
    warehouse_name VARCHAR(255) NOT NULL,
    destination_warehouse_id CHAR(36) NULL,
    destination_name VARCHAR(255) NULL,
    received_by VARCHAR(255) NULL,
    requested_by VARCHAR(255) NULL,
    note VARCHAR(2000) NULL,
    approval_note VARCHAR(2000) NULL,
    rejection_reason VARCHAR(2000) NULL,
    completed_at DATETIME NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_warehouse_outbounds_code (code),
    KEY idx_warehouse_outbounds_status (status),
    KEY idx_warehouse_outbounds_outbound_date (outbound_date),
    KEY idx_warehouse_outbounds_warehouse_id (warehouse_id),
    KEY idx_warehouse_outbounds_destination_warehouse_id (destination_warehouse_id),
    CONSTRAINT fk_warehouse_outbounds_warehouse
        FOREIGN KEY (warehouse_id) REFERENCES warehouses (id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_warehouse_outbounds_destination_warehouse
        FOREIGN KEY (destination_warehouse_id) REFERENCES warehouses (id)
        ON DELETE RESTRICT
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS warehouse_outbound_items (
    id CHAR(36) NOT NULL DEFAULT (UUID()),
    warehouse_outbound_id CHAR(36) NOT NULL,
    item_id VARCHAR(100) NULL,
    item_code VARCHAR(100) NULL,
    item_name VARCHAR(255) NOT NULL,
    unit VARCHAR(100) NULL,
    quantity DECIMAL(18,2) NOT NULL,
    batch_number VARCHAR(100) NULL,
    expiry_date DATE NULL,
    note VARCHAR(1000) NULL,
    PRIMARY KEY (id),
    KEY idx_warehouse_outbound_items_outbound_id (warehouse_outbound_id),
    KEY idx_warehouse_outbound_items_item_code (item_code),
    KEY idx_warehouse_outbound_items_item_name (item_name),
    CONSTRAINT fk_warehouse_outbound_items_outbound
        FOREIGN KEY (warehouse_outbound_id) REFERENCES warehouse_outbounds (id)
        ON DELETE CASCADE
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

CREATE TABLE IF NOT EXISTS contracts (
    id CHAR(36) NOT NULL DEFAULT (UUID()),
    ho_so_nhan_vien_id CHAR(36) NOT NULL,
    contract_number VARCHAR(100) NOT NULL,
    contract_type VARCHAR(100) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NULL,
    salary DECIMAL(18, 2) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    note VARCHAR(2000) NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_contracts_number (contract_number),
    KEY idx_contracts_employee_id (ho_so_nhan_vien_id),
    KEY idx_contracts_status (status),
    CONSTRAINT fk_contracts_employee
        FOREIGN KEY (ho_so_nhan_vien_id) REFERENCES ho_so_nhan_vien (id)
        ON DELETE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS business_trips (
    id CHAR(36) NOT NULL DEFAULT (UUID()),
    ho_so_nhan_vien_id CHAR(36) NOT NULL,
    destination VARCHAR(255) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    purpose VARCHAR(1000) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    approver_id CHAR(36) NULL,
    reject_reason VARCHAR(1000) NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NULL,
    PRIMARY KEY (id),
    KEY idx_business_trips_employee_id (ho_so_nhan_vien_id),
    KEY idx_business_trips_status (status),
    KEY idx_business_trips_dates (start_date, end_date),
    CONSTRAINT fk_business_trips_employee
        FOREIGN KEY (ho_so_nhan_vien_id) REFERENCES ho_so_nhan_vien (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_business_trips_approver
        FOREIGN KEY (approver_id) REFERENCES ho_so_nhan_vien (id)
        ON DELETE SET NULL
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS vehicles (
    id CHAR(36) NOT NULL DEFAULT (UUID()),
    name VARCHAR(255) NOT NULL,
    license_plate VARCHAR(50) NOT NULL UNIQUE,
    driver_name VARCHAR(255) NOT NULL,
    driver_phone VARCHAR(50) NOT NULL,
    seat_capacity INT NOT NULL DEFAULT 4,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS vehicle_requests (
    id CHAR(36) NOT NULL DEFAULT (UUID()),
    ho_so_nhan_vien_id CHAR(36) NOT NULL,
    vehicle_id CHAR(36) NULL,
    vehicle_type VARCHAR(50) NOT NULL,
    departure_time DATETIME NOT NULL,
    return_time DATETIME NOT NULL,
    route_description VARCHAR(1000) NOT NULL,
    passenger_count INT NOT NULL DEFAULT 1,
    purpose VARCHAR(1000) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    approver_id CHAR(36) NULL,
    driver_name VARCHAR(255) NULL,
    driver_phone VARCHAR(50) NULL,
    license_plate VARCHAR(50) NULL,
    reject_reason VARCHAR(1000) NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NULL,
    PRIMARY KEY (id),
    KEY idx_vehicle_requests_employee_id (ho_so_nhan_vien_id),
    KEY idx_vehicle_requests_status (status),
    CONSTRAINT fk_vehicle_requests_employee
        FOREIGN KEY (ho_so_nhan_vien_id) REFERENCES ho_so_nhan_vien (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_vehicle_requests_approver
        FOREIGN KEY (approver_id) REFERENCES ho_so_nhan_vien (id)
        ON DELETE SET NULL,
    CONSTRAINT fk_vehicle_requests_vehicle
        FOREIGN KEY (vehicle_id) REFERENCES vehicles (id)
        ON DELETE SET NULL
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS assets (
    id CHAR(36) NOT NULL DEFAULT (UUID()),
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(100) NOT NULL,
    unit VARCHAR(50) NOT NULL,
    model VARCHAR(100) NULL,
    serial_number VARCHAR(100) NULL,
    brand VARCHAR(100) NULL,
    manufacturer VARCHAR(100) NULL,
    image LONGTEXT NULL,
    specification VARCHAR(1000) NULL,
    purchase_price DECIMAL(18,2) NULL,
    purchase_date DATE NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    current_employee_id CHAR(36) NULL,
    current_department VARCHAR(255) NULL,
    description VARCHAR(1000) NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_assets_current_employee FOREIGN KEY (current_employee_id) REFERENCES ho_so_nhan_vien(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS asset_handovers (
    id CHAR(36) NOT NULL DEFAULT (UUID()),
    asset_id CHAR(36) NOT NULL,
    type VARCHAR(50) NOT NULL,
    from_employee_id CHAR(36) NULL,
    to_employee_id CHAR(36) NULL,
    from_department VARCHAR(255) NULL,
    to_department VARCHAR(255) NULL,
    handover_date DATE NOT NULL,
    document_number VARCHAR(100) NULL,
    note VARCHAR(1000) NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'COMPLETED',
    created_at DATETIME NOT NULL,
    updated_at DATETIME NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_asset_handovers_asset FOREIGN KEY (asset_id) REFERENCES assets(id) ON DELETE CASCADE,
    CONSTRAINT fk_asset_handovers_from_emp FOREIGN KEY (from_employee_id) REFERENCES ho_so_nhan_vien(id) ON DELETE SET NULL,
    CONSTRAINT fk_asset_handovers_to_emp FOREIGN KEY (to_employee_id) REFERENCES ho_so_nhan_vien(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS asset_maintenances (
    id CHAR(36) NOT NULL DEFAULT (UUID()),
    asset_id CHAR(36) NOT NULL,
    provider VARCHAR(255) NULL,
    cost DECIMAL(15,2) NULL,
    maintenance_date DATE NOT NULL,
    completion_date DATE NULL,
    content VARCHAR(1000) NULL,
    notes VARCHAR(1000) NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'UNDER_MAINTENANCE',
    created_at DATETIME NOT NULL,
    updated_at DATETIME NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_asset_maintenances_asset FOREIGN KEY (asset_id) REFERENCES assets(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS asset_inventories (
    id CHAR(36) NOT NULL DEFAULT (UUID()),
    document_number VARCHAR(100) NOT NULL UNIQUE,
    inventory_date DATE NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    notes VARCHAR(1000) NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS asset_inventory_details (
    id CHAR(36) NOT NULL DEFAULT (UUID()),
    inventory_id CHAR(36) NOT NULL,
    asset_id CHAR(36) NOT NULL,
    is_present BIT(1) NOT NULL DEFAULT b'1',
    current_status VARCHAR(50) NOT NULL,
    actual_status VARCHAR(50) NOT NULL,
    note VARCHAR(1000) NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_inventory_details_inventory FOREIGN KEY (inventory_id) REFERENCES asset_inventories(id) ON DELETE CASCADE,
    CONSTRAINT fk_inventory_details_asset FOREIGN KEY (asset_id) REFERENCES assets(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS asset_liquidations (
    id CHAR(36) NOT NULL DEFAULT (UUID()),
    asset_id CHAR(36) NOT NULL,
    liquidation_date DATE NOT NULL,
    price DECIMAL(15,2) NULL,
    document_number VARCHAR(100) NULL,
    reason VARCHAR(1000) NOT NULL,
    notes VARCHAR(1000) NULL,
    prior_status VARCHAR(50) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_asset_liquidations_asset FOREIGN KEY (asset_id) REFERENCES assets(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS integration_channels (
    id CHAR(36) NOT NULL,
    provider_id VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'DISCONNECTED',
    email VARCHAR(255) NULL,
    app_password VARCHAR(255) NULL,
    client_id VARCHAR(255) NULL,
    client_secret VARCHAR(255) NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_integration_channels_provider_id (provider_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS integration_sync_logs (
    id CHAR(36) NOT NULL,
    account_name VARCHAR(255) NOT NULL,
    action VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    response_code INT NULL,
    execution_time INT NOT NULL,
    error_message VARCHAR(2000) NULL,
    created_at DATETIME NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



