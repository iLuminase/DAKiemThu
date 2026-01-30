USE InventoryManagementDB;
GO
-- Tài khoản dev test(read/write)
CREATE LOGIN dev_login
WITH PASSWORD = 'Dev@123456',
CHECK_POLICY = OFF;
GO

USE InventoryManagementDB;
GO
-- Tài khoản staff(read only)
CREATE USER dev_user FOR LOGIN dev_login;
GO
CREATE LOGIN staff_login
WITH PASSWORD = 'Staff@123456',
CHECK_POLICY = OFF;
GO

USE InventoryManagementDB;
GO

CREATE USER staff_user FOR LOGIN staff_login;
GO
-- Tạo role
CREATE ROLE app_dev;
CREATE ROLE app_staff;
GO
-- Gán quyền cho role
ALTER ROLE app_dev ADD MEMBER dev_user;
ALTER ROLE app_staff ADD MEMBER staff_user;
GO

-- Phân quyền cho dev
-- Dev được thao tác gần như toàn bộ
GRANT SELECT, INSERT, UPDATE, DELETE ON users TO app_dev;
GRANT SELECT, INSERT, UPDATE, DELETE ON products TO app_dev;
GRANT SELECT, INSERT, UPDATE, DELETE ON inventory TO app_dev;
GRANT SELECT, INSERT, UPDATE, DELETE ON warehouses TO app_dev;
GRANT SELECT, INSERT, UPDATE, DELETE ON categories TO app_dev;
GRANT SELECT, INSERT, UPDATE, DELETE ON stock_in TO app_dev;
GRANT SELECT, INSERT, UPDATE, DELETE ON stock_out TO app_dev;

GO
-- Phân quyền cho staff
-- Tạo view trước
CREATE VIEW v_staff_products_wh_hcm
AS
SELECT
    p.id,
    p.code,
    p.name,
    p.unit,
    i.quantity,
    w.name AS warehouse_name
FROM inventory i
JOIN products p ON p.id = i.product_id
JOIN warehouses w ON w.id = i.warehouse_id
WHERE i.warehouse_id = 1;
GO

CREATE VIEW v_staff_products_wh_hn
AS
SELECT
    p.id,
    p.code,
    p.name,
    p.unit,
    i.quantity,
    w.name AS warehouse_name
FROM inventory i
JOIN products p ON p.id = i.product_id
JOIN warehouses w ON w.id = i.warehouse_id
WHERE i.warehouse_id = 2;
GO
-- Chỉ cho phép SELECT view
GRANT SELECT ON v_staff_products_wh_hcm TO app_staff;
GRANT SELECT ON v_staff_products_wh_hn TO app_staff;

-- Chặn truy cập bảng gốc
DENY SELECT ON inventory TO app_staff;
DENY SELECT ON products TO app_staff;
DENY SELECT ON warehouses TO app_staff;
GO