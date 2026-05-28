USE med_office;

CREATE TABLE IF NOT EXISTS chuyen_khoa (
    id_chuyen_khoa CHAR(36) NOT NULL DEFAULT (UUID()),
    ten_chuyen_khoa VARCHAR(255) NOT NULL,
    user_id CHAR(36) NOT NULL,
    PRIMARY KEY (id_chuyen_khoa),
    KEY idx_chuyen_khoa_user_id (user_id),
    KEY idx_chuyen_khoa_ten_chuyen_khoa (ten_chuyen_khoa),
    CONSTRAINT fk_chuyen_khoa_user
        FOREIGN KEY (user_id) REFERENCES nguoi_dung (id)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;
