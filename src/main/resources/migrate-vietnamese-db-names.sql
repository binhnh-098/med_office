USE med_office;

ALTER TABLE chuyen_khoa
    RENAME COLUMN user_id TO nguoi_dung_id,
    RENAME INDEX idx_chuyen_khoa_user_id TO idx_chuyen_khoa_nguoi_dung_id;

ALTER TABLE nguoi_dung
    RENAME COLUMN email TO thu_dien_tu;

ALTER TABLE cong_van_den
    RENAME COLUMN is_deleted TO da_xoa;

ALTER TABLE ho_so_nhan_vien
    RENAME COLUMN code TO ma_nhan_vien,
    RENAME COLUMN name TO ten_nhan_vien,
    RENAME COLUMN birth_date TO ngay_sinh,
    RENAME COLUMN gender TO gioi_tinh,
    RENAME COLUMN identity_number TO so_dinh_danh,
    RENAME COLUMN social_insurance TO so_bao_hiem_xa_hoi,
    RENAME COLUMN email TO thu_dien_tu,
    RENAME COLUMN phone TO so_dien_thoai,
    RENAME COLUMN degree TO bang_cap,
    RENAME COLUMN specialty TO chuyen_khoa,
    RENAME COLUMN academic_title TO hoc_ham,
    RENAME COLUMN academic_title_name TO ten_hoc_ham,
    RENAME COLUMN certificate TO chung_chi,
    RENAME COLUMN position_code TO ma_chuc_vu,
    RENAME COLUMN honor_title TO danh_hieu,
    RENAME COLUMN signing_pin TO ma_pin_ky,
    RENAME COLUMN signing_account TO tai_khoan_ky,
    RENAME COLUMN signing_otp TO otp_ky,
    RENAME COLUMN invoice_password TO mat_khau_hoa_don,
    RENAME COLUMN avatar_image TO anh_dai_dien,
    RENAME COLUMN signature_image TO anh_chu_ky,
    RENAME COLUMN locked_from TO khoa_tu_ngay,
    RENAME COLUMN locked_to TO khoa_den_ngay,
    RENAME COLUMN prescription_account TO tai_khoan_ke_don,
    RENAME COLUMN prescription_password TO mat_khau_ke_don,
    RENAME COLUMN online_booking TO dat_lich_truc_tuyen,
    RENAME COLUMN active TO dang_hoat_dong,
    RENAME COLUMN note TO ghi_chu,
    RENAME COLUMN created_at TO ngay_tao,
    RENAME COLUMN updated_at TO ngay_cap_nhat,
    RENAME INDEX uk_ho_so_nhan_vien_code TO uk_ho_so_nhan_vien_ma_nhan_vien,
    RENAME INDEX idx_ho_so_nhan_vien_name TO idx_ho_so_nhan_vien_ten_nhan_vien,
    RENAME INDEX idx_ho_so_nhan_vien_active TO idx_ho_so_nhan_vien_dang_hoat_dong,
    RENAME INDEX idx_ho_so_nhan_vien_specialty TO idx_ho_so_nhan_vien_chuyen_khoa;

RENAME TABLE
    rowboat_chat_histories TO lich_su_chat_rowboat,
    doctor_meal_registrations TO dang_ky_bua_an_bac_si,
    doctor_meal_registration_items TO chi_tiet_dang_ky_bua_an_bac_si,
    doctor_meal_registration_item_snapshots TO mon_an_trong_dang_ky_bua_an_bac_si,
    doctor_meal_dishes TO mon_an_bac_si;

ALTER TABLE lich_su_chat_rowboat
    RENAME COLUMN username TO ten_dang_nhap,
    RENAME COLUMN user_message TO tin_nhan_nguoi_dung,
    RENAME COLUMN assistant_message TO tin_nhan_tro_ly,
    RENAME COLUMN request_state_json TO trang_thai_yeu_cau_json,
    RENAME COLUMN response_state_json TO trang_thai_phan_hoi_json,
    RENAME COLUMN status TO trang_thai,
    RENAME COLUMN error_message TO thong_bao_loi,
    RENAME COLUMN created_at TO ngay_tao,
    RENAME INDEX idx_rowboat_chat_histories_username TO idx_lich_su_chat_rowboat_ten_dang_nhap,
    RENAME INDEX idx_rowboat_chat_histories_status TO idx_lich_su_chat_rowboat_trang_thai,
    RENAME INDEX idx_rowboat_chat_histories_created_at TO idx_lich_su_chat_rowboat_ngay_tao;

