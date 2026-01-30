USE InventoryManagementDB;
GO
-- Tự động thêm số lượng khi nhập kho
CREATE TRIGGER trg_stock_in_add_inventory
ON stock_in_items
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;

    -- Cộng tồn kho
    MERGE inventory AS target
    USING (
        SELECT 
            si.warehouse_id,
            sii.product_id,
            sii.quantity
        FROM inserted sii
        JOIN stock_in si ON si.id = sii.stock_in_id
    ) AS source
    ON target.product_id = source.product_id
       AND target.warehouse_id = source.warehouse_id

    WHEN MATCHED THEN
        UPDATE SET target.quantity = target.quantity + source.quantity

    WHEN NOT MATCHED THEN
        INSERT (product_id, warehouse_id, quantity)
        VALUES (source.product_id, source.warehouse_id, source.quantity);
END;
GO
-- TRIGGER: XUẤT KHO → TRỪ TỒN + CHẶN XUẤT QUÁ TỒN
CREATE TRIGGER trg_stock_out_sub_inventory
ON stock_out_items
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;

    -- Kiểm tra tồn kho
    IF EXISTS (
        SELECT 1
        FROM inserted soi
        JOIN stock_out so ON so.id = soi.stock_out_id
        JOIN inventory i 
            ON i.product_id = soi.product_id 
           AND i.warehouse_id = so.warehouse_id
        WHERE i.quantity < soi.quantity
    )
    BEGIN
        RAISERROR (N'Số lượng tồn kho không đủ để xuất', 16, 1);
        ROLLBACK TRANSACTION;
        RETURN;
    END;

    -- Trừ tồn kho
    UPDATE i
    SET i.quantity = i.quantity - soi.quantity
    FROM inventory i
    JOIN stock_out so ON so.warehouse_id = i.warehouse_id
    JOIN inserted soi ON soi.product_id = i.product_id
                      AND soi.stock_out_id = so.id;
END;
GO
-- TRIGGER: TỰ DISABLE KHUYẾN MÃI KHI HẾT HẠN
CREATE TRIGGER trg_promotion_auto_disable
ON promotions
AFTER INSERT, UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE promotions
    SET is_active = 0,
        updated_at = SYSDATETIME()
    WHERE end_date < SYSDATETIME()
      AND is_active = 1;
END;
GO
-- SQL Job để tắt khuyến mãi hết hạn hàng ngày
UPDATE promotions
SET is_active = 0,
    updated_at = SYSDATETIME()
WHERE end_date < SYSDATETIME()
  AND is_active = 1;
GO
-- Test nhập kho
INSERT INTO stock_in_items (stock_in_id, product_id, quantity, price)
VALUES (1, 1, 100, 200000);
GO
-- test xuất kho
INSERT INTO stock_out_items (stock_out_id, product_id, quantity)
VALUES (1, 1, 1000);
GO  