/* =========================================================
   INVENTORY MANAGEMENT SYSTEM - MSSQL
   Author: Phatdevio
   Notes:
   - User ID: UUID (UNIQUEIDENTIFIER)
   - Auth: Clerk (external auth)
   - RBAC: Role + Group
   - Audit: created_at, updated_at
   ========================================================= */
CREATE DATABASE InventoryManagementDB;
GO
USE InventoryManagementDB;
GO
SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
GO

/* =========================
   1. ROLES
   ========================= */
CREATE TABLE roles (
    id INT IDENTITY PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,       -- ADMIN, MANAGER, STAFF
    name NVARCHAR(100),
    description NVARCHAR(255),
    created_at DATETIME2 DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NULL
);
GO

/* =========================
   2. USER GROUPS
   ========================= */
CREATE TABLE user_groups (
    id INT IDENTITY PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,       -- WAREHOUSE_A, SALE_TEAM
    name NVARCHAR(100),
    description NVARCHAR(255),
    created_at DATETIME2 DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NULL
);
GO

/* =========================
   3. USERS (CLERK AUTH)
   ========================= */
CREATE TABLE users (
    id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),

    clerk_user_id VARCHAR(255) UNIQUE NOT NULL, -- ID từ Clerk
    email VARCHAR(255),
    username VARCHAR(100),
    phone VARCHAR(20),

    role_id INT NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',

    created_at DATETIME2 DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NULL,

    CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles(id)
);
GO

/* =========================
   4. USER - GROUP (N:N)
   ========================= */
CREATE TABLE user_group_members (
    user_id UNIQUEIDENTIFIER NOT NULL,
    group_id INT NOT NULL,

    PRIMARY KEY (user_id, group_id),
    CONSTRAINT fk_ugm_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_ugm_group FOREIGN KEY (group_id) REFERENCES user_groups(id)
);
GO

/* =========================
   5. WAREHOUSES
   ========================= */
CREATE TABLE warehouses (
    id INT IDENTITY PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name NVARCHAR(255),
    location NVARCHAR(255),
    created_at DATETIME2 DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NULL
);
GO

/* =========================
   6. CATEGORIES
   ========================= */
CREATE TABLE categories (
    id INT IDENTITY PRIMARY KEY,
    name NVARCHAR(255),
    description NVARCHAR(255),
    created_at DATETIME2 DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NULL
);
GO

/* =========================
   7. PRODUCTS
   ========================= */
CREATE TABLE products (
    id INT IDENTITY PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name NVARCHAR(255),
    category_id INT,
    unit NVARCHAR(50),

    price_in DECIMAL(18,2),
    price_out DECIMAL(18,2),

    created_at DATETIME2 DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NULL,

    CONSTRAINT fk_products_category FOREIGN KEY (category_id)
        REFERENCES categories(id)
);
GO

/* =========================
   8. SUPPLIERS
   ========================= */
CREATE TABLE suppliers (
    id INT IDENTITY PRIMARY KEY,
    name NVARCHAR(255),
    phone VARCHAR(20),
    email VARCHAR(255),
    address NVARCHAR(255),
    created_at DATETIME2 DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NULL
);
GO

/* =========================
   9. INVENTORY (PRODUCT x WAREHOUSE)
   ========================= */
CREATE TABLE inventory (
    product_id INT NOT NULL,
    warehouse_id INT NOT NULL,
    quantity INT DEFAULT 0,

    PRIMARY KEY (product_id, warehouse_id),
    CONSTRAINT fk_inventory_product FOREIGN KEY (product_id)
        REFERENCES products(id),
    CONSTRAINT fk_inventory_warehouse FOREIGN KEY (warehouse_id)
        REFERENCES warehouses(id)
);
GO

/* =========================
   10. STOCK IN
   ========================= */
