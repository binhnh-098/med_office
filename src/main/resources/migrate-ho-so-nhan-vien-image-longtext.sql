USE med_office;

ALTER TABLE ho_so_nhan_vien
    MODIFY COLUMN anh_dai_dien LONGTEXT NULL,
    MODIFY COLUMN anh_chu_ky LONGTEXT NULL;
