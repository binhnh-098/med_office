USE med_office;

SET FOREIGN_KEY_CHECKS = 0;

UPDATE chuyen_khoa
SET nguoi_dung_id = COALESCE(nguoi_dung_id, user_id)
WHERE user_id IS NOT NULL;

UPDATE chuyen_khoa
SET nguoi_dung_id = '22222222-2222-2222-2222-222222222221'
WHERE nguoi_dung_id IS NULL OR nguoi_dung_id = '';

UPDATE nguoi_dung
SET thu_dien_tu = COALESCE(NULLIF(thu_dien_tu, ''), email)
WHERE email IS NOT NULL;

UPDATE cong_van_den
SET da_xoa = COALESCE(da_xoa, is_deleted)
WHERE is_deleted IS NOT NULL;

ALTER TABLE ho_so_nhan_vien
    ADD COLUMN ngay_tao DATETIME NULL;

UPDATE ho_so_nhan_vien
SET
    ma_nhan_vien = COALESCE(NULLIF(ma_nhan_vien, ''), code),
    ten_nhan_vien = COALESCE(NULLIF(ten_nhan_vien, ''), name),
    ngay_sinh = COALESCE(ngay_sinh, birth_date),
    gioi_tinh = COALESCE(gioi_tinh, gender),
    so_dinh_danh = COALESCE(NULLIF(so_dinh_danh, ''), identity_number),
    so_bao_hiem_xa_hoi = COALESCE(NULLIF(so_bao_hiem_xa_hoi, ''), social_insurance),
    thu_dien_tu = COALESCE(NULLIF(thu_dien_tu, ''), email),
    so_dien_thoai = COALESCE(NULLIF(so_dien_thoai, ''), phone),
    bang_cap = COALESCE(NULLIF(bang_cap, ''), degree),
    chuyen_khoa = COALESCE(NULLIF(chuyen_khoa, ''), specialty),
    hoc_ham = COALESCE(NULLIF(hoc_ham, ''), academic_title),
    ten_hoc_ham = COALESCE(NULLIF(ten_hoc_ham, ''), academic_title_name),
    chung_chi = COALESCE(NULLIF(chung_chi, ''), certificate),
    ma_chuc_vu = COALESCE(NULLIF(ma_chuc_vu, ''), position_code),
    danh_hieu = COALESCE(NULLIF(danh_hieu, ''), honor_title),
    ma_pin_ky = COALESCE(NULLIF(ma_pin_ky, ''), signing_pin),
    tai_khoan_ky = COALESCE(NULLIF(tai_khoan_ky, ''), signing_account),
    otp_ky = COALESCE(NULLIF(otp_ky, ''), signing_otp),
    mat_khau_hoa_don = COALESCE(NULLIF(mat_khau_hoa_don, ''), invoice_password),
    anh_dai_dien = COALESCE(NULLIF(anh_dai_dien, ''), avatar_image),
    anh_chu_ky = COALESCE(NULLIF(anh_chu_ky, ''), signature_image),
    khoa_tu_ngay = COALESCE(khoa_tu_ngay, locked_from),
    khoa_den_ngay = COALESCE(khoa_den_ngay, locked_to),
    tai_khoan_ke_don = COALESCE(NULLIF(tai_khoan_ke_don, ''), prescription_account),
    mat_khau_ke_don = COALESCE(NULLIF(mat_khau_ke_don, ''), prescription_password),
    dat_lich_truc_tuyen = COALESCE(dat_lich_truc_tuyen, online_booking),
    dang_hoat_dong = COALESCE(dang_hoat_dong, active),
    ghi_chu = COALESCE(NULLIF(ghi_chu, ''), note),
    ngay_tao = COALESCE(ngay_tao, created_at, CURRENT_TIMESTAMP),
    ngay_cap_nhat = COALESCE(ngay_cap_nhat, updated_at);

INSERT INTO lich_su_chat_rowboat (
    id,
    ten_dang_nhap,
    tin_nhan_nguoi_dung,
    tin_nhan_tro_ly,
    trang_thai_yeu_cau_json,
    trang_thai_phan_hoi_json,
    trang_thai,
    thong_bao_loi,
    ngay_tao
)
SELECT
    id,
    username,
    user_message,
    assistant_message,
    request_state_json,
    response_state_json,
    status,
    error_message,
    created_at
FROM rowboat_chat_histories old_row
WHERE NOT EXISTS (
    SELECT 1 FROM lich_su_chat_rowboat new_row WHERE new_row.id = old_row.id
);

INSERT INTO dang_ky_bua_an_bac_si (
    id,
    nam_tuan,
    so_tuan,
    nhan_tuan,
    ngay_bat_dau_tuan,
    ngay_ket_thuc_tuan,
    ten_dang_nhap,
    ten_dang_nhap_nguoi_dang_ky,
    ho_ten_nguoi_dang_ky,
    phong_ban_nguoi_dang_ky,
    vai_tro_nguoi_dang_ky,
    tong_so_luong,
    tong_tien,
    du_lieu_json,
    ngay_tao
)
SELECT
    id,
    week_year,
    week_number,
    week_label,
    week_start_date,
    week_end_date,
    username,
    requester_username,
    requester_full_name,
    requester_department,
    requester_role,
    total_quantity,
    total_amount,
    payload_json,
    created_at
