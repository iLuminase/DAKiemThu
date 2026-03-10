-- Sample data để test hệ thống
USE InventoryManagement;
GO

-- 1. Insert Roles
INSERT INTO roles (code, name, description) VALUES 
('ADMIN', N'Quản trị viên', N'Toàn quyền hệ thống'),
('MANAGER', N'Quản lý', N'Quản lý nhiều kho'),
('STAFF', N'Nhân viên', N'Nhân viên kho');
GO

-- 2. Insert User Groups
INSERT INTO user_groups (code, name, description) VALUES 
('WH_HCM', N'Nhóm Kho HCM', N'Nhân viên kho TP.HCM'),
('WH_HN', N'Nhóm Kho HN', N'Nhân viên kho Hà Nội'),
('WH_DN', N'Nhóm Kho DN', N'Nhân viên kho Đà Nẵng'),
('ALL_WH', N'Tất cả kho', N'Quyền truy cập tất cả kho');
GO

-- 3. Insert Warehouses
INSERT INTO warehouses (code, name, location) VALUES 
('WH001', N'Kho HCM - Quận 1', N'123 Nguyễn Huệ, Q1, TP.HCM'),
('WH002', N'Kho HCM - Quận 7', N'456 Nguyễn Văn Linh, Q7, TP.HCM'),
('WH003', N'Kho Hà Nội', N'789 Hoàn Kiếm, Hà Nội'),
('WH004', N'Kho Đà Nẵng', N'321 Trần Phú, Đà Nẵng');
GO

-- 4. Map Warehouses to Groups
INSERT INTO warehouse_groups (warehouse_id, group_id) VALUES 
(1, 1), (2, 1),  -- Kho HCM thuộc nhóm WH_HCM
(3, 2),          -- Kho HN thuộc nhóm WH_HN
(4, 3),          -- Kho DN thuộc nhóm WH_DN
(1, 4), (2, 4), (3, 4), (4, 4);  -- Nhóm ALL_WH có quyền tất cả kho
GO

-- 5. Insert Sample Users (Clerk User IDs là dummy, cần thay bằng thật từ Clerk)
DECLARE @AdminRoleId INT = (SELECT id FROM roles WHERE code = 'ADMIN');
DECLARE @ManagerRoleId INT = (SELECT id FROM roles WHERE code = 'MANAGER');
DECLARE @StaffRoleId INT = (SELECT id FROM roles WHERE code = 'STAFF');

INSERT INTO users (id, clerk_user_id, email, username, phone, role_id, status) VALUES 
(NEWID(), 'clerk_admin_001', 'admin@inventory.com', 'admin', '0901234567', @AdminRoleId, 'ACTIVE'),
(NEWID(), 'clerk_manager_hcm', 'manager.hcm@inventory.com', 'manager_hcm', '0902345678', @ManagerRoleId, 'ACTIVE'),
(NEWID(), 'clerk_manager_hn', 'manager.hn@inventory.com', 'manager_hn', '0903456789', @ManagerRoleId, 'ACTIVE'),
(NEWID(), 'clerk_staff_hcm_q1', 'staff.hcm.q1@inventory.com', 'staff_hcm_q1', '0904567890', @StaffRoleId, 'ACTIVE'),
(NEWID(), 'clerk_staff_hn', 'staff.hn@inventory.com', 'staff_hn', '0905678901', @StaffRoleId, 'ACTIVE');
GO

-- 6. Assign Users to Groups
DECLARE @AdminUserId UNIQUEIDENTIFIER = (SELECT id FROM users WHERE clerk_user_id = 'clerk_admin_001');
DECLARE @ManagerHcmId UNIQUEIDENTIFIER = (SELECT id FROM users WHERE clerk_user_id = 'clerk_manager_hcm');
DECLARE @ManagerHnId UNIQUEIDENTIFIER = (SELECT id FROM users WHERE clerk_user_id = 'clerk_manager_hn');
DECLARE @StaffHcmQ1Id UNIQUEIDENTIFIER = (SELECT id FROM users WHERE clerk_user_id = 'clerk_staff_hcm_q1');
DECLARE @StaffHnId UNIQUEIDENTIFIER = (SELECT id FROM users WHERE clerk_user_id = 'clerk_staff_hn');

DECLARE @GroupAllWhId INT = (SELECT id FROM user_groups WHERE code = 'ALL_WH');
DECLARE @GroupWhHcmId INT = (SELECT id FROM user_groups WHERE code = 'WH_HCM');
DECLARE @GroupWhHnId INT = (SELECT id FROM user_groups WHERE code = 'WH_HN');

INSERT INTO user_group_members (user_id, group_id) VALUES 
(@AdminUserId, @GroupAllWhId),              -- Admin: all warehouses
(@ManagerHcmId, @GroupWhHcmId),             -- Manager HCM: kho HCM
(@ManagerHnId, @GroupWhHnId),               -- Manager HN: kho HN
(@StaffHcmQ1Id, @GroupWhHcmId),             -- Staff HCM Q1: kho HCM
(@StaffHnId, @GroupWhHnId);                 -- Staff HN: kho HN
GO

