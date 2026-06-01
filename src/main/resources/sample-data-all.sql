SET NAMES utf8mb4;

USE med_office;

-- Canonical local development reset seed.
-- Import with:
-- mysql --default-character-set=utf8mb4 -u med_office -p med_office --execute="SOURCE src/main/resources/sample-data-all.sql"
-- This file clears app data tables, then inserts the fixed UUID sample records below.

START TRANSACTION;

DELETE FROM user_roles;
DELETE FROM mon_an_trong_dang_ky_bua_an_bac_si;
DELETE FROM chi_tiet_dang_ky_bua_an_bac_si;
DELETE FROM dang_ky_bua_an_bac_si;
DELETE FROM mon_an_bac_si;
DELETE FROM lich_su_chat_rowboat;
DELETE FROM cong_van_di;
DELETE FROM cong_van_den;
DELETE FROM ho_so_nhan_vien;
DELETE FROM chuyen_khoa;
DELETE FROM nha_cung_cap;
DELETE FROM chuc_vu;
DELETE FROM nguoi_dung;

INSERT INTO chuc_vu (
    id,
    ma_chuc_vu,
    ten_chuc_vu,
    cap_bac,
    mo_ta,
    ngay_tao,
    ngay_cap_nhat
) VALUES
('11111111-1111-1111-1111-111111111111', 'GIAM_DOC', 'GiÃ¡m Ä‘á»‘c', 1, 'Quáº£n lÃ½ Ä‘iá»u hÃ nh phÃ²ng khÃ¡m', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('11111111-1111-1111-1111-111111111112', 'TRUONG_KHOA', 'TrÆ°á»Ÿng khoa', 2, 'Phá»¥ trÃ¡ch chuyÃªn mÃ´n theo khoa', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('11111111-1111-1111-1111-111111111113', 'BAC_SI', 'BÃ¡c sÄ©', 3, 'KhÃ¡m vÃ  Ä‘iá»u trá»‹ bá»‡nh nhÃ¢n', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('11111111-1111-1111-1111-111111111114', 'DIEU_DUONG', 'Äiá»u dÆ°á»¡ng', 4, 'Há»— trá»£ chÄƒm sÃ³c ngÆ°á»i bá»‡nh', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('11111111-1111-1111-1111-111111111115', 'LE_TAN', 'Lá»… tÃ¢n', 5, 'Tiáº¿p nháº­n vÃ  hÆ°á»›ng dáº«n bá»‡nh nhÃ¢n', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO nguoi_dung (
    id,
    ten_dang_nhap,
    mat_khau_ma_hoa,
    phong_ban_id,
    chuc_vu_id,
    trang_thai,
    lan_dang_nhap_cuoi,
    ngay_tao,
    ngay_cap_nhat
) VALUES
('22222222-2222-2222-2222-222222222221', 'admin', '{bcrypt}$2a$10$EGUG4jSi3brf/Ww73Nw.Euhk/OrlDCer8VH0otQCQXSPNdun9ig8i', NULL, '11111111-1111-1111-1111-111111111111', 'ACTIVE', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('22222222-2222-2222-2222-222222222222', 'doctor1', '{bcrypt}$2a$10$EGUG4jSi3brf/Ww73Nw.Euhk/OrlDCer8VH0otQCQXSPNdun9ig8i', NULL, '11111111-1111-1111-1111-111111111113', 'ACTIVE', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('22222222-2222-2222-2222-222222222223', 'doctor2', '{bcrypt}$2a$10$EGUG4jSi3brf/Ww73Nw.Euhk/OrlDCer8VH0otQCQXSPNdun9ig8i', NULL, '11111111-1111-1111-1111-111111111113', 'ACTIVE', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('22222222-2222-2222-2222-222222222224', 'nurse1', '{bcrypt}$2a$10$EGUG4jSi3brf/Ww73Nw.Euhk/OrlDCer8VH0otQCQXSPNdun9ig8i', NULL, '11111111-1111-1111-1111-111111111114', 'ACTIVE', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('22222222-2222-2222-2222-222222222225', 'reception', '{bcrypt}$2a$10$EGUG4jSi3brf/Ww73Nw.Euhk/OrlDCer8VH0otQCQXSPNdun9ig8i', NULL, '11111111-1111-1111-1111-111111111115', 'ACTIVE', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT IGNORE INTO user_roles (user_id, role_id, created_at)
SELECT nd.id, r.id, CURRENT_TIMESTAMP
FROM nguoi_dung nd
JOIN chuc_vu cv ON cv.id = nd.chuc_vu_id
JOIN roles r ON r.code = CASE cv.ma_chuc_vu
    WHEN 'GIAM_DOC' THEN 'ADMIN'
    WHEN 'TRUONG_KHOA' THEN 'NHAN_SU'
    WHEN 'LE_TAN' THEN 'VAN_THU'
    ELSE cv.ma_chuc_vu
END
WHERE nd.chuc_vu_id IS NOT NULL;

INSERT INTO chuyen_khoa (
    id_chuyen_khoa,
    ten_chuyen_khoa,
    nguoi_dung_id
) VALUES
('33333333-3333-3333-3333-333333333331', 'Ná»™i tá»•ng quÃ¡t', '22222222-2222-2222-2222-222222222221'),
('33333333-3333-3333-3333-333333333332', 'Nhi khoa', '22222222-2222-2222-2222-222222222221'),
('33333333-3333-3333-3333-333333333333', 'Tai mÅ©i há»ng', '22222222-2222-2222-2222-222222222221'),
('33333333-3333-3333-3333-333333333334', 'Da liá»…u', '22222222-2222-2222-2222-222222222221'),
('33333333-3333-3333-3333-333333333335', 'Sáº£n phá»¥ khoa', '22222222-2222-2222-2222-222222222221');

INSERT INTO nha_cung_cap (
    id,
    ma_nha_cung_cap,
    ten_nha_cung_cap,
    trang_thai,
    ngay_tao
) VALUES
('44444444-4444-4444-4444-444444444441', 'NCC001', 'CÃ´ng ty DÆ°á»£c pháº©m An TÃ¢m', 'ACTIVE', CURRENT_TIMESTAMP),
('44444444-4444-4444-4444-444444444442', 'NCC002', 'Thiáº¿t bá»‹ Y táº¿ Minh Khang', 'ACTIVE', CURRENT_TIMESTAMP),
('44444444-4444-4444-4444-444444444443', 'NCC003', 'Suáº¥t Äƒn Bá»‡nh viá»‡n Hoa Sen', 'ACTIVE', CURRENT_TIMESTAMP);

INSERT INTO ho_so_nhan_vien (
    id,
    nguoi_dung_id,
    ma_nhan_vien,
    ten_nhan_vien,
    ngay_sinh,
    gioi_tinh,
    so_dinh_danh,
    so_bao_hiem_xa_hoi,
    thu_dien_tu,
    so_dien_thoai,
    bang_cap,
    chuyen_khoa,
    hoc_ham,
    ten_hoc_ham,
    chung_chi,
    ma_chuc_vu,
    danh_hieu,
    ma_pin_ky,
    tai_khoan_ky,
    otp_ky,
    mat_khau_hoa_don,
    anh_dai_dien,
    anh_chu_ky,
    khoa_tu_ngay,
    khoa_den_ngay,
    tai_khoan_ke_don,
    mat_khau_ke_don,
    dat_lich_truc_tuyen,
    dang_hoat_dong,
    ghi_chu,
    ngay_tao,
    ngay_cap_nhat
) VALUES
('55555555-5555-5555-5555-555555555551', '22222222-2222-2222-2222-222222222221', 'NV0001', 'Nguyá»…n VÄƒn Quáº£n', '1980-02-10', 1, '079080000001', 'BHXH000001', 'admin@medoffice.local', '0901000001', 'BS.CKII', 'Ná»™i tá»•ng quÃ¡t', 'CKII', 'ChuyÃªn khoa II', '001001/BYT-CCHN', 'GIAM_DOC', 'Tháº§y thuá»‘c Æ°u tÃº', '', '', '', '', '', '', NULL, NULL, 'admin_rx', 'clinic123', b'0', b'1', 'TÃ i khoáº£n quáº£n trá»‹ máº«u', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('55555555-5555-5555-5555-555555555552', '22222222-2222-2222-2222-222222222222', 'NV0002', 'Tráº§n Thá»‹ An', '1988-06-15', 2, '079088000002', 'BHXH000002', 'doctor1@medoffice.local', '0901000002', 'ThS.BS', 'Nhi khoa', 'THS', 'Tháº¡c sÄ©', '001002/BYT-CCHN', 'BAC_SI', '', '', '', '', '', '', '', NULL, NULL, 'doctor1_rx', 'clinic123', b'1', b'1', 'BÃ¡c sÄ© khÃ¡m nhi', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('55555555-5555-5555-5555-555555555553', '22222222-2222-2222-2222-222222222223', 'NV0003', 'LÃª Minh BÃ¬nh', '1985-11-03', 1, '079085000003', 'BHXH000003', 'doctor2@medoffice.local', '0901000003', 'BS.CKI', 'Tai mÅ©i há»ng', 'CKI', 'ChuyÃªn khoa I', '001003/BYT-CCHN', 'BAC_SI', '', '', '', '', '', '', '', NULL, NULL, 'doctor2_rx', 'clinic123', b'1', b'1', 'BÃ¡c sÄ© tai mÅ©i há»ng', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('55555555-5555-5555-5555-555555555554', '22222222-2222-2222-2222-222222222224', 'NV0004', 'Pháº¡m Thu CÃºc', '1992-04-22', 2, '079092000004', 'BHXH000004', 'nurse1@medoffice.local', '0901000004', 'CNÄD', 'Äiá»u dÆ°á»¡ng', '', '', '001004/BYT-CCHN', 'DIEU_DUONG', '', '', '', '', '', '', '', NULL, NULL, '', '', b'0', b'1', 'Äiá»u dÆ°á»¡ng phÃ²ng khÃ¡m', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('55555555-5555-5555-5555-555555555555', '22222222-2222-2222-2222-222222222225', 'NV0005', 'HoÃ ng Háº£i DÆ°Æ¡ng', '1994-09-09', 1, '079094000005', 'BHXH000005', 'reception@medoffice.local', '0901000005', 'CÄ', 'HÃ nh chÃ­nh', '', '', '', 'LE_TAN', '', '', '', '', '', '', '', NULL, NULL, '', '', b'0', b'1', 'Lá»… tÃ¢n tiáº¿p nháº­n', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO cong_van_den (
    id,
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
    y_kien_chi_dao,
    tep_dinh_kem_chinh,
    trang_thai,
    ngay_tao,
    ngay_cap_nhat,
    da_doc,
    da_xu_ly,
    da_xoa,
    nguoi_tao_id,
    nguoi_cap_nhat_id
) VALUES
('66666666-6666-6666-6666-666666666661', 'CV-UBND-001/2026', 'DEN-001', 'Triá»ƒn khai kiá»ƒm tra an toÃ n phÃ²ng khÃ¡m', 'YÃªu cáº§u rÃ  soÃ¡t há»“ sÆ¡ phÃ¡p lÃ½ vÃ  quy trÃ¬nh an toÃ n.', 'UBND Quáº­n', 'Nguyá»…n VÄƒn A', '2026-05-01', '2026-05-02', 'BINH_THUONG', 'THUONG', 'EMAIL', '2026-05-15', 'BINH_THUONG', 3, 1, 'RÃ  soÃ¡t Ä‘iá»u kiá»‡n hoáº¡t Ä‘á»™ng phÃ²ng khÃ¡m', '', 'Giao bá»™ pháº­n hÃ nh chÃ­nh tá»•ng há»£p bÃ¡o cÃ¡o', '', 'MOI_TIEP_NHAN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, b'0', b'0', b'0', '22222222-2222-2222-2222-222222222225', '22222222-2222-2222-2222-222222222225'),
('66666666-6666-6666-6666-666666666662', 'CV-SYT-015/2026', 'DEN-002', 'Cáº­p nháº­t hÆ°á»›ng dáº«n bÃ¡o cÃ¡o bá»‡nh truyá»n nhiá»…m', 'Sá»Ÿ Y táº¿ yÃªu cáº§u cáº­p nháº­t biá»ƒu máº«u bÃ¡o cÃ¡o Ä‘á»‹nh ká»³.', 'Sá»Ÿ Y táº¿', 'Tráº§n Thá»‹ B', '2026-05-06', '2026-05-06', 'KHAN', 'THUONG', 'VAN_THU', '2026-05-10', 'KHAN', 5, 1, 'Cáº­p nháº­t quy trÃ¬nh bÃ¡o cÃ¡o bá»‡nh truyá»n nhiá»…m', '', 'BÃ¡c sÄ© phá»¥ trÃ¡ch chuyÃªn mÃ´n xá»­ lÃ½', '', 'DANG_XU_LY', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, b'1', b'0', b'0', '22222222-2222-2222-2222-222222222225', '22222222-2222-2222-2222-222222222221'),
('66666666-6666-6666-6666-666666666663', 'CV-BHXH-021/2026', 'DEN-003', 'Äá»‘i soÃ¡t dá»¯ liá»‡u báº£o hiá»ƒm y táº¿ thÃ¡ng 05', 'Äá» nghá»‹ Ä‘á»‘i soÃ¡t dá»¯ liá»‡u khÃ¡m chá»¯a bá»‡nh báº£o hiá»ƒm y táº¿.', 'BHXH ThÃ nh phá»‘', 'LÃª VÄƒn C', '2026-05-12', '2026-05-13', 'BINH_THUONG', 'THUONG', 'EMAIL', '2026-05-25', 'BINH_THUONG', 4, 1, 'Äá»‘i soÃ¡t dá»¯ liá»‡u BHYT thÃ¡ng 05/2026', '', '', '', 'HOAN_THANH', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, b'1', b'1', b'0', '22222222-2222-2222-2222-222222222225', '22222222-2222-2222-2222-222222222221');

INSERT INTO cong_van_di (
    id,
    so_cong_van,
    tieu_de,
    noi_dung_tom_tat,
    don_vi_nhan,
    ngay_ban_hanh,
    nguoi_ky_id,
    trang_thai,
    ngay_tao,
    ngay_cap_nhat
) VALUES
('77777777-7777-7777-7777-777777777771', 'DI-001/2026', 'BÃ¡o cÃ¡o rÃ  soÃ¡t Ä‘iá»u kiá»‡n hoáº¡t Ä‘á»™ng', 'BÃ¡o cÃ¡o káº¿t quáº£ rÃ  soÃ¡t Ä‘iá»u kiá»‡n hoáº¡t Ä‘á»™ng phÃ²ng khÃ¡m.', 'UBND Quáº­n', '2026-05-14', '22222222-2222-2222-2222-222222222221', 'DA_BAN_HANH', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('77777777-7777-7777-7777-777777777772', 'DI-002/2026', 'BÃ¡o cÃ¡o bá»‡nh truyá»n nhiá»…m tuáº§n 20', 'Tá»•ng há»£p sá»‘ liá»‡u bÃ¡o cÃ¡o bá»‡nh truyá»n nhiá»…m tuáº§n 20.', 'Sá»Ÿ Y táº¿', '2026-05-18', '22222222-2222-2222-2222-222222222221', 'DA_BAN_HANH', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('77777777-7777-7777-7777-777777777773', 'DI-003/2026', 'CÃ´ng vÄƒn Ä‘á»‘i soÃ¡t dá»¯ liá»‡u BHYT', 'Gá»­i dá»¯ liá»‡u Ä‘á»‘i soÃ¡t khÃ¡m chá»¯a bá»‡nh báº£o hiá»ƒm y táº¿ thÃ¡ng 05.', 'BHXH ThÃ nh phá»‘', '2026-05-26', '22222222-2222-2222-2222-222222222221', 'DU_THAO', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

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
) VALUES
('88888888-8888-8888-8888-888888888881', 'reception', 'TÃ³m táº¯t cÃ´ng vÄƒn Ä‘áº¿n DEN-001', 'CÃ´ng vÄƒn yÃªu cáº§u rÃ  soÃ¡t há»“ sÆ¡ phÃ¡p lÃ½ vÃ  quy trÃ¬nh an toÃ n phÃ²ng khÃ¡m.', NULL, NULL, 'SUCCESS', NULL, CURRENT_TIMESTAMP),
('88888888-8888-8888-8888-888888888882', 'admin', 'Liá»‡t kÃª cÃ¡c viá»‡c cáº§n lÃ m hÃ´m nay', 'CÃ³ 2 viá»‡c cáº§n Æ°u tiÃªn: xá»­ lÃ½ bÃ¡o cÃ¡o Sá»Ÿ Y táº¿ vÃ  Ä‘á»‘i soÃ¡t dá»¯ liá»‡u BHYT.', NULL, NULL, 'SUCCESS', NULL, CURRENT_TIMESTAMP);

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
) VALUES
('99999999-9999-9999-9999-999999999901', 2026, 22, 'MONDAY', '2026-05-25', 'lunch', 'Bá»¯a trÆ°a', 'CÆ¡m gÃ  xá»‘i má»¡', 45000, 45000, 720, '11:30', 'KÃ¨m canh rau', 'admin', CURRENT_TIMESTAMP),
('99999999-9999-9999-9999-999999999902', 2026, 22, 'MONDAY', '2026-05-25', 'dinner', 'Bá»¯a tá»‘i', 'BÃºn bÃ² Huáº¿', 50000, 50000, 680, '17:30', '', 'admin', CURRENT_TIMESTAMP),
('99999999-9999-9999-9999-999999999903', 2026, 22, 'TUESDAY', '2026-05-26', 'lunch', 'Bá»¯a trÆ°a', 'CÆ¡m sÆ°á»n bÃ¬ cháº£', 48000, 48000, 760, '11:30', '', 'admin', CURRENT_TIMESTAMP),
('99999999-9999-9999-9999-999999999904', 2026, 22, 'WEDNESDAY', '2026-05-27', 'lunch', 'Bá»¯a trÆ°a', 'Phá»Ÿ bÃ² tÃ¡i náº¡m', 52000, 52000, 640, '11:30', '', 'admin', CURRENT_TIMESTAMP),
('99999999-9999-9999-9999-999999999905', 2026, 22, 'THURSDAY', '2026-05-28', 'lunch', 'Bá»¯a trÆ°a', 'MÃ¬ Quáº£ng gÃ ', 47000, 47000, 700, '11:30', '', 'admin', CURRENT_TIMESTAMP);

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
) VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', 2026, 22, 'Tuáº§n 22/2026', '2026-05-25', '2026-05-31', 'doctor1', 'doctor1', 'Tráº§n Thá»‹ An', 'Nhi khoa', 'BÃ¡c sÄ©', 2, 93000, '{"week":{"year":2026,"number":22,"label":"Tuáº§n 22/2026"},"requester":{"username":"doctor1","name":"Tráº§n Thá»‹ An","department":"Nhi khoa","role":"BÃ¡c sÄ©"},"summary":{"totalQuantity":2,"totalAmount":93000}}', CURRENT_TIMESTAMP),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2', 2026, 22, 'Tuáº§n 22/2026', '2026-05-25', '2026-05-31', 'doctor2', 'doctor2', 'LÃª Minh BÃ¬nh', 'Tai mÅ©i há»ng', 'BÃ¡c sÄ©', 1, 52000, '{"week":{"year":2026,"number":22,"label":"Tuáº§n 22/2026"},"requester":{"username":"doctor2","name":"LÃª Minh BÃ¬nh","department":"Tai mÅ©i há»ng","role":"BÃ¡c sÄ©"},"summary":{"totalQuantity":1,"totalAmount":52000}}', CURRENT_TIMESTAMP);

INSERT INTO chi_tiet_dang_ky_bua_an_bac_si (
    id,
    dang_ky_id,
    ngay_an,
    thu_trong_tuan,
    ma_bua_an,
    ten_bua_an,
    so_luong_bua_an,
    thanh_tien_bua_an
) VALUES
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb1', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', '2026-05-25', 'MONDAY', 'lunch', 'Bá»¯a trÆ°a', 1, 45000),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb2', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', '2026-05-26', 'TUESDAY', 'lunch', 'Bá»¯a trÆ°a', 1, 48000),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb3', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2', '2026-05-27', 'WEDNESDAY', 'lunch', 'Bá»¯a trÆ°a', 1, 52000);

INSERT INTO mon_an_trong_dang_ky_bua_an_bac_si (
    id,
    chi_tiet_dang_ky_id,
    ten_mon_an,
    gio_phuc_vu,
    so_luong,
    don_gia,
    thanh_tien
) VALUES
('cccccccc-cccc-cccc-cccc-ccccccccccc1', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb1', 'CÆ¡m gÃ  xá»‘i má»¡', '11:30', 1, 45000, 45000),
('cccccccc-cccc-cccc-cccc-ccccccccccc2', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb2', 'CÆ¡m sÆ°á»n bÃ¬ cháº£', '11:30', 1, 48000, 48000),
('cccccccc-cccc-cccc-cccc-ccccccccccc3', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb3', 'Phá»Ÿ bÃ² tÃ¡i náº¡m', '11:30', 1, 52000, 52000);

COMMIT;


