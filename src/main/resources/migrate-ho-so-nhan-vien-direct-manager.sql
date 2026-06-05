ALTER TABLE ho_so_nhan_vien
    ADD COLUMN IF NOT EXISTS cap_tren_truc_tiep_id CHAR(36) NULL AFTER ma_chuc_vu;

ALTER TABLE ho_so_nhan_vien
    ADD INDEX IF NOT EXISTS idx_ho_so_nhan_vien_cap_tren_truc_tiep_id (cap_tren_truc_tiep_id);

ALTER TABLE ho_so_nhan_vien
    ADD CONSTRAINT fk_ho_so_nhan_vien_cap_tren_truc_tiep
        FOREIGN KEY (cap_tren_truc_tiep_id) REFERENCES ho_so_nhan_vien (id)
        ON DELETE SET NULL;