-- 7. Insert Categories
INSERT INTO categories (name, slug, description) VALUES 
(N'Điện tử', 'dien-tu', N'Thiết bị điện tử'),
(N'Thời trang', 'thoi-trang', N'Quần áo, phụ kiện'),
(N'Thực phẩm', 'thuc-pham', N'Thực phẩm khô'),
(N'Đồ gia dụng', 'do-gia-dung', N'Dụng cụ nhà bếp, nội thất'),
(N'Sách', 'sach', N'Sách văn học, giáo khoa');
GO

-- 8. Insert Suppliers
INSERT INTO suppliers (name, phone, email, address) VALUES 
(N'Công ty TNHH ABC', '0281234567', 'abc@supplier.com', N'123 Đường A, Q1, TP.HCM'),
(N'Công ty Cổ phần XYZ', '0242345678', 'xyz@supplier.com', N'456 Đường B, Hà Nội'),
(N'Nhà cung cấp 123', '0903456789', 'ncc123@supplier.com', N'789 Đường C, Đà Nẵng');
GO

-- 9. Insert Products
DECLARE @CatElectronics INT = (SELECT id FROM categories WHERE slug = 'dien-tu');
DECLARE @CatFashion INT = (SELECT id FROM categories WHERE slug = 'thoi-trang');
DECLARE @CatFood INT = (SELECT id FROM categories WHERE slug = 'thuc-pham');

INSERT INTO products (code, name, slug, category_id, unit, price_in, price_out, is_active) VALUES 
('PRD001', N'Laptop Dell Inspiron 15', 'laptop-dell-inspiron-15', @CatElectronics, N'Chiếc', 12000000, 15000000, 1),
('PRD002', N'iPhone 15 Pro Max', 'iphone-15-pro-max', @CatElectronics, N'Chiếc', 28000000, 35000000, 1),
('PRD003', N'Áo sơ mi nam', 'ao-so-mi-nam', @CatFashion, N'Cái', 150000, 300000, 1),
('PRD004', N'Quần jeans nữ', 'quan-jeans-nu', @CatFashion, N'Cái', 200000, 400000, 1),
('PRD005', N'Gạo ST25', 'gao-st25', @CatFood, N'Kg', 25000, 35000, 1),
('PRD006', N'Dầu ăn Simply', 'dau-an-simply', @CatFood, N'Chai', 50000, 70000, 1),
('PRD007', N'Tai nghe AirPods Pro', 'tai-nghe-airpods-pro', @CatElectronics, N'Chiếc', 5000000, 6500000, 1),
('PRD008', N'Nồi cơm điện Philips', 'noi-com-dien-philips', @CatElectronics, N'Chiếc', 1200000, 1800000, 1);
GO

-- 10. Initialize Inventory (tồn kho ban đầu = 0)
INSERT INTO inventory (product_id, warehouse_id, quantity)
SELECT p.id, w.id, 0
FROM products p
CROSS JOIN warehouses w;
GO

-- 11. Insert Sample Stock In (Phiếu nhập kho)
DECLARE @Supplier1Id INT = (SELECT TOP 1 id FROM suppliers);
DECLARE @Warehouse1Id INT = (SELECT TOP 1 id FROM warehouses WHERE code = 'WH001');
DECLARE @AdminId UNIQUEIDENTIFIER = (SELECT id FROM users WHERE clerk_user_id = 'clerk_admin_001');

INSERT INTO stock_in (supplier_id, warehouse_id, created_by, note) VALUES 
(@Supplier1Id, @Warehouse1Id, @AdminId, N'Nhập hàng đầu tiên');

DECLARE @StockInId INT = SCOPE_IDENTITY();

-- Insert items và update inventory
DECLARE @Laptop INT = (SELECT id FROM products WHERE code = 'PRD001');
DECLARE @iPhone INT = (SELECT id FROM products WHERE code = 'PRD002');

INSERT INTO stock_in_items (stock_in_id, product_id, quantity, price) VALUES 
(@StockInId, @Laptop, 10, 12000000),
(@StockInId, @iPhone, 5, 28000000);

-- Update inventory
UPDATE inventory SET quantity = quantity + 10 
WHERE product_id = @Laptop AND warehouse_id = @Warehouse1Id;

UPDATE inventory SET quantity = quantity + 5 
WHERE product_id = @iPhone AND warehouse_id = @Warehouse1Id;
GO

PRINT N'✅ Sample data đã được insert thành công!';
PRINT N'';
PRINT N'📝 Thông tin test users:';
PRINT N'1. Admin: clerk_user_id = clerk_admin_001';
PRINT N'2. Manager HCM: clerk_user_id = clerk_manager_hcm';
PRINT N'3. Manager HN: clerk_user_id = clerk_manager_hn';
PRINT N'4. Staff HCM Q1: clerk_user_id = clerk_staff_hcm_q1';
PRINT N'5. Staff HN: clerk_user_id = clerk_staff_hn';
PRINT N'';
PRINT N'⚠️  Lưu ý: Cần thay thế clerk_user_id bằng ID thật từ Clerk khi test!';
GO
