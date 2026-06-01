USE med_office;

DELIMITER $$

DROP PROCEDURE IF EXISTS drop_fk_if_exists$$
CREATE PROCEDURE drop_fk_if_exists(
    IN p_table_name VARCHAR(128),
    IN p_constraint_name VARCHAR(128)
)
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.TABLE_CONSTRAINTS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = p_table_name
          AND CONSTRAINT_NAME = p_constraint_name
          AND CONSTRAINT_TYPE = 'FOREIGN KEY'
    ) THEN
        SET @sql = CONCAT(
            'ALTER TABLE `', p_table_name, '` DROP FOREIGN KEY `', p_constraint_name, '`'
        );
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END$$

DROP PROCEDURE IF EXISTS add_index_if_missing$$
CREATE PROCEDURE add_index_if_missing(
    IN p_table_name VARCHAR(128),
    IN p_index_name VARCHAR(128),
    IN p_column_list VARCHAR(512)
)
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.STATISTICS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = p_table_name
          AND INDEX_NAME = p_index_name
    ) THEN
        SET @sql = CONCAT(
            'ALTER TABLE `', p_table_name, '` ADD INDEX `', p_index_name, '` (', p_column_list, ')'
        );
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END$$

DROP PROCEDURE IF EXISTS add_fk_if_missing$$
CREATE PROCEDURE add_fk_if_missing(
    IN p_table_name VARCHAR(128),
    IN p_constraint_name VARCHAR(128),
    IN p_fk_column VARCHAR(128),
    IN p_referenced_table VARCHAR(128),
    IN p_referenced_column VARCHAR(128),
    IN p_on_delete VARCHAR(64)
)
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.TABLE_CONSTRAINTS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = p_table_name
          AND CONSTRAINT_NAME = p_constraint_name
          AND CONSTRAINT_TYPE = 'FOREIGN KEY'
    ) THEN
        SET @sql = CONCAT(
            'ALTER TABLE `', p_table_name, '` ADD CONSTRAINT `', p_constraint_name,
            '` FOREIGN KEY (`', p_fk_column, '`) REFERENCES `', p_referenced_table,
            '` (`', p_referenced_column, '`) ', p_on_delete
        );
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END$$

DELIMITER ;

CALL drop_fk_if_exists('chi_tiet_dang_ky_bua_an_bac_si', 'FK81oxso7jllvi7yg34duv1havg');
CALL drop_fk_if_exists('chi_tiet_dang_ky_bua_an_bac_si', 'fk_chi_tiet_dang_ky_bua_an_bac_si_dang_ky');
CALL drop_fk_if_exists('mon_an_trong_dang_ky_bua_an_bac_si', 'FKm573xhhq7tcdq1kejaltsuhhu');
CALL drop_fk_if_exists('mon_an_trong_dang_ky_bua_an_bac_si', 'fk_mon_an_trong_dang_ky_bua_an_bac_si_chi_tiet');
CALL drop_fk_if_exists('chuc_vu', 'fk_chuc_vu_user');
CALL drop_fk_if_exists('chuc_vu', 'fk_chuc_vu_nguoi_dung');
CALL drop_fk_if_exists('nguoi_dung', 'fk_nguoi_dung_chuc_vu');
CALL drop_fk_if_exists('ho_so_nhan_vien', 'fk_ho_so_nhan_vien_nguoi_dung');
CALL drop_fk_if_exists('chuyen_khoa', 'fk_chuyen_khoa_user');
CALL drop_fk_if_exists('chuyen_khoa', 'fk_chuyen_khoa_nguoi_dung');
CALL drop_fk_if_exists('cong_van_den', 'fk_cong_van_den_don_vi_gui');
CALL drop_fk_if_exists('cong_van_den', 'fk_cong_van_den_nguoi_xu_ly');
CALL drop_fk_if_exists('cong_van_den', 'fk_cong_van_den_ho_so');
CALL drop_fk_if_exists('cong_van_den', 'fk_cong_van_den_nguoi_tao');
CALL drop_fk_if_exists('cong_van_den', 'fk_cong_van_den_nguoi_cap_nhat');
CALL drop_fk_if_exists('cong_van_di', 'fk_cong_van_di_nguoi_ky');

ALTER TABLE chuc_vu
    MODIFY id CHAR(36) NOT NULL DEFAULT (UUID());

ALTER TABLE nha_cung_cap
    MODIFY id CHAR(36) NOT NULL DEFAULT (UUID());

ALTER TABLE nguoi_dung
    MODIFY id CHAR(36) NOT NULL DEFAULT (UUID()),
    MODIFY phong_ban_id CHAR(36) NULL,
    MODIFY chuc_vu_id CHAR(36) NULL;

ALTER TABLE ho_so_nhan_vien
    MODIFY id CHAR(36) NOT NULL DEFAULT (UUID()),
    MODIFY nguoi_dung_id CHAR(36) NULL;

ALTER TABLE chuyen_khoa
    MODIFY id_chuyen_khoa CHAR(36) NOT NULL DEFAULT (UUID()),
    MODIFY nguoi_dung_id CHAR(36) NOT NULL;

ALTER TABLE cong_van_den
    MODIFY id CHAR(36) NOT NULL DEFAULT (UUID()),
    MODIFY don_vi_gui_id CHAR(36) NULL,
    MODIFY phong_ban_xu_ly_id CHAR(36) NULL,
    MODIFY nguoi_xu_ly_id CHAR(36) NULL,
    MODIFY loai_van_ban_id CHAR(36) NULL,
    MODIFY linh_vuc_id CHAR(36) NULL,
    MODIFY ho_so_id CHAR(36) NULL,
    MODIFY nguoi_tao_id CHAR(36) NULL,
    MODIFY nguoi_cap_nhat_id CHAR(36) NULL;

