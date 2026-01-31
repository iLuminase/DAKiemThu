/* =========================================================
   TẠO LOGIN, USER VÀ PHÂN QUYỀN (FIXED VERSION)
   ========================================================= */

USE InventoryManagement;
GO

/* ========================================
   BƯỚC 1: XÓA USER CŨ (NẾU TỒN TẠI)
   ======================================== */
IF EXISTS (SELECT 1 FROM sys.database_principals WHERE name = 'db_admin_user')
    DROP USER db_admin_user;
IF EXISTS (SELECT 1 FROM sys.database_principals WHERE name = 'dev_user')
    DROP USER dev_user;
IF EXISTS (SELECT 1 FROM sys.database_principals WHERE name = 'staff_user')
    DROP USER staff_user;
GO

/* ========================================
   BƯỚC 2: XÓA LOGIN CŨ (SERVER LEVEL)
   ======================================== */
USE master;
GO
IF EXISTS (SELECT 1 FROM sys.server_principals WHERE name = 'db_admin_login')
    DROP LOGIN db_admin_login;
IF EXISTS (SELECT 1 FROM sys.server_principals WHERE name = 'dev_login')
    DROP LOGIN dev_login;
IF EXISTS (SELECT 1 FROM sys.server_principals WHERE name = 'staff_login')
    DROP LOGIN staff_login;
GO

/* ========================================
   BƯỚC 3: TẠO LOGIN
   ======================================== */
CREATE LOGIN db_admin_login
WITH PASSWORD = 'Admin@123456',
CHECK_POLICY = OFF;

CREATE LOGIN dev_login
WITH PASSWORD = 'Dev@123456',
CHECK_POLICY = OFF;

CREATE LOGIN staff_login
WITH PASSWORD = 'Staff@123456',
CHECK_POLICY = OFF;
GO

/* ========================================
   BƯỚC 4: TẠO USER TRONG DATABASE
   ======================================== */
USE InventoryManagement;
GO

CREATE USER db_admin_user FOR LOGIN db_admin_login;
CREATE USER dev_user FOR LOGIN dev_login;
CREATE USER staff_user FOR LOGIN staff_login;
GO

/* ========================================
   BƯỚC 5: PHÂN QUYỀN DB ADMIN
   ======================================== */
ALTER ROLE db_owner ADD MEMBER db_admin_user;
GO

/* ========================================
   BƯỚC 6: ROLE DEV
   ======================================== */
IF NOT EXISTS (SELECT 1 FROM sys.database_principals WHERE name = 'app_dev')
    CREATE ROLE app_dev;
GO

ALTER ROLE app_dev ADD MEMBER dev_user;
GO

GRANT SELECT, INSERT, UPDATE, DELETE ON users TO app_dev;
GRANT SELECT, INSERT, UPDATE, DELETE ON products TO app_dev;
GRANT SELECT, INSERT, UPDATE, DELETE ON inventory TO app_dev;
GRANT SELECT, INSERT, UPDATE, DELETE ON warehouses TO app_dev;
GRANT SELECT, INSERT, UPDATE, DELETE ON categories TO app_dev;
GRANT SELECT, INSERT, UPDATE, DELETE ON suppliers TO app_dev;
GRANT SELECT, INSERT, UPDATE, DELETE ON stock_in TO app_dev;
GRANT SELECT, INSERT, UPDATE, DELETE ON stock_in_items TO app_dev;
GRANT SELECT, INSERT, UPDATE, DELETE ON stock_out TO app_dev;
GRANT SELECT, INSERT, UPDATE, DELETE ON stock_out_items TO app_dev;
GRANT SELECT, INSERT, UPDATE, DELETE ON promotions TO app_dev;
GRANT SELECT, INSERT, UPDATE, DELETE ON promotion_products TO app_dev;
GRANT SELECT ON audit_logs TO app_dev;

/* QUAN TRỌNG: cho phép dev test Stored Procedure */
GRANT EXECUTE TO app_dev;
GO

/* ========================================
   BƯỚC 7: VIEW CHO STAFF (DEMO)
   ======================================== */
IF OBJECT_ID('v_staff_products_wh_hcm', 'V') IS NOT NULL
    DROP VIEW v_staff_products_wh_hcm;
GO

CREATE VIEW v_staff_products_wh_hcm AS
SELECT
    p.id, p.code, p.name, p.slug, p.unit,
    p.price_out,
    i.quantity,
    w.name AS warehouse_name,
    w.code AS warehouse_code
FROM inventory i
JOIN products p ON p.id = i.product_id
JOIN warehouses w ON w.id = i.warehouse_id
WHERE w.id = 1;
GO

IF OBJECT_ID('v_staff_products_wh_hn', 'V') IS NOT NULL
    DROP VIEW v_staff_products_wh_hn;
GO

CREATE VIEW v_staff_products_wh_hn AS
SELECT
    p.id, p.code, p.name, p.slug, p.unit,
    p.price_out,
    i.quantity,
    w.name AS warehouse_name,
    w.code AS warehouse_code
FROM inventory i
JOIN products p ON p.id = i.product_id
JOIN warehouses w ON w.id = i.warehouse_id
WHERE w.id = 2;
GO

/* ========================================
   BƯỚC 8: ROLE STAFF
   ======================================== */
IF NOT EXISTS (SELECT 1 FROM sys.database_principals WHERE name = 'app_staff')
    CREATE ROLE app_staff;
GO

ALTER ROLE app_staff ADD MEMBER staff_user;
GO

GRANT SELECT ON v_staff_products_wh_hcm TO app_staff;
GRANT SELECT ON v_staff_products_wh_hn TO app_staff;

DENY SELECT, INSERT, UPDATE, DELETE ON inventory TO app_staff;
DENY SELECT, INSERT, UPDATE, DELETE ON products TO app_staff;
DENY SELECT, INSERT, UPDATE, DELETE ON warehouses TO app_staff;
DENY SELECT, INSERT, UPDATE, DELETE ON stock_in TO app_staff;
DENY SELECT, INSERT, UPDATE, DELETE ON stock_out TO app_staff;
GO

PRINT '✅ HOÀN TẤT TẠO LOGIN & PHÂN QUYỀN';
GO
