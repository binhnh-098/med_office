USE med_office;

ALTER TABLE ho_so_nhan_vien
    MODIFY COLUMN avatar_image LONGTEXT NULL,
    MODIFY COLUMN signature_image LONGTEXT NULL;