ALTER TABLE cong_van_di
    MODIFY id CHAR(36) NOT NULL DEFAULT (UUID()),
    MODIFY nguoi_ky_id CHAR(36) NULL;

ALTER TABLE lich_su_chat_rowboat
    MODIFY id CHAR(36) NOT NULL DEFAULT (UUID());

ALTER TABLE dang_ky_bua_an_bac_si
    MODIFY id CHAR(36) NOT NULL DEFAULT (UUID());

ALTER TABLE chi_tiet_dang_ky_bua_an_bac_si
    MODIFY id CHAR(36) NOT NULL DEFAULT (UUID()),
    MODIFY dang_ky_id CHAR(36) NOT NULL;

ALTER TABLE mon_an_trong_dang_ky_bua_an_bac_si
    MODIFY id CHAR(36) NOT NULL DEFAULT (UUID()),
    MODIFY chi_tiet_dang_ky_id CHAR(36) NOT NULL;

ALTER TABLE mon_an_bac_si
    MODIFY id CHAR(36) NOT NULL DEFAULT (UUID());

CALL add_index_if_missing('nguoi_dung', 'idx_nguoi_dung_chuc_vu_id', '`chuc_vu_id`');
CALL add_index_if_missing('chuyen_khoa', 'idx_chuyen_khoa_nguoi_dung_id', '`nguoi_dung_id`');
CALL add_index_if_missing('cong_van_den', 'idx_cong_van_den_don_vi_gui_id', '`don_vi_gui_id`');
CALL add_index_if_missing('cong_van_den', 'idx_cong_van_den_nguoi_xu_ly_id', '`nguoi_xu_ly_id`');
CALL add_index_if_missing('cong_van_den', 'idx_cong_van_den_ho_so_id', '`ho_so_id`');
CALL add_index_if_missing('cong_van_den', 'idx_cong_van_den_nguoi_tao_id', '`nguoi_tao_id`');
CALL add_index_if_missing('cong_van_den', 'idx_cong_van_den_nguoi_cap_nhat_id', '`nguoi_cap_nhat_id`');
CALL add_index_if_missing('cong_van_di', 'idx_cong_van_di_nguoi_ky_id', '`nguoi_ky_id`');
CALL add_index_if_missing('chi_tiet_dang_ky_bua_an_bac_si', 'idx_chi_tiet_dang_ky_bua_an_bac_si_dang_ky', '`dang_ky_id`');
CALL add_index_if_missing('mon_an_trong_dang_ky_bua_an_bac_si', 'idx_mon_an_trong_dang_ky_bua_an_bac_si_chi_tiet', '`chi_tiet_dang_ky_id`');

CALL add_fk_if_missing('nguoi_dung', 'fk_nguoi_dung_chuc_vu', 'chuc_vu_id', 'chuc_vu', 'id', 'ON DELETE SET NULL');
CALL add_fk_if_missing('ho_so_nhan_vien', 'fk_ho_so_nhan_vien_nguoi_dung', 'nguoi_dung_id', 'nguoi_dung', 'id', 'ON DELETE SET NULL');
CALL add_fk_if_missing('chuyen_khoa', 'fk_chuyen_khoa_nguoi_dung', 'nguoi_dung_id', 'nguoi_dung', 'id', 'ON DELETE RESTRICT');
CALL add_fk_if_missing('cong_van_den', 'fk_cong_van_den_don_vi_gui', 'don_vi_gui_id', 'nha_cung_cap', 'id', 'ON DELETE SET NULL');
CALL add_fk_if_missing('cong_van_den', 'fk_cong_van_den_nguoi_xu_ly', 'nguoi_xu_ly_id', 'nguoi_dung', 'id', 'ON DELETE SET NULL');
CALL add_fk_if_missing('cong_van_den', 'fk_cong_van_den_ho_so', 'ho_so_id', 'ho_so_nhan_vien', 'id', 'ON DELETE SET NULL');
CALL add_fk_if_missing('cong_van_den', 'fk_cong_van_den_nguoi_tao', 'nguoi_tao_id', 'nguoi_dung', 'id', 'ON DELETE SET NULL');
CALL add_fk_if_missing('cong_van_den', 'fk_cong_van_den_nguoi_cap_nhat', 'nguoi_cap_nhat_id', 'nguoi_dung', 'id', 'ON DELETE SET NULL');
CALL add_fk_if_missing('cong_van_di', 'fk_cong_van_di_nguoi_ky', 'nguoi_ky_id', 'nguoi_dung', 'id', 'ON DELETE SET NULL');
CALL add_fk_if_missing('chi_tiet_dang_ky_bua_an_bac_si', 'fk_chi_tiet_dang_ky_bua_an_bac_si_dang_ky', 'dang_ky_id', 'dang_ky_bua_an_bac_si', 'id', 'ON DELETE CASCADE');
CALL add_fk_if_missing('mon_an_trong_dang_ky_bua_an_bac_si', 'fk_mon_an_trong_dang_ky_bua_an_bac_si_chi_tiet', 'chi_tiet_dang_ky_id', 'chi_tiet_dang_ky_bua_an_bac_si', 'id', 'ON DELETE CASCADE');

DROP PROCEDURE IF EXISTS add_fk_if_missing;
DROP PROCEDURE IF EXISTS add_index_if_missing;
DROP PROCEDURE IF EXISTS drop_fk_if_exists;
