
-- MSSQL TEST ONLY
-- staff login
SELECT * FROM v_staff_products_wh_hcm; -- OK
SELECT * FROM inventory;              -- ❌ Permission denied


-- dev login
SELECT * FROM inventory; -- OK
DELETE FROM products;  