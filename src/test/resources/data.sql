INSERT INTO chuc_vu (id, ma_chuc_vu, ten_chuc_vu, cap_bac, mo_ta, ngay_tao, ngay_cap_nhat)
VALUES (1, 'LT', N'Le tan', 4, N'Le tan', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO chuc_vu (id, ma_chuc_vu, ten_chuc_vu, cap_bac, mo_ta, ngay_tao, ngay_cap_nhat)
VALUES (2, 'QL', N'Quan tri he thong', 2, N'Quan tri he thong', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO chuc_vu (id, ma_chuc_vu, ten_chuc_vu, cap_bac, mo_ta, ngay_tao, ngay_cap_nhat)
VALUES (3, 'BS', N'Bac si', 3, N'Bac si', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO chuc_vu (id, ma_chuc_vu, ten_chuc_vu, cap_bac, mo_ta, ngay_tao, ngay_cap_nhat)
VALUES (4, 'DD', N'Dieu duong', 4, N'Dieu duong', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO nguoi_dung (
    id,
    ten_dang_nhap,
    mat_khau_ma_hoa,
    ho_ten,
    email,
    so_dien_thoai,
    phong_ban_id,
    chuc_vu_id,
    trang_thai,
    lan_dang_nhap_cuoi,
    ngay_tao,
    ngay_cap_nhat
) VALUES (
    1,
    'reception',
    '{bcrypt}$2a$10$PyiMc9O5vHJ1FRK6dL/TxObB1S0KVmN1J13/TFM2Cb4KNSWOd9qnK',
    N'Reception Staff',
    'reception@med-office.local',
    '0901234567',
    1,
    1,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO nha_cung_cap (
    id,
    ma_nha_cung_cap,
    ten_nha_cung_cap,
    trang_thai,
    ngay_tao
) VALUES (
    1,
    'DVG01',
    N'So Y Te',
    'ACTIVE',
    CURRENT_TIMESTAMP
);

INSERT INTO nguoi_dung (
    id,
    ten_dang_nhap,
    mat_khau_ma_hoa,
    ho_ten,
    email,
    so_dien_thoai,
    phong_ban_id,
    chuc_vu_id,
    trang_thai,
    lan_dang_nhap_cuoi,
    ngay_tao,
    ngay_cap_nhat
) VALUES (
    2,
    'admin',
    '{bcrypt}$2a$10$PyiMc9O5vHJ1FRK6dL/TxObB1S0KVmN1J13/TFM2Cb4KNSWOd9qnK',
    N'System Administrator',
    'admin@med-office.local',
    '0901234568',
    1,
    2,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
