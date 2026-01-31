USE InventoryManagement;
GO

/* =====================================================
   1. ROLES
   ===================================================== */
INSERT INTO roles (code, name, description) VALUES
('ADMIN',   N'Quản trị hệ thống', N'Toàn quyền hệ thống'),
('MANAGER', N'Quản lý kho',      N'Quản lý hoạt động kho'),
('STAFF',   N'Nhân viên kho',    N'Thực hiện nhập xuất'),
('ACCOUNT', N'Kế toán',          N'Theo dõi số liệu'),
('VIEWER',  N'Người xem',        N'Chỉ xem báo cáo');

/* =====================================================
   2. USER GROUPS (WAREHOUSE GROUP)
   ===================================================== */
INSERT INTO user_groups (code, name, description) VALUES
('WH_HCM', N'Kho Hồ Chí Minh', N'Kho miền Nam'),
('WH_HN',  N'Kho Hà Nội',      N'Kho miền Bắc'),
('WH_DN',  N'Kho Đà Nẵng',     N'Kho miền Trung'),
('SALE',  N'Bộ phận bán hàng', N'Nhân viên bán'),
('ADMIN', N'Quản trị',         N'Nhóm quản trị');

/* =====================================================
   3. USERS (UUID FIXED)
   ===================================================== */
DECLARE @u1 UNIQUEIDENTIFIER = NEWID();
DECLARE @u2 UNIQUEIDENTIFIER = NEWID();
DECLARE @u3 UNIQUEIDENTIFIER = NEWID();
DECLARE @u4 UNIQUEIDENTIFIER = NEWID();
DECLARE @u5 UNIQUEIDENTIFIER = NEWID();

INSERT INTO users (id, clerk_user_id, email, username, phone, role_id) VALUES
(@u1, 'clerk_admin_01',  'admin@congty.vn',   'admin',   '0909000001', 1),
(@u2, 'clerk_mgr_01',    'quanly@congty.vn',  'quanly',  '0909000002', 2),
(@u3, 'clerk_staff_01',  'khohcm@congty.vn',  'kho_hcm', '0909000003', 3),
(@u4, 'clerk_staff_02',  'khohn@congty.vn',   'kho_hn',  '0909000004', 3),
(@u5, 'clerk_acc_01',    'ketoan@congty.vn',  'ketoan',  '0909000005', 4);

/* =====================================================
   4. USER GROUP MEMBERS
   ===================================================== */
INSERT INTO user_group_members VALUES
(@u1, 5),
(@u2, 1),
(@u3, 1),
(@u4, 2),
(@u5, 5);

/* =====================================================
   5. WAREHOUSES
   ===================================================== */
INSERT INTO warehouses (code, name, location) VALUES
('WH-HCM', N'Kho Hồ Chí Minh', N'Quận 7, TP.HCM'),
('WH-HN',  N'Kho Hà Nội',     N'Cầu Giấy, Hà Nội'),
('WH-DN',  N'Kho Đà Nẵng',    N'Hải Châu, Đà Nẵng'),
('WH-BD',  N'Kho Bình Dương', N'Dĩ An, Bình Dương'),
('WH-CT',  N'Kho Cần Thơ',    N'Ninh Kiều, Cần Thơ');

/* =====================================================
   6. CATEGORIES
   ===================================================== */
INSERT INTO categories (name, slug, description) VALUES
(N'Điện tử',        'dien-tu',        N'Sản phẩm điện tử'),
(N'Gia dụng',       'gia-dung',       N'Đồ gia dụng'),
(N'Văn phòng phẩm', 'van-phong-pham', N'Đồ dùng văn phòng'),
(N'Thực phẩm',      'thuc-pham',      N'Hàng tiêu dùng'),
(N'Khác',           'khac',           N'Khác');

/* =====================================================
   7. PRODUCTS
   ===================================================== */
INSERT INTO products
(code, name, slug, image_url, category_id, unit, price_in, price_out)
VALUES
('P001', N'Chuột Logitech B100', 'chuot-logitech-b100',
 'https://img.demo.vn/chuot.jpg', 1, N'Cái', 180000, 250000),

('P002', N'Bàn phím cơ DareU', 'ban-phim-co-dareu',
 'https://img.demo.vn/banphim.jpg', 1, N'Cái', 650000, 850000),

('P003', N'Giấy A4 Double A', 'giay-a4-double-a',
 'https://img.demo.vn/giaya4.jpg', 3, N'Ream', 48000, 72000),

('P004', N'Mì gói Hảo Hảo', 'mi-goi-hao-hao',
 'https://img.demo.vn/mihaohao.jpg', 4, N'Thùng', 92000, 125000),

('P005', N'Bình nước Lock&Lock', 'binh-nuoc-lock-lock',
 'https://img.demo.vn/binhnuoc.jpg', 2, N'Cái', 85000, 130000);

/* =====================================================
   8. SUPPLIERS
   ===================================================== */
INSERT INTO suppliers (name, phone, email, address) VALUES
(N'Công ty Thiết bị Số', '0281111111', 'tbso@nhacc.vn', N'TP.HCM'),
(N'Công ty Văn phòng An Phát', '0282222222', 'vpp@nhacc.vn', N'Hà Nội'),
(N'Công ty Thực phẩm Acecook', '0283333333', 'food@nhacc.vn', N'Bình Dương'),
(N'Công ty Gia dụng Việt', '0284444444', 'giadung@nhacc.vn', N'Đồng Nai'),
(N'Nhà phân phối Tổng hợp', '0285555555', 'tonghop@nhacc.vn', N'Đà Nẵng');

/* =====================================================
   9. INVENTORY (SỐ LƯỢNG BAN ĐẦU)
   ===================================================== */
INSERT INTO inventory VALUES
(1, 1, 50),
(2, 1, 30),
(3, 2, 200),
(4, 3, 120),
(5, 1, 40);
GO
DROP TRIGGER trg_promotions_updated_at;
DROP TRIGGER trg_promotion_auto_disable;
GO

/* =====================================================
   10. PROMOTIONS
   ===================================================== */
INSERT INTO promotions
(code, name, discount_type, discount_value, start_date, end_date)
VALUES
('KM10',  N'Giảm 10% toàn bộ', 'PERCENT', 10, GETDATE(), DATEADD(DAY, 30, GETDATE())),
('KM50K', N'Giảm 50.000đ',     'FIXED',   50000, GETDATE(), DATEADD(DAY, 15, GETDATE())),
('SALE7', N'Sale cuối tuần',  'PERCENT', 7,  GETDATE(), DATEADD(DAY, 7, GETDATE())),
('NEW5',  N'Hàng mới giảm 5%', 'PERCENT', 5,  GETDATE(), DATEADD(DAY, 10, GETDATE())),
('VIP',   N'Khách VIP',       'FIXED',   100000, GETDATE(), DATEADD(DAY, 60, GETDATE()));

/* =====================================================
   11. PROMOTION - PRODUCTS
   ===================================================== */


/* =====================================================
   END SAMPLE DATA
   ===================================================== */
