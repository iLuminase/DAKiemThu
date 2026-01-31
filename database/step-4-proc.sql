/* =====================================================
   STORED PROCEDURES - INVENTORY MANAGEMENT SYSTEM
   ===================================================== */

USE InventoryManagement;
GO

SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
GO

/* =====================================================
   1. TYPE: STOCK IN ITEMS
   ===================================================== */
CREATE TYPE tvp_stock_in_items AS TABLE (
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(18,2)
);
GO

/* =====================================================
   2. TYPE: STOCK OUT ITEMS
   ===================================================== */
CREATE TYPE tvp_stock_out_items AS TABLE (
    product_id INT NOT NULL,
    quantity INT NOT NULL
);
GO

/* =====================================================
   3. STORED PROCEDURE: NHẬP KHO
   ===================================================== */
CREATE PROC sp_stock_in
    @supplier_id INT,
    @warehouse_id INT,
    @created_by UNIQUEIDENTIFIER,
    @note NVARCHAR(255),
    @items tvp_stock_in_items READONLY
AS
BEGIN
    SET NOCOUNT ON;

    BEGIN TRY
        BEGIN TRANSACTION;

        DECLARE @stock_in_id INT;

        /* 1. Tạo phiếu nhập kho */
        INSERT INTO stock_in (supplier_id, warehouse_id, created_by, note)
        VALUES (@supplier_id, @warehouse_id, @created_by, @note);

        SET @stock_in_id = SCOPE_IDENTITY();

        /* 2. Thêm chi tiết nhập kho */
        INSERT INTO stock_in_items (stock_in_id, product_id, quantity, price)
        SELECT @stock_in_id, product_id, quantity, price
        FROM @items;

        /* 3. Cộng tồn kho */
        MERGE inventory AS target
        USING (
            SELECT product_id, quantity
            FROM @items
        ) AS source
        ON target.product_id = source.product_id
           AND target.warehouse_id = @warehouse_id

        WHEN MATCHED THEN
            UPDATE SET target.quantity = target.quantity + source.quantity

        WHEN NOT MATCHED THEN
            INSERT (product_id, warehouse_id, quantity)
            VALUES (source.product_id, @warehouse_id, source.quantity);

        /* 4. Audit log */
        INSERT INTO audit_logs (entity, entity_id, action, performed_by)
        VALUES ('STOCK_IN', CAST(@stock_in_id AS NVARCHAR), 'CREATE', @created_by);

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        ROLLBACK TRANSACTION;
        THROW;
    END CATCH
END;
GO

/* =====================================================
   4. STORED PROCEDURE: XUẤT KHO
   ===================================================== */
CREATE PROC sp_stock_out
    @warehouse_id INT,
    @created_by UNIQUEIDENTIFIER,
    @reason NVARCHAR(100),
    @note NVARCHAR(255),
    @items tvp_stock_out_items READONLY
AS
BEGIN
    SET NOCOUNT ON;

    BEGIN TRY
        BEGIN TRANSACTION;

        DECLARE @stock_out_id INT;

        /* 1. Kiểm tra tồn kho */
        IF EXISTS (
            SELECT 1
            FROM @items i
            LEFT JOIN inventory inv
                ON inv.product_id = i.product_id
               AND inv.warehouse_id = @warehouse_id
            WHERE inv.product_id IS NULL
               OR inv.quantity < i.quantity
        )
        BEGIN
            RAISERROR (N'Tồn kho không đủ để xuất', 16, 1);
            ROLLBACK TRANSACTION;
            RETURN;
        END

        /* 2. Tạo phiếu xuất kho */
        INSERT INTO stock_out (warehouse_id, created_by, reason, note)
        VALUES (@warehouse_id, @created_by, @reason, @note);

        SET @stock_out_id = SCOPE_IDENTITY();

        /* 3. Thêm chi tiết xuất kho */
        INSERT INTO stock_out_items (stock_out_id, product_id, quantity)
        SELECT @stock_out_id, product_id, quantity
        FROM @items;

        /* 4. Trừ tồn kho */
        UPDATE inv
        SET inv.quantity = inv.quantity - i.quantity
        FROM inventory inv
        JOIN @items i ON i.product_id = inv.product_id
        WHERE inv.warehouse_id = @warehouse_id;

        /* 5. Audit log */
        INSERT INTO audit_logs (entity, entity_id, action, performed_by)
        VALUES ('STOCK_OUT', CAST(@stock_out_id AS NVARCHAR), 'CREATE', @created_by);

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        ROLLBACK TRANSACTION;
        THROW;
    END CATCH
END;
GO

/* =====================================================
   5. STORED PROCEDURE: ĐIỀU CHỈNH TỒN (KIỂM KÊ)
   ===================================================== */
CREATE PROC sp_adjust_inventory
    @warehouse_id INT,
    @product_id INT,
    @actual_quantity INT,
    @created_by UNIQUEIDENTIFIER,
    @note NVARCHAR(255)
AS
BEGIN
    SET NOCOUNT ON;

    BEGIN TRY
        BEGIN TRANSACTION;

        DECLARE @system_quantity INT;

        SELECT @system_quantity = quantity
        FROM inventory
        WHERE warehouse_id = @warehouse_id
          AND product_id = @product_id;

        IF @system_quantity IS NULL
        BEGIN
            RAISERROR (N'Sản phẩm không tồn tại trong kho', 16, 1);
            ROLLBACK TRANSACTION;
            RETURN;
        END

        UPDATE inventory
        SET quantity = @actual_quantity
        WHERE warehouse_id = @warehouse_id
          AND product_id = @product_id;

        INSERT INTO audit_logs (entity, entity_id, action, performed_by)
        VALUES (
            'INVENTORY_ADJUST',
            CONCAT(@product_id, '-', @warehouse_id),
            'ADJUST',
            @created_by
        );

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        ROLLBACK TRANSACTION;
        THROW;
    END CATCH
END;
GO

/* =====================================================
   END OF STORED PROCEDURES
   ===================================================== */
