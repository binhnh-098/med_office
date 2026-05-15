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

INSERT INTO cong_van_den (
    so_cong_van,
    so_den,
    tieu_de,
    noi_dung_tom_tat,
    don_vi_gui,
    nguoi_ky,
    ngay_van_ban,
    ngay_nhan,
    muc_do_khan,
    muc_do_mat,
    nguon_nhan,
    han_xu_ly,
    do_khan_xu_ly,
    so_trang,
    so_ban,
    trich_yeu,
    ghi_chu,
    trang_thai,
    ngay_tao,
    ngay_cap_nhat,
    da_doc,
    da_xu_ly,
    is_deleted,
    nguoi_tao_id
) VALUES (
    'CV-2026-001',
    'DEN-2026-001',
    N'Thong bao lich kiem tra dinh ky',
    N'So Y Te thong bao lich kiem tra dinh ky ve cong tac quan ly ho so va quy trinh tiep nhan nguoi benh.',
    N'So Y Te',
    N'Nguyen Van Minh',
    DATE '2026-05-01',
    DATE '2026-05-02',
    'BINH_THUONG',
    'THUONG',
    'EMAIL',
    DATE '2026-05-20',
    'BINH_THUONG',
    3,
    1,
    N'Kiem tra dinh ky cong tac quan ly ho so y te.',
    N'Chuyen phong hanh chinh tong hop theo doi.',
    'MOI_TIEP_NHAN',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    FALSE,
    FALSE,
    FALSE,
    2
);

INSERT INTO cong_van_den (
    so_cong_van,
    so_den,
    tieu_de,
    noi_dung_tom_tat,
    don_vi_gui,
    nguoi_ky,
    ngay_van_ban,
    ngay_nhan,
    muc_do_khan,
    muc_do_mat,
    nguon_nhan,
    han_xu_ly,
    do_khan_xu_ly,
    so_trang,
    so_ban,
    trich_yeu,
    y_kien_chi_dao,
    trang_thai,
    ngay_tao,
    ngay_cap_nhat,
    da_doc,
    da_xu_ly,
    is_deleted,
    nguoi_tao_id
) VALUES (
    'CV-2026-002',
    'DEN-2026-002',
    N'Yeu cau bao cao tinh hinh vat tu y te',
    N'De nghi don vi bao cao tinh hinh ton kho, su dung va nhu cau bo sung vat tu y te trong thang 5.',
    N'Phong Ke hoach Tong hop',
    N'Tran Thi Lan',
    DATE '2026-05-03',
    DATE '2026-05-04',
    'KHAN',
    'THUONG',
    'TRUC_TIEP',
    DATE '2026-05-10',
    'KHAN',
    2,
    1,
    N'Bao cao tinh hinh vat tu y te thang 5.',
    N'Uu tien tong hop va gui bao cao dung han.',
    'DANG_XU_LY',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    TRUE,
    FALSE,
    FALSE,
    2
);

INSERT INTO cong_van_den (
    so_cong_van,
    so_den,
    tieu_de,
    noi_dung_tom_tat,
    don_vi_gui,
    nguoi_ky,
    ngay_van_ban,
    ngay_nhan,
    muc_do_khan,
    muc_do_mat,
    nguon_nhan,
    han_xu_ly,
    do_khan_xu_ly,
    so_trang,
    so_ban,
    trich_yeu,
    ghi_chu,
    trang_thai,
    ngay_tao,
    ngay_cap_nhat,
    da_doc,
    da_xu_ly,
    is_deleted,
    nguoi_tao_id,
    nguoi_cap_nhat_id
) VALUES (
    'CV-2026-003',
    'DEN-2026-003',
    N'Huong dan trien khai quy trinh tiep nhan benh nhan',
    N'Huong dan cap nhat quy trinh tiep nhan, phan luong va luu tru ho so benh nhan tai bo phan le tan.',
    N'Ban Giam doc',
    N'Le Hoang Nam',
    DATE '2026-05-05',
    DATE '2026-05-06',
    'BINH_THUONG',
    'NOI_BO',
    'HE_THONG',
    DATE '2026-05-15',
    'BINH_THUONG',
    5,
    2,
    N'Trien khai quy trinh tiep nhan benh nhan moi.',
    N'Da cap nhat tai lieu huong dan cho bo phan lien quan.',
    'DA_XU_LY',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    TRUE,
    TRUE,
    FALSE,
    2,
    2
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
