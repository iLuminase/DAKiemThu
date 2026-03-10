-- Script tạo database và user cho SQL Server
-- Chạy script này với quyền sa hoặc sysadmin

USE master;
GO

-- Tạo database nếu chưa tồn tại
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'InventoryManagement')
BEGIN
    CREATE DATABASE InventoryManagement;
    PRINT 'Database InventoryManagement đã được tạo';
END
ELSE
BEGIN
    PRINT 'Database InventoryManagement đã tồn tại';
END
GO

USE InventoryManagement;
GO

-- Tạo login nếu chưa tồn tại
IF NOT EXISTS (SELECT * FROM sys.server_principals WHERE name = 'dev_user')
BEGIN
    CREATE LOGIN dev_user WITH PASSWORD = 'Dev@123456', CHECK_POLICY = OFF;
    PRINT 'Login dev_user đã được tạo';
END
ELSE
BEGIN
    PRINT 'Login dev_user đã tồn tại';
END
GO

-- Tạo user trong database
IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = 'dev_user')
BEGIN
    CREATE USER dev_user FOR LOGIN dev_user;
    PRINT 'User dev_user đã được tạo trong database';
END
ELSE
BEGIN
    PRINT 'User dev_user đã tồn tại trong database';
END
GO

-- Cấp quyền cho user
ALTER ROLE db_owner ADD MEMBER dev_user;
GO

PRINT 'Cấu hình database hoàn tất. Hibernate sẽ tự động tạo tables khi chạy application.';
GO
