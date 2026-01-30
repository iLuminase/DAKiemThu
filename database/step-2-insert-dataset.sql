/* =========================================================
   SAMPLE DATA FOR INVENTORY MANAGEMENT SYSTEM (MSSQL)
   ========================================================= */
USE InventoryManagementDB;
GO
SET NOCOUNT ON;

/* =========================
   1. ROLES
   ========================= */
INSERT INTO roles (code, name, description) VALUES
('ADMIN',   N'Quản trị hệ thống', N'Toàn quyền'),
('MANAGER', N'Quản lý',          N'Quản lý kho'),
('STAFF',   N'Nhân viên',        N'Nhân viên kho'),
('ACCOUNT', N'Kế toán',          N'Kế toán kho'),
('VIEWER',  N'Xem báo cáo',      N'Chỉ xem');

/* =========================
   2. USER GROUPS
   ========================= */
INSERT INTO user_groups (code, name) VALUES
('WH_HCM',  N'Kho Hồ Chí Minh'),
('WH_HN',   N'Kho Hà Nội'),
('SALE',    N'Bộ phận bán hàng'),
('FIN',     N'Phòng kế toán'),
('ADMIN_G', N'Ban quản trị');

/* =========================
   3. USERS (UUID FIXED)
   ========================= */
DECLARE @u1 UNIQUEIDENTIFIER = NEWID();
DECLARE @u2 UNIQUEIDENTIFIER = NEWID();
DECLARE @u3 UNIQUEIDENTIFIER = NEWID();
DECLARE @u4 UNIQUEIDENTIFIER = NEWID();
DECLARE @u5 UNIQUEIDENTIFIER = NEWID();

INSERT INTO users (id, clerk_user_id, email, username, phone, role_id)
VALUES
(@u1, 'clerk_admin_001',  'admin@company.vn',   'admin',   '0900000001', 1),
(@u2, 'clerk_mgr_001',    'manager@company.vn', 'manager', '0900000002', 2),
(@u3, 'clerk_staff_001',  'staff1@company.vn',  'staff1',  '0900000003', 3),
(@u4, 'clerk_staff_002',  'staff2@company.vn',  'staff2',  '0900000004', 3),
(@u5, 'clerk_account_01', 'acc@company.vn',     'account', '0900000005', 4);

/* =========================
   4. USER - GROUP
   ========================= */
INSERT INTO user_group_members VALUES
(@u1, 5),
(@u2, 1),
(@u3, 1),
(@u4, 2),
(@u5, 4);

/* =========================
   5. WAREHOUSES
   ========================= */
INSERT INTO warehouses (code, name, location) VALUES
('WH-HCM', N'Kho HCM', N'TP Hồ Chí Minh'),
('WH-HN',  N'Kho HN',  N'Hà Nội'),
('WH-DN',  N'Kho ĐN',  N'Đà Nẵng'),
('WH-BD',  N'Kho BD',  N'Bình Dương'),
('WH-CT',  N'Kho CT',  N'Cần Thơ');

/* =========================
   6. CATEGORIES
   ========================= */
INSERT INTO categories (name) VALUES
(N'Điện tử'),
(N'Gia dụng'),
(N'Thực phẩm'),
(N'Văn phòng phẩm'),
(N'Khác');

/* =========================
   7. PRODUCTS
   ========================= */
INSERT INTO products (code, name, category_id, unit, price_in, price_out) VALUES
('P001', N'Chuột Logitech', 1, N'Cái', 200000, 300000),
('P002', N'Bàn phím cơ',    1, N'Cái', 700000, 900000),
('P003', N'Mì gói',         3, N'Thùng', 90000, 120000),
('P004', N'Giấy A4',        4, N'Ream', 50000, 75000),
('P005', N'Bình nước',      2, N'Cái', 80000, 120000);

/* =========================
   8. SUPPLIERS
   ========================= */
