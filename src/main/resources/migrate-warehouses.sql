SET NAMES utf8mb4;

USE med_office;

CREATE TABLE IF NOT EXISTS warehouses (
    id CHAR(36) NOT NULL DEFAULT (UUID()),
    code VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NULL,
    location VARCHAR(500) NOT NULL,
    note VARCHAR(2000) NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    parent_warehouse_id CHAR(36) NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_warehouses_code (code),
    KEY idx_warehouses_status (status),
    KEY idx_warehouses_parent (parent_warehouse_id),
    KEY idx_warehouses_created_at (created_at),
    CONSTRAINT fk_warehouses_parent
        FOREIGN KEY (parent_warehouse_id) REFERENCES warehouses (id)
        ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS warehouse_managers (
    warehouse_id CHAR(36) NOT NULL,
    employee_profile_id CHAR(36) NOT NULL,
    PRIMARY KEY (warehouse_id, employee_profile_id),
    KEY idx_warehouse_managers_employee (employee_profile_id),
    CONSTRAINT fk_warehouse_managers_warehouse
        FOREIGN KEY (warehouse_id) REFERENCES warehouses (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_warehouse_managers_employee
        FOREIGN KEY (employee_profile_id) REFERENCES ho_so_nhan_vien (id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO permissions (id, code, module_code, module_name, name, description, created_at, updated_at) VALUES
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb032', 'warehouse.view', 'warehouse', 'Module kho', 'Xem kho', 'Xem danh sách, chi tiết, phân cấp và thống kê kho', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb033', 'warehouse.manage', 'warehouse', 'Module kho', 'Quản lý kho', 'Tạo, sửa, khóa mở và cấu hình phân cấp kho', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE
    module_code = VALUES(module_code),
    module_name = VALUES(module_name),
    name = VALUES(name),
    description = VALUES(description),
    updated_at = CURRENT_TIMESTAMP;

INSERT IGNORE INTO role_permissions (role_id, permission_id, created_at)
SELECT r.id, p.id, CURRENT_TIMESTAMP
FROM roles r
JOIN permissions p ON p.code IN ('warehouse.view', 'warehouse.manage')
WHERE r.code = 'ADMIN';
