USE med_office;

ALTER TABLE chuc_vu
    MODIFY COLUMN id CHAR(36) NOT NULL DEFAULT (UUID());

ALTER TABLE chuc_vu
    ADD COLUMN user_id CHAR(36) NULL;

ALTER TABLE chuc_vu
    ADD KEY idx_chuc_vu_user_id (user_id);

ALTER TABLE chuc_vu
    ADD CONSTRAINT fk_chuc_vu_user
        FOREIGN KEY (user_id) REFERENCES nguoi_dung (id);
