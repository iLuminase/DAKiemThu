# PowerShell script để setup database trong MSSQL Docker container

Write-Host "⏳ Đang chờ MSSQL container sẵn sàng..." -ForegroundColor Yellow
Start-Sleep -Seconds 20

Write-Host "📦 Đang tạo database và user..." -ForegroundColor Cyan

$sqlCommand = @"
USE master;

-- Tạo database
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

-- Tạo login
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

-- Tạo user
IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = 'dev_user')
BEGIN
    CREATE USER dev_user FOR LOGIN dev_user;
    PRINT 'User dev_user đã được tạo';
END
GO

-- Cấp quyền
ALTER ROLE db_owner ADD MEMBER dev_user;
GO

SELECT 'Database setup completed!' AS Status;
"@

docker exec -i inventory-mssql /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P "YourStrong@Passw0rd" -Q $sqlCommand

Write-Host "✅ Database setup hoàn tất!" -ForegroundColor Green
Write-Host "🚀 Hibernate sẽ tự động tạo tables khi chạy Spring Boot application" -ForegroundColor Green
