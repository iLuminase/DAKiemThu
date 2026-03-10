# 🐳 Docker Setup - Hướng dẫn chạy Database

Hướng dẫn setup MSSQL Server 2025 với Docker cho Windows.

## ⚙️ Yêu cầu

- **Docker Desktop** đã cài đặt và đang chạy
- **PowerShell** (Windows)

## 🚀 Quick Start

### 1. Khởi động MSSQL Container

```powershell
# Tại thư mục root của project
docker-compose up -d
```

Container sẽ tự động:
- Download MSSQL Server 2025
- Khởi động container `inventory-mssql`
- Expose port `1433`
- Tạo volume persistent

### 2. Tạo Database

```powershell
cd database
.\docker-setup.ps1
```

Script sẽ tự động:
- Tạo database `InventoryManagement`
- Tạo roles (ADMIN, MANAGER, STAFF)
- Tạo user admin mẫu

### 3. Verify

```powershell
docker exec -it inventory-mssql /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P admin@123 -C -Q "SELECT name FROM sys.databases"
```

✅ Nếu thấy `InventoryManagement` trong list → Thành công!

## 📝 Thông tin kết nối

| Thông tin | Giá trị |
|-----------|---------|  
| Host | `localhost` |
| Port | `1433` |
| Database | `InventoryManagement` |
| Username | `sa` |
| Password | `admin@123` |

⚠️ **Lưu ý:** Đổi password trong production!

---

## � Các lệnh Docker hữu ích

### Quản lý Container

```powershell
# Dừng container
docker-compose down

# Dừng và xóa volumes (reset toàn bộ)
docker-compose down -v

# Restart container
docker-compose restart

# Xem logs
docker logs inventory-mssql

# Xem logs real-time
docker logs -f inventory-mssql
```

### Kết nối vào Database

```powershell
# Vào sqlcmd shell
docker exec -it inventory-mssql /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P admin@123 -d InventoryManagement -C

# Chạy SQL file
docker exec -it inventory-mssql /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P admin@123 -d InventoryManagement -C -i /path/to/file.sql
```

### Backup & Restore

```powershell
# Backup database
docker exec -it inventory-mssql /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P admin@123 -C -Q "BACKUP DATABASE InventoryManagement TO DISK = '/var/opt/mssql/backup/inventory.bak'"

# Copy backup ra máy
docker cp inventory-mssql:/var/opt/mssql/backup/inventory.bak ./backup/

# Restore database
docker exec -it inventory-mssql /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P admin@123 -C -Q "RESTORE DATABASE InventoryManagement FROM DISK = '/var/opt/mssql/backup/inventory.bak' WITH REPLACE"
```

## ❌ Troubleshooting

### Container không start

```powershell
# Kiểm tra port 1433 đã bị chiếm chưa
netstat -an | findstr 1433

# Xem error logs
docker logs inventory-mssql
```

### Login failed

```powershell
# Reset container với password mới
docker-compose down -v
docker-compose up -d

# Đợi 15s rồi chạy lại setup
Start-Sleep -Seconds 15
cd database
.\docker-setup.ps1
```

### Không thấy database

```powershell
# Kiểm tra container health
docker inspect inventory-mssql --format='{{.State.Health.Status}}'

# Phải là "healthy" mới OK
```

## 🔌 Kết nối từ Tools khác

### Azure Data Studio
1. Connection type: Microsoft SQL Server
2. Server: `localhost,1433`
3. Authentication: SQL Login
4. Username: `sa`
5. Password: `admin@123`
6. Database: `InventoryManagement`

### DBeaver / SSMS
Tương tự như trên, port `1433`

---

## 📊 Sample Data

Nếu muốn insert dữ liệu mẫu:

```powershell
# Copy file vào container
docker cp database/sample-data.sql inventory-mssql:/tmp/

# Execute
docker exec -it inventory-mssql /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P admin@123 -d InventoryManagement -C -i /tmp/sample-data.sql
```

---

## 🎯 Next Steps

Sau khi database đã chạy:

1. **Tạo user Clerk** trên [clerk.com](https://clerk.com)
2. **Insert user vào DB** với Clerk User ID
3. **Chạy backend:** `cd backend\inventory; .\mvnw.cmd spring-boot:run`
4. **Test API:** Postman với header `X-Clerk-User-Id`

---

**Need help?** Check logs: `docker logs inventory-mssql`
