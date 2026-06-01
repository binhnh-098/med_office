USE med_office;

DELIMITER $$

DROP PROCEDURE IF EXISTS drop_chuc_vu_fks_for_column$$
CREATE PROCEDURE drop_chuc_vu_fks_for_column(IN p_column_name VARCHAR(128))
BEGIN
    DECLARE is_done INT DEFAULT FALSE;
    DECLARE constraint_name VARCHAR(128);
    DECLARE constraint_cursor CURSOR FOR
        SELECT CONSTRAINT_NAME
        FROM information_schema.KEY_COLUMN_USAGE
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'chuc_vu'
          AND COLUMN_NAME = p_column_name
          AND REFERENCED_TABLE_NAME IS NOT NULL;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET is_done = TRUE;

    OPEN constraint_cursor;
    drop_constraints: LOOP
        FETCH constraint_cursor INTO constraint_name;
        IF is_done THEN
            LEAVE drop_constraints;
        END IF;

        SET @sql = CONCAT('ALTER TABLE chuc_vu DROP FOREIGN KEY `', constraint_name, '`');
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END LOOP;
    CLOSE constraint_cursor;
END$$

DROP PROCEDURE IF EXISTS drop_chuc_vu_index_if_exists$$
CREATE PROCEDURE drop_chuc_vu_index_if_exists(IN p_index_name VARCHAR(128))
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.STATISTICS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'chuc_vu'
          AND INDEX_NAME = p_index_name
    ) THEN
        SET @sql = CONCAT('ALTER TABLE chuc_vu DROP INDEX `', p_index_name, '`');
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END$$

DROP PROCEDURE IF EXISTS drop_chuc_vu_column_if_exists$$
CREATE PROCEDURE drop_chuc_vu_column_if_exists(IN p_column_name VARCHAR(128))
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'chuc_vu'
          AND COLUMN_NAME = p_column_name
    ) THEN
        SET @sql = CONCAT('ALTER TABLE chuc_vu DROP COLUMN `', p_column_name, '`');
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END$$

DELIMITER ;

CALL drop_chuc_vu_fks_for_column('user_id');
CALL drop_chuc_vu_fks_for_column('nguoi_dung_id');
CALL drop_chuc_vu_index_if_exists('idx_chuc_vu_user_id');
CALL drop_chuc_vu_index_if_exists('idx_chuc_vu_nguoi_dung_id');
CALL drop_chuc_vu_column_if_exists('user_id');
CALL drop_chuc_vu_column_if_exists('nguoi_dung_id');

DROP PROCEDURE IF EXISTS drop_chuc_vu_fks_for_column;
DROP PROCEDURE IF EXISTS drop_chuc_vu_index_if_exists;
DROP PROCEDURE IF EXISTS drop_chuc_vu_column_if_exists;
