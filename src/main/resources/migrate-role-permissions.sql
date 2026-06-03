SET NAMES utf8mb4;

USE med_office;

CREATE TABLE IF NOT EXISTS roles (
    id CHAR(36) NOT NULL DEFAULT (UUID()),
    code VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000) NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_roles_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS permissions (
    id CHAR(36) NOT NULL DEFAULT (UUID()),
    code VARCHAR(150) NOT NULL,
    module_code VARCHAR(100) NOT NULL,
    module_name VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000) NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_permissions_code (code),
    KEY idx_permissions_module_code (module_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS role_permissions (
    role_id CHAR(36) NOT NULL,
    permission_id CHAR(36) NOT NULL,
    created_at DATETIME NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    KEY idx_role_permissions_permission_id (permission_id),
    CONSTRAINT fk_role_permissions_role
        FOREIGN KEY (role_id) REFERENCES roles (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_role_permissions_permission
        FOREIGN KEY (permission_id) REFERENCES permissions (id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS user_roles (
    user_id CHAR(36) NOT NULL,
    role_id CHAR(36) NOT NULL,
    created_at DATETIME NOT NULL,
    PRIMARY KEY (user_id, role_id),
    KEY idx_user_roles_role_id (role_id),
    CONSTRAINT fk_user_roles_user
        FOREIGN KEY (user_id) REFERENCES nguoi_dung (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role
        FOREIGN KEY (role_id) REFERENCES roles (id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO roles (id, code, name, description, created_at, updated_at) VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', 'ADMIN', 'Admin', 'Toan quyen he thong', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2', 'NHAN_SU', 'Nhan su', 'Quan ly ho so nhan vien', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3', 'VAN_THU', 'Van thu', 'Quan ly cong van den va cong van di', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa4', 'BAC_SI', 'Bac si', 'Su dung nghiep vu bac si', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa5', 'DIEU_DUONG', 'Dieu duong', 'Su dung nghiep vu dieu duong', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa6', 'DINH_DUONG', 'Dinh duong', 'Quan ly suat an va thuc don', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description),
    updated_at = CURRENT_TIMESTAMP;

INSERT INTO permissions (id, code, module_code, module_name, name, description, created_at, updated_at) VALUES
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb001', 'overview.dashboard.view', 'overview', 'Tong quan', 'Xem dashboard', 'Xem dashboard', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb002', 'employees.directory.view', 'human-resources', 'Module nhan su', 'Xem danh sach nhan su', 'Xem danh sach nhan su', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb003', 'employees.directory.update', 'human-resources', 'Module nhan su', 'Cap nhat nhan su', 'Cap nhat nhan su', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb004', 'employees.profile.view', 'human-resources', 'Module nhan su', 'Xem ho so nhan vien', 'Xem ho so nhan vien', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb005', 'employees.organization.view', 'human-resources', 'Module nhan su', 'Xem co cau to chuc', 'Xem co cau to chuc', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb006', 'employees.contact.update', 'human-resources', 'Module nhan su', 'Cap nhat lien he nhan vien', 'Cap nhat lien he nhan vien', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb007', 'employees.contract.expiring.view', 'human-resources', 'Module nhan su', 'Xem hop dong sap het han', 'Xem hop dong sap het han', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb008', 'employees.personal.update', 'human-resources', 'Module nhan su', 'Cap nhat thong tin ca nhan', 'Cap nhat thong tin ca nhan', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb009', 'employees.bank.update', 'human-resources', 'Module nhan su', 'Cap nhat thong tin ngan hang', 'Cap nhat thong tin ngan hang', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb010', 'employees.report.department.view', 'human-resources', 'Module nhan su', 'Xem bao cao theo phong ban', 'Xem bao cao theo phong ban', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb011', 'employees.create', 'human-resources', 'Module nhan su', 'Tao nhan vien', 'Tao nhan vien', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb012', 'documents.incoming.view', 'documents', 'Module cong van', 'Xem cong van den', 'Xem cong van den', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb013', 'documents.incoming.update', 'documents', 'Module cong van', 'Cap nhat cong van den', 'Cap nhat cong van den', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb014', 'documents.outgoing.view', 'documents', 'Module cong van', 'Xem cong van di', 'Xem cong van di', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb015', 'documents.outgoing.update', 'documents', 'Module cong van', 'Cap nhat cong van di', 'Cap nhat cong van di', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb016', 'documents.reference.view', 'documents', 'Module cong van', 'Xem van ban tham chieu', 'Xem van ban tham chieu', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb017', 'documents.archive.search', 'documents', 'Module cong van', 'Tra cuu luu tru', 'Tra cuu luu tru', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb018', 'schedules.duty.view', 'schedules', 'Module lich', 'Xem lich truc', 'Xem lich truc', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb019', 'schedules.duty.update', 'schedules', 'Module lich', 'Cap nhat lich truc', 'Cap nhat lich truc', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb020', 'schedules.meeting.view', 'schedules', 'Module lich', 'Xem lich hop', 'Xem lich hop', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb021', 'schedules.room.book', 'schedules', 'Module lich', 'Dat phong hop', 'Dat phong hop', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb022', 'schedules.minutes.update', 'schedules', 'Module lich', 'Cap nhat bien ban hop', 'Cap nhat bien ban hop', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb023', 'meals.doctor.view', 'meals', 'Module suat an', 'Xem suat an bac si', 'Xem suat an bac si', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb024', 'meals.doctor.update', 'meals', 'Module suat an', 'Cap nhat suat an bac si', 'Cap nhat suat an bac si', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb025', 'meals.patient.view', 'meals', 'Module suat an', 'Xem suat an benh nhan', 'Xem suat an benh nhan', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb026', 'meals.patient.update', 'meals', 'Module suat an', 'Cap nhat suat an benh nhan', 'Cap nhat suat an benh nhan', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb027', 'meals.weekly-menu.view', 'meals', 'Module suat an', 'Xem thuc don tuan', 'Xem thuc don tuan', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb028', 'meals.weekly-menu.update', 'meals', 'Module suat an', 'Cap nhat thuc don tuan', 'Cap nhat thuc don tuan', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb029', 'system.permissions.manage', 'system', 'He thong', 'Quan ly phan quyen', 'Quan ly phan quyen', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb030', 'system.accounts.view', 'system', 'He thong', 'Xem tai khoan', 'Xem tai khoan', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb031', 'system.accounts.update', 'system', 'He thong', 'Cap nhat tai khoan', 'Cap nhat tai khoan', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb032', 'warehouse.view', 'warehouse', 'Module kho', 'Xem kho', 'Xem danh sach, chi tiet, phan cap va thong ke kho', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb033', 'warehouse.manage', 'warehouse', 'Module kho', 'Quan ly kho', 'Tao, sua, khoa mo va cau hinh phan cap kho', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE
    module_code = VALUES(module_code),
    module_name = VALUES(module_name),
    name = VALUES(name),
    description = VALUES(description),
    updated_at = CURRENT_TIMESTAMP;

DELETE rp FROM role_permissions rp
JOIN roles r ON r.id = rp.role_id
WHERE r.code IN ('ADMIN', 'NHAN_SU', 'VAN_THU', 'BAC_SI', 'DIEU_DUONG', 'DINH_DUONG');

INSERT INTO role_permissions (role_id, permission_id, created_at)
SELECT r.id, p.id, CURRENT_TIMESTAMP
FROM roles r
JOIN permissions p
WHERE r.code = 'ADMIN';

INSERT INTO role_permissions (role_id, permission_id, created_at)
SELECT r.id, p.id, CURRENT_TIMESTAMP
FROM roles r
JOIN permissions p ON p.code IN (
    'overview.dashboard.view',
    'employees.directory.view',
    'employees.directory.update',
    'employees.profile.view',
    'employees.organization.view',
    'employees.contact.update',
    'employees.contract.expiring.view',
    'employees.personal.update',
    'employees.bank.update',
    'employees.report.department.view',
    'employees.create'
)
WHERE r.code = 'NHAN_SU';

INSERT INTO role_permissions (role_id, permission_id, created_at)
SELECT r.id, p.id, CURRENT_TIMESTAMP
FROM roles r
JOIN permissions p ON p.code IN (
    'overview.dashboard.view',
    'documents.incoming.view',
    'documents.incoming.update',
    'documents.outgoing.view',
    'documents.outgoing.update',
    'documents.reference.view',
    'documents.archive.search'
)
WHERE r.code = 'VAN_THU';

INSERT INTO role_permissions (role_id, permission_id, created_at)
SELECT r.id, p.id, CURRENT_TIMESTAMP
FROM roles r
JOIN permissions p ON p.code IN (
    'overview.dashboard.view',
    'employees.profile.view',
    'meals.doctor.view',
    'meals.doctor.update',
    'schedules.duty.view'
)
WHERE r.code = 'BAC_SI';

INSERT INTO role_permissions (role_id, permission_id, created_at)
SELECT r.id, p.id, CURRENT_TIMESTAMP
FROM roles r
JOIN permissions p ON p.code IN (
    'overview.dashboard.view',
    'employees.profile.view',
    'meals.patient.view',
    'meals.patient.update',
    'schedules.duty.view'
)
WHERE r.code = 'DIEU_DUONG';

INSERT INTO role_permissions (role_id, permission_id, created_at)
SELECT r.id, p.id, CURRENT_TIMESTAMP
FROM roles r
JOIN permissions p ON p.code IN (
    'overview.dashboard.view',
    'meals.doctor.view',
    'meals.doctor.update',
    'meals.patient.view',
    'meals.patient.update',
    'meals.weekly-menu.view',
    'meals.weekly-menu.update'
)
WHERE r.code = 'DINH_DUONG';

INSERT IGNORE INTO user_roles (user_id, role_id, created_at)
SELECT nd.id, r.id, CURRENT_TIMESTAMP
FROM nguoi_dung nd
JOIN chuc_vu cv ON cv.id = nd.chuc_vu_id
JOIN roles r ON r.code = CASE cv.ma_chuc_vu
    WHEN 'GIAM_DOC' THEN 'ADMIN'
    WHEN 'TRUONG_KHOA' THEN 'NHAN_SU'
    WHEN 'LE_TAN' THEN 'VAN_THU'
    ELSE cv.ma_chuc_vu
END
WHERE nd.chuc_vu_id IS NOT NULL;