CREATE TABLE stock_in (
    id INT IDENTITY PRIMARY KEY,
    supplier_id INT,
    warehouse_id INT,
    created_by UNIQUEIDENTIFIER,

    note NVARCHAR(255),
    created_at DATETIME2 DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NULL,

    CONSTRAINT fk_stock_in_supplier FOREIGN KEY (supplier_id)
        REFERENCES suppliers(id),
    CONSTRAINT fk_stock_in_warehouse FOREIGN KEY (warehouse_id)
        REFERENCES warehouses(id),
    CONSTRAINT fk_stock_in_user FOREIGN KEY (created_by)
        REFERENCES users(id)
);
GO

CREATE TABLE stock_in_items (
    id INT IDENTITY PRIMARY KEY,
    stock_in_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(18,2),

    CONSTRAINT fk_sii_stock_in FOREIGN KEY (stock_in_id)
        REFERENCES stock_in(id),
    CONSTRAINT fk_sii_product FOREIGN KEY (product_id)
        REFERENCES products(id)
);
GO

/* =========================
   11. STOCK OUT
   ========================= */
CREATE TABLE stock_out (
    id INT IDENTITY PRIMARY KEY,
    warehouse_id INT NOT NULL,
    created_by UNIQUEIDENTIFIER,
    reason NVARCHAR(100), -- SALE, INTERNAL, DAMAGED
    note NVARCHAR(255),

    created_at DATETIME2 DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NULL,

    CONSTRAINT fk_stock_out_warehouse FOREIGN KEY (warehouse_id)
        REFERENCES warehouses(id),
    CONSTRAINT fk_stock_out_user FOREIGN KEY (created_by)
        REFERENCES users(id)
);
GO

CREATE TABLE stock_out_items (
    id INT IDENTITY PRIMARY KEY,
    stock_out_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,

    CONSTRAINT fk_soi_stock_out FOREIGN KEY (stock_out_id)
        REFERENCES stock_out(id),
    CONSTRAINT fk_soi_product FOREIGN KEY (product_id)
        REFERENCES products(id)
);
GO

/* =========================
   12. PROMOTIONS
   ========================= */
CREATE TABLE promotions (
    id INT IDENTITY PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name NVARCHAR(255),

    discount_type VARCHAR(20),  -- PERCENT, FIXED
    discount_value DECIMAL(18,2),

    start_date DATETIME2,
    end_date DATETIME2,
    is_active BIT DEFAULT 1,

    created_at DATETIME2 DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NULL
);
GO

CREATE TABLE promotion_products (
    promotion_id INT NOT NULL,
    product_id INT NOT NULL,

    PRIMARY KEY (promotion_id, product_id),
    CONSTRAINT fk_pp_promotion FOREIGN KEY (promotion_id)
        REFERENCES promotions(id),
    CONSTRAINT fk_pp_product FOREIGN KEY (product_id)
        REFERENCES products(id)
);
GO

/* =========================
   13. STOCK AUDIT (INVENTORY CHECK)
   ========================= */
CREATE TABLE stock_audit (
    id INT IDENTITY PRIMARY KEY,
    warehouse_id INT NOT NULL,
    product_id INT NOT NULL,

    system_quantity INT,
    actual_quantity INT,
    difference AS (actual_quantity - system_quantity),

    created_by UNIQUEIDENTIFIER,
    created_at DATETIME2 DEFAULT SYSDATETIME(),

    CONSTRAINT fk_audit_warehouse FOREIGN KEY (warehouse_id)
        REFERENCES warehouses(id),
    CONSTRAINT fk_audit_product FOREIGN KEY (product_id)
        REFERENCES products(id),
    CONSTRAINT fk_audit_user FOREIGN KEY (created_by)
        REFERENCES users(id)
);
GO

/* =========================
   14. AUDIT LOGS
   ========================= */
CREATE TABLE audit_logs (
    id BIGINT IDENTITY PRIMARY KEY,
    entity NVARCHAR(100),
    entity_id NVARCHAR(100),
    action NVARCHAR(50),       -- CREATE, UPDATE, DELETE
    performed_by UNIQUEIDENTIFIER,
    performed_at DATETIME2 DEFAULT SYSDATETIME()
);
GO
