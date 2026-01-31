/* =====================================================
   DATABASE: INVENTORY MANAGEMENT SYSTEM
   Tech: MSSQL
   ===================================================== */

-- 1. CREATE DATABASE
CREATE DATABASE InventoryManagement;
GO

USE InventoryManagement;
GO

SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
GO

/* =====================================================
   2. AUTH & USER MANAGEMENT
   ===================================================== */

CREATE TABLE roles (
    id INT IDENTITY PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,       -- ADMIN, MANAGER, STAFF
    name NVARCHAR(100),
    description NVARCHAR(255),
    created_at DATETIME2 DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NULL
);

CREATE TABLE user_groups (
    id INT IDENTITY PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,       -- WH_HCM, WH_HN
    name NVARCHAR(100),
    description NVARCHAR(255),
    created_at DATETIME2 DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NULL
);

CREATE TABLE users (
    id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    clerk_user_id VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255),
    username VARCHAR(100),
    phone VARCHAR(20),
    role_id INT NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at DATETIME2 DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NULL,
    CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE user_group_members (
    user_id UNIQUEIDENTIFIER NOT NULL,
    group_id INT NOT NULL,
    PRIMARY KEY (user_id, group_id),
    CONSTRAINT fk_ug_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_ug_group FOREIGN KEY (group_id) REFERENCES user_groups(id)
);

/* =====================================================
   3. MASTER DATA
   ===================================================== */

CREATE TABLE warehouses (
    id INT IDENTITY PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name NVARCHAR(255),
    location NVARCHAR(255),
    created_at DATETIME2 DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NULL
);

CREATE TABLE categories (
    id INT IDENTITY PRIMARY KEY,
    name NVARCHAR(255),
    slug VARCHAR(255) UNIQUE,               -- SEO
    description NVARCHAR(255),
    created_at DATETIME2 DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NULL
);

CREATE TABLE products (
    id INT IDENTITY PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name NVARCHAR(255) NOT NULL,
    slug VARCHAR(255) UNIQUE NOT NULL,      -- SEO URL
    image_url VARCHAR(500),                 -- Image product
    category_id INT,
    unit NVARCHAR(50),
    price_in DECIMAL(18,2),
    price_out DECIMAL(18,2),
    is_active BIT DEFAULT 1,
    created_at DATETIME2 DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NULL,
    CONSTRAINT fk_products_category FOREIGN KEY (category_id)
        REFERENCES categories(id)
);

CREATE TABLE suppliers (
    id INT IDENTITY PRIMARY KEY,
    name NVARCHAR(255),
    phone VARCHAR(20),
    email VARCHAR(255),
    address NVARCHAR(255),
    created_at DATETIME2 DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NULL
);

/* =====================================================
   4. INVENTORY
   ===================================================== */

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

/* =====================================================
   5. STOCK IN
   ===================================================== */

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

/* =====================================================
   6. STOCK OUT
   ===================================================== */

CREATE TABLE stock_out (
    id INT IDENTITY PRIMARY KEY,
    warehouse_id INT NOT NULL,
    created_by UNIQUEIDENTIFIER,
    reason NVARCHAR(100),
    note NVARCHAR(255),
    created_at DATETIME2 DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NULL,
    CONSTRAINT fk_stock_out_warehouse FOREIGN KEY (warehouse_id)
        REFERENCES warehouses(id),
    CONSTRAINT fk_stock_out_user FOREIGN KEY (created_by)
        REFERENCES users(id)
);

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

/* =====================================================
   7. PROMOTION
   ===================================================== */

CREATE TABLE promotions (
    id INT IDENTITY PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name NVARCHAR(255),
    discount_type VARCHAR(20),              -- PERCENT / FIXED
    discount_value DECIMAL(18,2),
    start_date DATETIME2,
    end_date DATETIME2,
    is_active BIT DEFAULT 1,
    created_at DATETIME2 DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NULL
);

CREATE TABLE promotion_products (
    promotion_id INT NOT NULL,
    product_id INT NOT NULL,
    PRIMARY KEY (promotion_id, product_id),
    CONSTRAINT fk_pp_promotion FOREIGN KEY (promotion_id)
        REFERENCES promotions(id),
    CONSTRAINT fk_pp_product FOREIGN KEY (product_id)
        REFERENCES products(id)
);

/* =====================================================
   8. AUDIT LOG
   ===================================================== */

CREATE TABLE audit_logs (
    id BIGINT IDENTITY PRIMARY KEY,
    entity NVARCHAR(100),
    entity_id NVARCHAR(100),
    action NVARCHAR(50),
    performed_by UNIQUEIDENTIFIER,
    performed_at DATETIME2 DEFAULT SYSDATETIME()
);
