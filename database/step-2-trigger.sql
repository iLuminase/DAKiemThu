/* =====================================================
   TRIGGERS - INVENTORY MANAGEMENT SYSTEM
   LƯU Ý:
   - Không xử lý nhập / xuất kho
   - Nhập / xuất kho dùng Stored Procedure
   ===================================================== */

USE InventoryManagement;
GO

SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
GO

/* =====================================================
   1. AUTO UPDATE updated_at
   ===================================================== */

CREATE TRIGGER trg_roles_updated_at
ON roles
AFTER UPDATE
AS
BEGIN
    UPDATE roles
    SET updated_at = SYSDATETIME()
    WHERE id IN (SELECT id FROM inserted);
END;
GO

CREATE TRIGGER trg_users_updated_at
ON users
AFTER UPDATE
AS
BEGIN
    UPDATE users
    SET updated_at = SYSDATETIME()
    WHERE id IN (SELECT id FROM inserted);
END;
GO

CREATE TRIGGER trg_products_updated_at
ON products
AFTER UPDATE
AS
BEGIN
    UPDATE products
    SET updated_at = SYSDATETIME()
    WHERE id IN (SELECT id FROM inserted);
END;
GO

CREATE TRIGGER trg_categories_updated_at
ON categories
AFTER UPDATE
AS
BEGIN
    UPDATE categories
    SET updated_at = SYSDATETIME()
    WHERE id IN (SELECT id FROM inserted);
END;
GO

CREATE TRIGGER trg_warehouses_updated_at
ON warehouses
AFTER UPDATE
AS
BEGIN
    UPDATE warehouses
    SET updated_at = SYSDATETIME()
    WHERE id IN (SELECT id FROM inserted);
END;
GO

CREATE TRIGGER trg_suppliers_updated_at
ON suppliers
AFTER UPDATE
AS
BEGIN
    UPDATE suppliers
    SET updated_at = SYSDATETIME()
    WHERE id IN (SELECT id FROM inserted);
END;
GO

CREATE TRIGGER trg_promotions_updated_at
ON promotions
AFTER UPDATE
AS
BEGIN
    UPDATE promotions
    SET updated_at = SYSDATETIME()
    WHERE id IN (SELECT id FROM inserted);
END;
GO

/* =====================================================
   2. AUTO DISABLE PROMOTION WHEN EXPIRED
   ===================================================== */

CREATE TRIGGER trg_promotion_auto_disable
ON promotions
AFTER INSERT, UPDATE
AS
BEGIN
    UPDATE promotions
    SET is_active = 0,
        updated_at = SYSDATETIME()
    WHERE end_date < SYSDATETIME()
      AND is_active = 1;
END;
GO

/* =====================================================
   3. AUDIT LOG FOR INVENTORY CHANGE
   (Inventory chỉ thay đổi qua Stored Procedure)
   ===================================================== */

CREATE TRIGGER trg_inventory_audit
ON inventory
AFTER UPDATE
AS
BEGIN
    INSERT INTO audit_logs (entity, entity_id, action, performed_at)
    SELECT
        'INVENTORY',
        CONCAT(i.product_id, '-', i.warehouse_id),
        'UPDATE',
        SYSDATETIME()
    FROM inserted i;
END;
GO

/* =====================================================
   4. AUDIT LOG FOR MASTER DATA
   ===================================================== */

CREATE TRIGGER trg_products_audit
ON products
AFTER INSERT, UPDATE, DELETE
AS
BEGIN
    -- INSERT / UPDATE
    INSERT INTO audit_logs (entity, entity_id, action, performed_at)
    SELECT
        'PRODUCT',
        CAST(id AS NVARCHAR),
        CASE 
            WHEN EXISTS (SELECT 1 FROM deleted WHERE deleted.id = inserted.id)
                THEN 'UPDATE'
            ELSE 'CREATE'
        END,
        SYSDATETIME()
    FROM inserted;

    -- DELETE
    INSERT INTO audit_logs (entity, entity_id, action, performed_at)
    SELECT
        'PRODUCT',
        CAST(id AS NVARCHAR),
        'DELETE',
        SYSDATETIME()
    FROM deleted;
END;
GO

/* =====================================================
   END OF TRIGGERS
   ===================================================== */
