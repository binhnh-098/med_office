USE med_office;

CREATE TABLE IF NOT EXISTS ho_so_nhan_vien (
    id CHAR(36) NOT NULL DEFAULT (UUID()),
    nguoi_dung_id CHAR(36) NULL,
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
    academic_title VARCHAR(100) NULL,
    academic_title_name VARCHAR(255) NULL,
    certificate VARCHAR(100) NULL,
    position_code VARCHAR(100) NULL,
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

INSERT INTO ho_so_nhan_vien (
    nguoi_dung_id,
    code,
    name,
    email,
    phone,
    position_code,
    online_booking,
    active,
    note,
    created_at,
    updated_at
)
SELECT
    nd.id AS nguoi_dung_id,
    CONCAT('NV', UPPER(REPLACE(LEFT(nd.id, 8), '-', ''))) AS code,
    nd.ho_ten AS name,
    nd.email,
    nd.so_dien_thoai AS phone,
    cv.ma_chuc_vu AS position_code,
    b'0' AS online_booking,
    CASE WHEN UPPER(COALESCE(nd.trang_thai, '')) = 'ACTIVE' THEN b'1' ELSE b'0' END AS active,
    CONCAT('Dong bo tu nguoi_dung: ', nd.ten_dang_nhap) AS note,
    COALESCE(nd.ngay_tao, CURRENT_TIMESTAMP) AS created_at,
    nd.ngay_cap_nhat AS updated_at
FROM nguoi_dung nd
LEFT JOIN chuc_vu cv ON cv.id = nd.chuc_vu_id
LEFT JOIN ho_so_nhan_vien hsnv ON hsnv.nguoi_dung_id = nd.id
WHERE hsnv.id IS NULL;

SELECT id, nguoi_dung_id, code, name, email, phone, position_code, active
FROM ho_so_nhan_vien
ORDER BY id;