FROM doctor_meal_registrations old_row
WHERE NOT EXISTS (
    SELECT 1 FROM dang_ky_bua_an_bac_si new_row WHERE new_row.id = old_row.id
);

INSERT INTO chi_tiet_dang_ky_bua_an_bac_si (
    id,
    dang_ky_id,
    ngay_an,
    thu_trong_tuan,
    ma_bua_an,
    ten_bua_an,
    so_luong_bua_an,
    thanh_tien_bua_an
)
SELECT
    id,
    registration_id,
    meal_date,
    day_of_week,
    meal_id,
    meal_label,
    meal_quantity,
    meal_amount
FROM doctor_meal_registration_items old_row
WHERE NOT EXISTS (
    SELECT 1 FROM chi_tiet_dang_ky_bua_an_bac_si new_row WHERE new_row.id = old_row.id
);

INSERT INTO mon_an_trong_dang_ky_bua_an_bac_si (
    id,
    chi_tiet_dang_ky_id,
    ten_mon_an,
    gio_phuc_vu,
    so_luong,
    don_gia,
    thanh_tien
)
SELECT
    id,
    registration_item_id,
    name,
    serving_time,
    quantity,
    unit_price,
    line_total
FROM doctor_meal_registration_item_snapshots old_row
WHERE NOT EXISTS (
    SELECT 1 FROM mon_an_trong_dang_ky_bua_an_bac_si new_row WHERE new_row.id = old_row.id
);

INSERT INTO mon_an_bac_si (
    id,
    nam_tuan,
    so_tuan,
    thu_trong_tuan,
    ngay_an,
    ma_bua_an,
    ten_bua_an,
    ten_mon_an,
    gia,
    don_gia,
    calo,
    gio_phuc_vu,
    ghi_chu,
    nguoi_tao,
    ngay_tao
)
SELECT
    id,
    week_year,
    week_number,
    day_of_week,
    meal_date,
    meal_id,
    meal_label,
    name,
    price,
    unit_price,
    calories,
    serving_time,
    note,
    created_by,
    created_at
FROM doctor_meal_dishes old_row
WHERE NOT EXISTS (
    SELECT 1 FROM mon_an_bac_si new_row WHERE new_row.id = old_row.id
);

DROP TABLE IF EXISTS doctor_meal_registration_item_snapshots;
DROP TABLE IF EXISTS doctor_meal_registration_items;
DROP TABLE IF EXISTS doctor_meal_registrations;
DROP TABLE IF EXISTS doctor_meal_dishes;
DROP TABLE IF EXISTS rowboat_chat_histories;

ALTER TABLE chuc_vu
    DROP COLUMN user_id;

ALTER TABLE chuyen_khoa
    DROP COLUMN user_id;

ALTER TABLE nguoi_dung
    DROP COLUMN email;

ALTER TABLE cong_van_den
    DROP COLUMN is_deleted;

ALTER TABLE ho_so_nhan_vien
    DROP COLUMN academic_title,
    DROP COLUMN academic_title_name,
    DROP COLUMN active,
    DROP COLUMN avatar_image,
    DROP COLUMN birth_date,
    DROP COLUMN certificate,
    DROP COLUMN code,
    DROP COLUMN created_at,
    DROP COLUMN degree,
    DROP COLUMN email,
    DROP COLUMN gender,
    DROP COLUMN honor_title,
    DROP COLUMN identity_number,
    DROP COLUMN invoice_password,
    DROP COLUMN locked_from,
    DROP COLUMN locked_to,
    DROP COLUMN name,
    DROP COLUMN note,
    DROP COLUMN online_booking,
    DROP COLUMN phone,
    DROP COLUMN position_code,
    DROP COLUMN prescription_account,
    DROP COLUMN prescription_password,
    DROP COLUMN signature_image,
    DROP COLUMN signing_account,
    DROP COLUMN signing_otp,
    DROP COLUMN signing_pin,
    DROP COLUMN social_insurance,
    DROP COLUMN specialty,
    DROP COLUMN updated_at;

ALTER TABLE ho_so_nhan_vien
    MODIFY COLUMN ma_nhan_vien VARCHAR(50) NOT NULL,
    MODIFY COLUMN ten_nhan_vien VARCHAR(255) NOT NULL,
    MODIFY COLUMN ngay_tao DATETIME NOT NULL,
    MODIFY COLUMN dat_lich_truc_tuyen BIT(1) NOT NULL DEFAULT b'0',
    MODIFY COLUMN dang_hoat_dong BIT(1) NOT NULL DEFAULT b'1';

ALTER TABLE ho_so_nhan_vien
    ADD UNIQUE KEY uk_ho_so_nhan_vien_ma_nhan_vien (ma_nhan_vien),
    ADD KEY idx_ho_so_nhan_vien_ten_nhan_vien (ten_nhan_vien),
    ADD KEY idx_ho_so_nhan_vien_dang_hoat_dong (dang_hoat_dong),
    ADD KEY idx_ho_so_nhan_vien_chuyen_khoa (chuyen_khoa);

SET FOREIGN_KEY_CHECKS = 1;