ALTER TABLE dang_ky_bua_an_bac_si
    RENAME COLUMN week_year TO nam_tuan,
    RENAME COLUMN week_number TO so_tuan,
    RENAME COLUMN week_label TO nhan_tuan,
    RENAME COLUMN week_start_date TO ngay_bat_dau_tuan,
    RENAME COLUMN week_end_date TO ngay_ket_thuc_tuan,
    RENAME COLUMN username TO ten_dang_nhap,
    RENAME COLUMN requester_username TO ten_dang_nhap_nguoi_dang_ky,
    RENAME COLUMN requester_full_name TO ho_ten_nguoi_dang_ky,
    RENAME COLUMN requester_department TO phong_ban_nguoi_dang_ky,
    RENAME COLUMN requester_role TO vai_tro_nguoi_dang_ky,
    RENAME COLUMN total_quantity TO tong_so_luong,
    RENAME COLUMN total_amount TO tong_tien,
    RENAME COLUMN payload_json TO du_lieu_json,
    RENAME COLUMN created_at TO ngay_tao,
    RENAME INDEX idx_doctor_meal_registrations_week_user TO idx_dang_ky_bua_an_bac_si_tuan_nguoi_dung,
    RENAME INDEX idx_doctor_meal_registrations_week_requester TO idx_dang_ky_bua_an_bac_si_tuan_nguoi_dang_ky,
    RENAME INDEX idx_doctor_meal_registrations_created_at TO idx_dang_ky_bua_an_bac_si_ngay_tao;

ALTER TABLE chi_tiet_dang_ky_bua_an_bac_si
    RENAME COLUMN registration_id TO dang_ky_id,
    RENAME COLUMN meal_date TO ngay_an,
    RENAME COLUMN day_of_week TO thu_trong_tuan,
    RENAME COLUMN meal_id TO ma_bua_an,
    RENAME COLUMN meal_label TO ten_bua_an,
    RENAME COLUMN meal_quantity TO so_luong_bua_an,
    RENAME COLUMN meal_amount TO thanh_tien_bua_an,
    RENAME INDEX idx_doctor_meal_registration_items_registration TO idx_chi_tiet_dang_ky_bua_an_bac_si_dang_ky,
    RENAME INDEX idx_doctor_meal_registration_items_date_meal TO idx_chi_tiet_dang_ky_bua_an_bac_si_ngay_bua_an;

ALTER TABLE mon_an_trong_dang_ky_bua_an_bac_si
    RENAME COLUMN registration_item_id TO chi_tiet_dang_ky_id,
    RENAME COLUMN name TO ten_mon_an,
    RENAME COLUMN serving_time TO gio_phuc_vu,
    RENAME COLUMN quantity TO so_luong,
    RENAME COLUMN unit_price TO don_gia,
    RENAME COLUMN line_total TO thanh_tien,
    RENAME INDEX idx_doctor_meal_registration_item_snapshots_item TO idx_mon_an_trong_dang_ky_bua_an_bac_si_chi_tiet;

ALTER TABLE mon_an_bac_si
    RENAME COLUMN week_year TO nam_tuan,
    RENAME COLUMN week_number TO so_tuan,
    RENAME COLUMN day_of_week TO thu_trong_tuan,
    RENAME COLUMN meal_date TO ngay_an,
    RENAME COLUMN meal_id TO ma_bua_an,
    RENAME COLUMN meal_label TO ten_bua_an,
    RENAME COLUMN name TO ten_mon_an,
    RENAME COLUMN price TO gia,
    RENAME COLUMN unit_price TO don_gia,
    RENAME COLUMN calories TO calo,
    RENAME COLUMN serving_time TO gio_phuc_vu,
    RENAME COLUMN note TO ghi_chu,
    RENAME COLUMN created_by TO nguoi_tao,
    RENAME COLUMN created_at TO ngay_tao,
    RENAME INDEX idx_doctor_meal_dishes_week_day TO idx_mon_an_bac_si_tuan_thu,
    RENAME INDEX idx_doctor_meal_dishes_date_meal TO idx_mon_an_bac_si_ngay_bua_an,
    RENAME INDEX idx_doctor_meal_dishes_created_at TO idx_mon_an_bac_si_ngay_tao;
