SET NAMES utf8mb4;

USE med_office;

-- Canonical local development reset seed.
-- Import with:
-- mysql --default-character-set=utf8mb4 -u med_office -p med_office --execute="SOURCE src/main/resources/sample-data-all.sql"
-- This file clears app data tables, then inserts the fixed UUID sample records below.

START TRANSACTION;

DELETE FROM doctor_meal_registration_item_snapshots;
DELETE FROM doctor_meal_registration_items;
DELETE FROM doctor_meal_registrations;
DELETE FROM doctor_meal_dishes;
DELETE FROM rowboat_chat_histories;
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
    user_id,
    cap_bac,
    mo_ta,
    ngay_tao,
    ngay_cap_nhat
) VALUES
('11111111-1111-1111-1111-111111111111', 'GIAM_DOC', 'Giám đốc', NULL, 1, 'Quản lý điều hành phòng khám', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('11111111-1111-1111-1111-111111111112', 'TRUONG_KHOA', 'Trưởng khoa', NULL, 2, 'Phụ trách chuyên môn theo khoa', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('11111111-1111-1111-1111-111111111113', 'BAC_SI', 'Bác sĩ', NULL, 3, 'Khám và điều trị bệnh nhân', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('11111111-1111-1111-1111-111111111114', 'DIEU_DUONG', 'Điều dưỡng', NULL, 4, 'Hỗ trợ chăm sóc người bệnh', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('11111111-1111-1111-1111-111111111115', 'LE_TAN', 'Lễ tân', NULL, 5, 'Tiếp nhận và hướng dẫn bệnh nhân', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

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
) VALUES
('22222222-2222-2222-2222-222222222221', 'admin', '{bcrypt}$2a$10$EGUG4jSi3brf/Ww73Nw.Euhk/OrlDCer8VH0otQCQXSPNdun9ig8i', 'Nguyễn Văn Quản', 'admin@medoffice.local', '0901000001', NULL, '11111111-1111-1111-1111-111111111111', 'ACTIVE', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('22222222-2222-2222-2222-222222222222', 'doctor1', '{bcrypt}$2a$10$EGUG4jSi3brf/Ww73Nw.Euhk/OrlDCer8VH0otQCQXSPNdun9ig8i', 'Trần Thị An', 'doctor1@medoffice.local', '0901000002', NULL, '11111111-1111-1111-1111-111111111113', 'ACTIVE', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('22222222-2222-2222-2222-222222222223', 'doctor2', '{bcrypt}$2a$10$EGUG4jSi3brf/Ww73Nw.Euhk/OrlDCer8VH0otQCQXSPNdun9ig8i', 'Lê Minh Bình', 'doctor2@medoffice.local', '0901000003', NULL, '11111111-1111-1111-1111-111111111113', 'ACTIVE', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('22222222-2222-2222-2222-222222222224', 'nurse1', '{bcrypt}$2a$10$EGUG4jSi3brf/Ww73Nw.Euhk/OrlDCer8VH0otQCQXSPNdun9ig8i', 'Phạm Thu Cúc', 'nurse1@medoffice.local', '0901000004', NULL, '11111111-1111-1111-1111-111111111114', 'ACTIVE', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('22222222-2222-2222-2222-222222222225', 'reception', '{bcrypt}$2a$10$EGUG4jSi3brf/Ww73Nw.Euhk/OrlDCer8VH0otQCQXSPNdun9ig8i', 'Hoàng Hải Dương', 'reception@medoffice.local', '0901000005', NULL, '11111111-1111-1111-1111-111111111115', 'ACTIVE', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

UPDATE chuc_vu
SET user_id = '22222222-2222-2222-2222-222222222221',
    ngay_cap_nhat = CURRENT_TIMESTAMP
WHERE id = '11111111-1111-1111-1111-111111111111'
  AND user_id IS NULL;

INSERT INTO chuyen_khoa (
    id_chuyen_khoa,
    ten_chuyen_khoa,
    user_id
) VALUES
('33333333-3333-3333-3333-333333333331', 'Nội tổng quát', '22222222-2222-2222-2222-222222222221'),
('33333333-3333-3333-3333-333333333332', 'Nhi khoa', '22222222-2222-2222-2222-222222222221'),
('33333333-3333-3333-3333-333333333333', 'Tai mũi họng', '22222222-2222-2222-2222-222222222221'),
('33333333-3333-3333-3333-333333333334', 'Da liễu', '22222222-2222-2222-2222-222222222221'),
('33333333-3333-3333-3333-333333333335', 'Sản phụ khoa', '22222222-2222-2222-2222-222222222221');

INSERT INTO nha_cung_cap (
    id,
    ma_nha_cung_cap,
    ten_nha_cung_cap,
    trang_thai,
    ngay_tao
) VALUES
('44444444-4444-4444-4444-444444444441', 'NCC001', 'Công ty Dược phẩm An Tâm', 'ACTIVE', CURRENT_TIMESTAMP),
('44444444-4444-4444-4444-444444444442', 'NCC002', 'Thiết bị Y tế Minh Khang', 'ACTIVE', CURRENT_TIMESTAMP),
('44444444-4444-4444-4444-444444444443', 'NCC003', 'Suất ăn Bệnh viện Hoa Sen', 'ACTIVE', CURRENT_TIMESTAMP);

INSERT INTO ho_so_nhan_vien (
    id,
    nguoi_dung_id,
    code,
    name,
    birth_date,
    gender,
    identity_number,
    social_insurance,
    email,
    phone,
    degree,
    specialty,
    academic_title,
    academic_title_name,
    certificate,
    position_code,
    honor_title,
    signing_pin,
    signing_account,
    signing_otp,
    invoice_password,
    avatar_image,
    signature_image,
    locked_from,
    locked_to,
    prescription_account,
    prescription_password,
    online_booking,
    active,
    note,
    created_at,
    updated_at
) VALUES
('55555555-5555-5555-5555-555555555551', '22222222-2222-2222-2222-222222222221', 'NV0001', 'Nguyễn Văn Quản', '1980-02-10', 1, '079080000001', 'BHXH000001', 'admin@medoffice.local', '0901000001', 'BS.CKII', 'Nội tổng quát', 'CKII', 'Chuyên khoa II', '001001/BYT-CCHN', 'GIAM_DOC', 'Thầy thuốc ưu tú', '', '', '', '', '', '', NULL, NULL, 'admin_rx', 'clinic123', b'0', b'1', 'Tài khoản quản trị mẫu', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('55555555-5555-5555-5555-555555555552', '22222222-2222-2222-2222-222222222222', 'NV0002', 'Trần Thị An', '1988-06-15', 2, '079088000002', 'BHXH000002', 'doctor1@medoffice.local', '0901000002', 'ThS.BS', 'Nhi khoa', 'THS', 'Thạc sĩ', '001002/BYT-CCHN', 'BAC_SI', '', '', '', '', '', '', '', NULL, NULL, 'doctor1_rx', 'clinic123', b'1', b'1', 'Bác sĩ khám nhi', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('55555555-5555-5555-5555-555555555553', '22222222-2222-2222-2222-222222222223', 'NV0003', 'Lê Minh Bình', '1985-11-03', 1, '079085000003', 'BHXH000003', 'doctor2@medoffice.local', '0901000003', 'BS.CKI', 'Tai mũi họng', 'CKI', 'Chuyên khoa I', '001003/BYT-CCHN', 'BAC_SI', '', '', '', '', '', '', '', NULL, NULL, 'doctor2_rx', 'clinic123', b'1', b'1', 'Bác sĩ tai mũi họng', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('55555555-5555-5555-5555-555555555554', '22222222-2222-2222-2222-222222222224', 'NV0004', 'Phạm Thu Cúc', '1992-04-22', 2, '079092000004', 'BHXH000004', 'nurse1@medoffice.local', '0901000004', 'CNĐD', 'Điều dưỡng', '', '', '001004/BYT-CCHN', 'DIEU_DUONG', '', '', '', '', '', '', '', NULL, NULL, '', '', b'0', b'1', 'Điều dưỡng phòng khám', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('55555555-5555-5555-5555-555555555555', '22222222-2222-2222-2222-222222222225', 'NV0005', 'Hoàng Hải Dương', '1994-09-09', 1, '079094000005', 'BHXH000005', 'reception@medoffice.local', '0901000005', 'CĐ', 'Hành chính', '', '', '', 'LE_TAN', '', '', '', '', '', '', '', NULL, NULL, '', '', b'0', b'1', 'Lễ tân tiếp nhận', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

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
    is_deleted,
    nguoi_tao_id,
    nguoi_cap_nhat_id
) VALUES
('66666666-6666-6666-6666-666666666661', 'CV-UBND-001/2026', 'DEN-001', 'Triển khai kiểm tra an toàn phòng khám', 'Yêu cầu rà soát hồ sơ pháp lý và quy trình an toàn.', 'UBND Quận', 'Nguyễn Văn A', '2026-05-01', '2026-05-02', 'BINH_THUONG', 'THUONG', 'EMAIL', '2026-05-15', 'BINH_THUONG', 3, 1, 'Rà soát điều kiện hoạt động phòng khám', '', 'Giao bộ phận hành chính tổng hợp báo cáo', '', 'MOI_TIEP_NHAN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, b'0', b'0', b'0', '22222222-2222-2222-2222-222222222225', '22222222-2222-2222-2222-222222222225'),
('66666666-6666-6666-6666-666666666662', 'CV-SYT-015/2026', 'DEN-002', 'Cập nhật hướng dẫn báo cáo bệnh truyền nhiễm', 'Sở Y tế yêu cầu cập nhật biểu mẫu báo cáo định kỳ.', 'Sở Y tế', 'Trần Thị B', '2026-05-06', '2026-05-06', 'KHAN', 'THUONG', 'VAN_THU', '2026-05-10', 'KHAN', 5, 1, 'Cập nhật quy trình báo cáo bệnh truyền nhiễm', '', 'Bác sĩ phụ trách chuyên môn xử lý', '', 'DANG_XU_LY', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, b'1', b'0', b'0', '22222222-2222-2222-2222-222222222225', '22222222-2222-2222-2222-222222222221'),
('66666666-6666-6666-6666-666666666663', 'CV-BHXH-021/2026', 'DEN-003', 'Đối soát dữ liệu bảo hiểm y tế tháng 05', 'Đề nghị đối soát dữ liệu khám chữa bệnh bảo hiểm y tế.', 'BHXH Thành phố', 'Lê Văn C', '2026-05-12', '2026-05-13', 'BINH_THUONG', 'THUONG', 'EMAIL', '2026-05-25', 'BINH_THUONG', 4, 1, 'Đối soát dữ liệu BHYT tháng 05/2026', '', '', '', 'HOAN_THANH', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, b'1', b'1', b'0', '22222222-2222-2222-2222-222222222225', '22222222-2222-2222-2222-222222222221');

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
('77777777-7777-7777-7777-777777777771', 'DI-001/2026', 'Báo cáo rà soát điều kiện hoạt động', 'Báo cáo kết quả rà soát điều kiện hoạt động phòng khám.', 'UBND Quận', '2026-05-14', '22222222-2222-2222-2222-222222222221', 'DA_BAN_HANH', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('77777777-7777-7777-7777-777777777772', 'DI-002/2026', 'Báo cáo bệnh truyền nhiễm tuần 20', 'Tổng hợp số liệu báo cáo bệnh truyền nhiễm tuần 20.', 'Sở Y tế', '2026-05-18', '22222222-2222-2222-2222-222222222221', 'DA_BAN_HANH', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('77777777-7777-7777-7777-777777777773', 'DI-003/2026', 'Công văn đối soát dữ liệu BHYT', 'Gửi dữ liệu đối soát khám chữa bệnh bảo hiểm y tế tháng 05.', 'BHXH Thành phố', '2026-05-26', '22222222-2222-2222-2222-222222222221', 'DU_THAO', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO rowboat_chat_histories (
    id,
    username,
    user_message,
    assistant_message,
    request_state_json,
    response_state_json,
    status,
    error_message,
    created_at
) VALUES
('88888888-8888-8888-8888-888888888881', 'reception', 'Tóm tắt công văn đến DEN-001', 'Công văn yêu cầu rà soát hồ sơ pháp lý và quy trình an toàn phòng khám.', NULL, NULL, 'SUCCESS', NULL, CURRENT_TIMESTAMP),
('88888888-8888-8888-8888-888888888882', 'admin', 'Liệt kê các việc cần làm hôm nay', 'Có 2 việc cần ưu tiên: xử lý báo cáo Sở Y tế và đối soát dữ liệu BHYT.', NULL, NULL, 'SUCCESS', NULL, CURRENT_TIMESTAMP);

INSERT INTO doctor_meal_dishes (
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
) VALUES
('99999999-9999-9999-9999-999999999901', 2026, 22, 'MONDAY', '2026-05-25', 'lunch', 'Bữa trưa', 'Cơm gà xối mỡ', 45000, 45000, 720, '11:30', 'Kèm canh rau', 'admin', CURRENT_TIMESTAMP),
('99999999-9999-9999-9999-999999999902', 2026, 22, 'MONDAY', '2026-05-25', 'dinner', 'Bữa tối', 'Bún bò Huế', 50000, 50000, 680, '17:30', '', 'admin', CURRENT_TIMESTAMP),
('99999999-9999-9999-9999-999999999903', 2026, 22, 'TUESDAY', '2026-05-26', 'lunch', 'Bữa trưa', 'Cơm sườn bì chả', 48000, 48000, 760, '11:30', '', 'admin', CURRENT_TIMESTAMP),
('99999999-9999-9999-9999-999999999904', 2026, 22, 'WEDNESDAY', '2026-05-27', 'lunch', 'Bữa trưa', 'Phở bò tái nạm', 52000, 52000, 640, '11:30', '', 'admin', CURRENT_TIMESTAMP),
('99999999-9999-9999-9999-999999999905', 2026, 22, 'THURSDAY', '2026-05-28', 'lunch', 'Bữa trưa', 'Mì Quảng gà', 47000, 47000, 700, '11:30', '', 'admin', CURRENT_TIMESTAMP);

INSERT INTO doctor_meal_registrations (
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
) VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', 2026, 22, 'Tuần 22/2026', '2026-05-25', '2026-05-31', 'doctor1', 'doctor1', 'Trần Thị An', 'Nhi khoa', 'Bác sĩ', 2, 93000, '{"week":{"year":2026,"number":22,"label":"Tuần 22/2026"},"requester":{"username":"doctor1","name":"Trần Thị An","department":"Nhi khoa","role":"Bác sĩ"},"summary":{"totalQuantity":2,"totalAmount":93000}}', CURRENT_TIMESTAMP),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2', 2026, 22, 'Tuần 22/2026', '2026-05-25', '2026-05-31', 'doctor2', 'doctor2', 'Lê Minh Bình', 'Tai mũi họng', 'Bác sĩ', 1, 52000, '{"week":{"year":2026,"number":22,"label":"Tuần 22/2026"},"requester":{"username":"doctor2","name":"Lê Minh Bình","department":"Tai mũi họng","role":"Bác sĩ"},"summary":{"totalQuantity":1,"totalAmount":52000}}', CURRENT_TIMESTAMP);

INSERT INTO doctor_meal_registration_items (
    id,
    registration_id,
    meal_date,
    day_of_week,
    meal_id,
    meal_label,
    meal_quantity,
    meal_amount
) VALUES
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb1', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', '2026-05-25', 'MONDAY', 'lunch', 'Bữa trưa', 1, 45000),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb2', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', '2026-05-26', 'TUESDAY', 'lunch', 'Bữa trưa', 1, 48000),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb3', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2', '2026-05-27', 'WEDNESDAY', 'lunch', 'Bữa trưa', 1, 52000);

INSERT INTO doctor_meal_registration_item_snapshots (
    id,
    registration_item_id,
    name,
    serving_time,
    quantity,
    unit_price,
    line_total
) VALUES
('cccccccc-cccc-cccc-cccc-ccccccccccc1', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb1', 'Cơm gà xối mỡ', '11:30', 1, 45000, 45000),
('cccccccc-cccc-cccc-cccc-ccccccccccc2', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb2', 'Cơm sườn bì chả', '11:30', 1, 48000, 48000),
('cccccccc-cccc-cccc-cccc-ccccccccccc3', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb3', 'Phở bò tái nạm', '11:30', 1, 52000, 52000);

COMMIT;
