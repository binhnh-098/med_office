USE med_office;

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
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

INSERT INTO ho_so_nhan_vien (
    nguoi_dung_id,
    ma_nhan_vien,
    ten_nhan_vien,
    thu_dien_tu,
    so_dien_thoai,
    ma_chuc_vu,
    dat_lich_truc_tuyen,
    dang_hoat_dong,
    ghi_chu,
    ngay_tao,
    ngay_cap_nhat
)
SELECT
    nd.id AS nguoi_dung_id,
    CONCAT('NV', UPPER(REPLACE(LEFT(nd.id, 8), '-', ''))) AS ma_nhan_vien,
    nd.ten_dang_nhap AS ten_nhan_vien,
    NULL AS thu_dien_tu,
    NULL AS so_dien_thoai,
    cv.ma_chuc_vu AS ma_chuc_vu,
    b'0' AS dat_lich_truc_tuyen,
    CASE WHEN UPPER(COALESCE(nd.trang_thai, '')) = 'ACTIVE' THEN b'1' ELSE b'0' END AS dang_hoat_dong,
    CONCAT('Dong bo tu nguoi_dung: ', nd.ten_dang_nhap) AS ghi_chu,
    COALESCE(nd.ngay_tao, CURRENT_TIMESTAMP) AS ngay_tao,
    nd.ngay_cap_nhat AS ngay_cap_nhat
FROM nguoi_dung nd
LEFT JOIN chuc_vu cv ON cv.id = nd.chuc_vu_id
LEFT JOIN ho_so_nhan_vien hsnv ON hsnv.nguoi_dung_id = nd.id
WHERE hsnv.id IS NULL;

SELECT id, nguoi_dung_id, ma_nhan_vien, ten_nhan_vien, thu_dien_tu, so_dien_thoai, ma_chuc_vu, dang_hoat_dong
FROM ho_so_nhan_vien
ORDER BY id;

