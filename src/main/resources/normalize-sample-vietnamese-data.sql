USE med_office;

UPDATE chuc_vu SET ten_chuc_vu = 'Giám đốc', mo_ta = 'Quản lý điều hành phòng khám' WHERE ma_chuc_vu = 'GIAM_DOC';
UPDATE chuc_vu SET ten_chuc_vu = 'Trưởng khoa', mo_ta = 'Phụ trách chuyên môn theo khoa' WHERE ma_chuc_vu = 'TRUONG_KHOA';
UPDATE chuc_vu SET ten_chuc_vu = 'Bác sĩ', mo_ta = 'Khám và điều trị bệnh nhân' WHERE ma_chuc_vu = 'BAC_SI';
UPDATE chuc_vu SET ten_chuc_vu = 'Điều dưỡng', mo_ta = 'Hỗ trợ chăm sóc người bệnh' WHERE ma_chuc_vu = 'DIEU_DUONG';
UPDATE chuc_vu SET ten_chuc_vu = 'Lễ tân', mo_ta = 'Tiếp nhận và hướng dẫn bệnh nhân' WHERE ma_chuc_vu = 'LE_TAN';

UPDATE chuyen_khoa SET ten_chuyen_khoa = 'Nội tổng quát' WHERE id_chuyen_khoa = '33333333-3333-3333-3333-333333333331';
UPDATE chuyen_khoa SET ten_chuyen_khoa = 'Nhi khoa' WHERE id_chuyen_khoa = '33333333-3333-3333-3333-333333333332';
UPDATE chuyen_khoa SET ten_chuyen_khoa = 'Tai mũi họng' WHERE id_chuyen_khoa = '33333333-3333-3333-3333-333333333333';
UPDATE chuyen_khoa SET ten_chuyen_khoa = 'Da liễu' WHERE id_chuyen_khoa = '33333333-3333-3333-3333-333333333334';
UPDATE chuyen_khoa SET ten_chuyen_khoa = 'Sản phụ khoa' WHERE id_chuyen_khoa = '33333333-3333-3333-3333-333333333335';

UPDATE nha_cung_cap SET ten_nha_cung_cap = 'Công ty Dược phẩm An Tâm' WHERE ma_nha_cung_cap = 'NCC001';
UPDATE nha_cung_cap SET ten_nha_cung_cap = 'Thiết bị Y tế Minh Khang' WHERE ma_nha_cung_cap = 'NCC002';
UPDATE nha_cung_cap SET ten_nha_cung_cap = 'Suất ăn Bệnh viện Hoa Sen' WHERE ma_nha_cung_cap = 'NCC003';

UPDATE ho_so_nhan_vien
SET ten_nhan_vien = 'Nguyễn Văn Quản',
    chuyen_khoa = 'Nội tổng quát',
    ten_hoc_ham = 'Chuyên khoa II',
    danh_hieu = 'Thầy thuốc ưu tú',
    ghi_chu = 'Tài khoản quản trị mẫu'
WHERE ma_nhan_vien = 'NV0001';

UPDATE ho_so_nhan_vien
SET ten_nhan_vien = 'Trần Thị An',
    chuyen_khoa = 'Nhi khoa',
    ten_hoc_ham = 'Thạc sĩ',
    ghi_chu = 'Bác sĩ khám nhi'
WHERE ma_nhan_vien = 'NV0002';

UPDATE ho_so_nhan_vien
SET ten_nhan_vien = 'Lê Minh Bình',
    chuyen_khoa = 'Tai mũi họng',
    ten_hoc_ham = 'Chuyên khoa I',
    ghi_chu = 'Bác sĩ tai mũi họng'
WHERE ma_nhan_vien = 'NV0003';

UPDATE ho_so_nhan_vien
SET ten_nhan_vien = 'Phạm Thu Cúc',
    bang_cap = 'CNĐD',
    chuyen_khoa = 'Điều dưỡng',
    ghi_chu = 'Điều dưỡng phòng khám'
WHERE ma_nhan_vien = 'NV0004';

UPDATE ho_so_nhan_vien
SET ten_nhan_vien = 'Hoàng Hải Dương',
    bang_cap = 'CĐ',
    chuyen_khoa = 'Hành chính',
    ghi_chu = 'Lễ tân tiếp nhận'
WHERE ma_nhan_vien = 'NV0005';

UPDATE cong_van_den
SET tieu_de = 'Triển khai kiểm tra an toàn phòng khám',
    noi_dung_tom_tat = 'Yêu cầu rà soát hồ sơ pháp lý và quy trình an toàn.',
    don_vi_gui = 'UBND Quận',
    nguoi_ky = 'Nguyễn Văn A',
    trich_yeu = 'Rà soát điều kiện hoạt động phòng khám',
    y_kien_chi_dao = 'Giao bộ phận hành chính tổng hợp báo cáo'
WHERE so_den = 'DEN-001';

UPDATE cong_van_den
SET tieu_de = 'Cập nhật hướng dẫn báo cáo bệnh truyền nhiễm',
    noi_dung_tom_tat = 'Sở Y tế yêu cầu cập nhật biểu mẫu báo cáo định kỳ.',
    don_vi_gui = 'Sở Y tế',
    nguoi_ky = 'Trần Thị B',
    trich_yeu = 'Cập nhật quy trình báo cáo bệnh truyền nhiễm',
    y_kien_chi_dao = 'Bác sĩ phụ trách chuyên môn xử lý'
