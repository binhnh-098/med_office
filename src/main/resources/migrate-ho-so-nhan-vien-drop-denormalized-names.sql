USE med_office;

ALTER TABLE ho_so_nhan_vien
    DROP COLUMN IF EXISTS specialty_name,
    DROP COLUMN IF EXISTS position_name;