INSERT INTO suppliers (name, phone, email) VALUES
(N'Cty Thiết Bị Số', '0281111111', 'tbso@sup.vn'),
(N'Cty Văn Phòng',   '0282222222', 'vpp@sup.vn'),
(N'Cty Thực Phẩm',   '0283333333', 'food@sup.vn'),
(N'Cty Gia Dụng',    '0284444444', 'giadung@sup.vn'),
(N'Nhà phân phối X', '0285555555', 'npdx@sup.vn');

/* =========================
   9. INVENTORY
   ========================= */
INSERT INTO inventory VALUES
(1, 1, 50),
(2, 1, 30),
(3, 1, 100),
(4, 2, 200),
(5, 3, 40);

/* =========================
   10. STOCK IN
   ========================= */
INSERT INTO stock_in (supplier_id, warehouse_id, created_by, note) VALUES
(1, 1, @u2, N'Nhập điện tử'),
(2, 1, @u2, N'Nhập VPP'),
(3, 2, @u3, N'Nhập thực phẩm'),
(4, 3, @u3, N'Nhập gia dụng'),
(5, 1, @u4, N'Nhập khác');

INSERT INTO stock_in_items (stock_in_id, product_id, quantity, price) VALUES
(1, 1, 10, 200000),
(1, 2, 5, 700000),
(2, 4, 50, 50000),
(3, 3, 20, 90000),
(4, 5, 10, 80000);

/* =========================
   11. STOCK OUT
   ========================= */
INSERT INTO stock_out (warehouse_id, created_by, reason, note) VALUES
(1, @u3, N'SALE',     N'Bán hàng'),
(1, @u3, N'INTERNAL', N'Xuất nội bộ'),
(2, @u4, N'SALE',     N'Bán lẻ'),
(3, @u4, N'DAMAGED',  N'Hỏng'),
(1, @u2, N'SALE',     N'Bán sỉ');

INSERT INTO stock_out_items (stock_out_id, product_id, quantity) VALUES
(1, 1, 2),
(2, 2, 1),
(3, 3, 5),
(4, 5, 1),
(5, 4, 10);

/* =========================
   12. PROMOTIONS
   ========================= */
INSERT INTO promotions (code, name, discount_type, discount_value, start_date, end_date) VALUES
('KM10', N'Giảm 10%', 'PERCENT', 10, GETDATE(), DATEADD(DAY,30,GETDATE())),
('KM50K',N'Giảm 50K', 'FIXED',   50000, GETDATE(), DATEADD(DAY,15,GETDATE())),
('SALE1',N'Sale tháng', 'PERCENT', 15, GETDATE(), DATEADD(DAY,10,GETDATE())),
('NEW',  N'Hàng mới', 'PERCENT', 5,  GETDATE(), DATEADD(DAY,7,GETDATE())),
('VIP',  N'Khách VIP','FIXED',   100000, GETDATE(), DATEADD(DAY,60,GETDATE()));

INSERT INTO promotion_products VALUES
(1,1),
(1,2),
(2,4),
(3,3),
(4,5);

/* =========================
   13. STOCK AUDIT
   ========================= */
INSERT INTO stock_audit (warehouse_id, product_id, system_quantity, actual_quantity, created_by) VALUES
(1, 1, 50, 48, @u3),
(1, 2, 30, 30, @u3),
(2, 3, 100, 95, @u4),
(3, 5, 40, 39, @u4),
(1, 4, 200, 200, @u2);

/* =========================
   14. AUDIT LOGS
   ========================= */
INSERT INTO audit_logs (entity, entity_id, action, performed_by) VALUES
('PRODUCT', 'P001', 'CREATE', @u1),
('STOCK_IN', '1',   'CREATE', @u2),
('STOCK_OUT','1',   'CREATE', @u3),
('PROMOTION','KM10','CREATE', @u1),
('INVENTORY','1-1', 'UPDATE', @u2);