WHERE so_den = 'DEN-002';

UPDATE cong_van_den
SET tieu_de = 'Đối soát dữ liệu bảo hiểm y tế tháng 05',
    noi_dung_tom_tat = 'Đề nghị đối soát dữ liệu khám chữa bệnh bảo hiểm y tế.',
    don_vi_gui = 'BHXH Thành phố',
    nguoi_ky = 'Lê Văn C',
    trich_yeu = 'Đối soát dữ liệu BHYT tháng 05/2026'
WHERE so_den = 'DEN-003';

UPDATE cong_van_di
SET tieu_de = 'Báo cáo rà soát điều kiện hoạt động',
    noi_dung_tom_tat = 'Báo cáo kết quả rà soát điều kiện hoạt động phòng khám.',
    don_vi_nhan = 'UBND Quận'
WHERE so_cong_van = 'DI-001/2026';

UPDATE cong_van_di
SET tieu_de = 'Báo cáo bệnh truyền nhiễm tuần 20',
    noi_dung_tom_tat = 'Tổng hợp số liệu báo cáo bệnh truyền nhiễm tuần 20.',
    don_vi_nhan = 'Sở Y tế'
WHERE so_cong_van = 'DI-002/2026';

UPDATE cong_van_di
SET tieu_de = 'Công văn đối soát dữ liệu BHYT',
    noi_dung_tom_tat = 'Gửi dữ liệu đối soát khám chữa bệnh bảo hiểm y tế tháng 05.',
    don_vi_nhan = 'BHXH Thành phố'
WHERE so_cong_van = 'DI-003/2026';

UPDATE lich_su_chat_rowboat
SET tin_nhan_nguoi_dung = 'Tóm tắt công văn đến DEN-001',
    tin_nhan_tro_ly = 'Công văn yêu cầu rà soát hồ sơ pháp lý và quy trình an toàn phòng khám.'
WHERE id = '88888888-8888-8888-8888-888888888881';

UPDATE lich_su_chat_rowboat
SET tin_nhan_nguoi_dung = 'Liệt kê các việc cần làm hôm nay',
    tin_nhan_tro_ly = 'Có 2 việc cần ưu tiên: xử lý báo cáo Sở Y tế và đối soát dữ liệu BHYT.'
WHERE id = '88888888-8888-8888-8888-888888888882';

UPDATE mon_an_bac_si SET ten_bua_an = 'Bữa trưa', ten_mon_an = 'Cơm gà xối mỡ', ghi_chu = 'Kèm canh rau' WHERE id = '99999999-9999-9999-9999-999999999901';
UPDATE mon_an_bac_si SET ten_bua_an = 'Bữa tối', ten_mon_an = 'Bún bò Huế' WHERE id = '99999999-9999-9999-9999-999999999902';
UPDATE mon_an_bac_si SET ten_bua_an = 'Bữa trưa', ten_mon_an = 'Cơm sườn bì chả' WHERE id = '99999999-9999-9999-9999-999999999903';
UPDATE mon_an_bac_si SET ten_bua_an = 'Bữa trưa', ten_mon_an = 'Phở bò tái nạm' WHERE id = '99999999-9999-9999-9999-999999999904';
UPDATE mon_an_bac_si SET ten_bua_an = 'Bữa trưa', ten_mon_an = 'Mì Quảng gà' WHERE id = '99999999-9999-9999-9999-999999999905';

UPDATE dang_ky_bua_an_bac_si
SET nhan_tuan = 'Tuần 22/2026',
    ho_ten_nguoi_dang_ky = 'Trần Thị An',
    vai_tro_nguoi_dang_ky = 'Bác sĩ',
    du_lieu_json = '{"week":{"year":2026,"number":22,"label":"Tuần 22/2026"},"requester":{"username":"doctor1","name":"Trần Thị An","department":"Nhi khoa","role":"Bác sĩ"},"summary":{"totalQuantity":2,"totalAmount":93000}}'
WHERE id = 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1';

UPDATE dang_ky_bua_an_bac_si
SET nhan_tuan = 'Tuần 22/2026',
    ho_ten_nguoi_dang_ky = 'Lê Minh Bình',
    phong_ban_nguoi_dang_ky = 'Tai mũi họng',
    vai_tro_nguoi_dang_ky = 'Bác sĩ',
    du_lieu_json = '{"week":{"year":2026,"number":22,"label":"Tuần 22/2026"},"requester":{"username":"doctor2","name":"Lê Minh Bình","department":"Tai mũi họng","role":"Bác sĩ"},"summary":{"totalQuantity":1,"totalAmount":52000}}'
WHERE id = 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2';

UPDATE chi_tiet_dang_ky_bua_an_bac_si SET ten_bua_an = 'Bữa trưa';

UPDATE mon_an_trong_dang_ky_bua_an_bac_si SET ten_mon_an = 'Cơm gà xối mỡ' WHERE id = 'cccccccc-cccc-cccc-cccc-ccccccccccc1';
UPDATE mon_an_trong_dang_ky_bua_an_bac_si SET ten_mon_an = 'Cơm sườn bì chả' WHERE id = 'cccccccc-cccc-cccc-cccc-ccccccccccc2';
UPDATE mon_an_trong_dang_ky_bua_an_bac_si SET ten_mon_an = 'Phở bò tái nạm' WHERE id = 'cccccccc-cccc-cccc-cccc-ccccccccccc3';
