# Hệ thống Quản lý Kho - Backend Setup Guide

## 🚀 Yêu cầu hệ thống

- Java 21+
- Maven 3.8+
- SQL Server 2019+
- Clerk Account (cho authentication)

## 📋 Cấu hình Database

### Option 1: Tự động tạo database (Khuyến nghị)

**Bước 1:** Chạy SQL script để tạo database và user:

```bash
# Kết nối SQL Server với quyền sa/admin và chạy:
sqlcmd -S localhost -U sa -P YourPassword -i database/init-database.sql
```

Hoặc mở file `database/init-database.sql` trong SQL Server Management Studio và execute.

**Bước 2:** Application sẽ tự động tạo tất cả tables khi chạy lần đầu (Hibernate ddl-auto: update)

### Option 2: Tạo database thủ công

Chạy các scripts theo thứ tự:

```bash
database/step-1-create-db.sql      # Tạo database và tables
database/step-2-trigger.sql        # Tạo triggers
database/step-3-db-login.sql       # Tạo login và user
database/step-4-proc.sql           # Tạo stored procedures (optional)
database/step-5-data-set.sql       # Insert sample data
```

## ⚙️ Cấu hình Application

### 1. Database Connection

File: `src/main/resources/application.yaml`

```yaml
spring:
  datasource:
    url: jdbc:sqlserver://localhost:1433;databaseName=InventoryManagement
    username: dev_user
    password: Dev@123456
```

📝 **Lưu ý:** 
- Hibernate sẽ tự động tạo/cập nhật tables với `ddl-auto: update`
- Database phải được tạo trước (dùng init-database.sql)

### 2. Clerk Authentication

Set environment variable:

**Windows PowerShell:**
```powershell
$env:CLERK_SECRET_KEY="sk_test_your_secret_key"
```

**Linux/Mac:**
```bash
export CLERK_SECRET_KEY="sk_test_your_secret_key"
```

Hoặc sửa trực tiếp trong `application.yaml`:
```yaml
clerk:
  secret-key: sk_test_your_actual_secret_key
```

## 🏃 Chạy ứng dụng

### Development Mode

```bash
cd backend/inventory

# Compile
./mvnw clean compile

# Run
./mvnw spring-boot:run
```

Application sẽ chạy tại: `http://localhost:8080/api`

### Production Build

```bash
./mvnw clean package
java -jar target/inventory-0.0.1-SNAPSHOT.war
```

## 📡 API Documentation

### Base URL
```
http://localhost:8080/api
```

### Authentication Header
Tất cả API yêu cầu header:
```
X-Clerk-User-Id: <clerk_user_id_from_frontend>
```

### Main Endpoints

#### Products
- `GET /products` - Danh sách sản phẩm
- `POST /products` - Tạo sản phẩm mới
- `PUT /products/{id}` - Cập nhật sản phẩm
- `DELETE /products/{id}` - Xóa sản phẩm
- `GET /products/search?keyword=...` - Tìm kiếm

#### Inventory
- `GET /inventory` - Tồn kho tổng quan
- `GET /inventory/warehouse/{id}` - Tồn kho theo kho
- `GET /inventory/low-stock` - Hàng sắp hết

#### Stock In (Nhập kho)
- `POST /stock-in` - Tạo phiếu nhập
- `GET /stock-in` - Danh sách phiếu nhập

#### Stock Out (Xuất kho)
- `POST /stock-out` - Tạo phiếu xuất
- `GET /stock-out` - Danh sách phiếu xuất

#### Categories
- `GET /categories` - Danh sách danh mục
- `POST /categories` - Tạo danh mục

#### Warehouses
- `GET /warehouses` - Danh sách kho
- `POST /warehouses` - Tạo kho mới

#### Suppliers
- `GET /suppliers` - Danh sách nhà cung cấp
- `POST /suppliers` - Tạo NCC mới

#### Users (Admin only)
- `GET /users` - Danh sách users
- `POST /users` - Tạo user
- `GET /users/me` - Thông tin user hiện tại

#### Audit Logs (Admin only)
- `GET /audit-logs` - Lịch sử thao tác
- `GET /audit-logs/recent` - Log gần nhất

## 🔌 WebSocket (Real-time Updates)

### Connect
```javascript
const socket = new SockJS('http://localhost:8080/api/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function(frame) {
    // Subscribe to stock updates
    stompClient.subscribe('/topic/stock-updates', function(message) {
        const update = JSON.parse(message.body);
        console.log('Stock updated:', update);
    });
    
    // Subscribe to specific warehouse
    stompClient.subscribe('/topic/stock-updates/1', function(message) {
        console.log('Warehouse 1 stock updated');
    });
});
```

### Message Format
```json
{
  "warehouseId": "1",
  "productId": "5",
  "newQuantity": 100,
  "updateType": "IMPORT",
  "timestamp": "2026-03-10T14:30:00"
}
```

## 🔐 Phân quyền

### ADMIN
- Toàn quyền trên hệ thống
- Quản lý users, xem audit logs

### MANAGER
- Quản lý nhiều kho được assign
- Nhập/xuất kho, xem báo cáo

### STAFF
- Chỉ xem/thao tác dữ liệu kho được assign
- Không có quyền tạo/xóa master data

## 🗄️ Database Schema

### Core Tables
- `users` - Người dùng (UUID primary key)
- `roles` - Vai trò (ADMIN, MANAGER, STAFF)
- `user_groups` - Nhóm người dùng
- `user_group_members` - User ↔ Group mapping
- `warehouses` - Kho hàng
- `warehouse_groups` - Group ↔ Warehouse mapping
- `products` - Sản phẩm
- `categories` - Danh mục sản phẩm
- `suppliers` - Nhà cung cấp
- `inventory` - Tồn kho (composite key)
- `stock_in` / `stock_in_items` - Phiếu nhập
- `stock_out` / `stock_out_items` - Phiếu xuất
- `audit_log` - Lịch sử thao tác
- `promotions` / `product_promotions` - Khuyến mãi

## 🐛 Troubleshooting

### Database Connection Failed
✅ Kiểm tra SQL Server đang chạy  
✅ Xác nhận username/password trong application.yaml  
✅ Chạy `database/init-database.sql` để tạo database và user

### Tables không tự động tạo
✅ Xác nhận `hibernate.ddl-auto: update` trong application.yaml  
✅ Database phải tồn tại trước  
✅ User phải có quyền CREATE TABLE

### Clerk Authentication Error
✅ Set environment variable `CLERK_SECRET_KEY`  
✅ Frontend phải gửi header `X-Clerk-User-Id`  
✅ User phải tồn tại trong bảng `users` với `clerk_user_id` tương ứng

### Port 8080 đã được sử dụng
Sửa port trong `application.yaml`:
```yaml
server:
  port: 8090
```

## 📝 Sample Request

### Tạo phiếu nhập kho

```bash
curl -X POST http://localhost:8080/api/stock-in \
  -H "Content-Type: application/json" \
  -H "X-Clerk-User-Id: user_abc123" \
  -d '{
    "supplierId": 1,
    "warehouseId": 1,
    "note": "Nhập hàng từ NCC A",
    "items": [
      {
        "productId": 5,
        "quantity": 100,
        "price": 50000
      }
    ]
  }'
```

### Response
```json
{
  "success": true,
  "message": "Nhập kho thành công",
  "data": {
    "id": 1,
    "supplierId": 1,
    "warehouseId": 1,
    "createdBy": "uuid-here",
    "note": "Nhập hàng từ NCC A",
    "items": [...],
    "createdAt": "2026-03-10T14:30:00"
  }
}
```

## 🎯 Next Steps

1. ✅ Setup database và chạy init script
2. ✅ Cấu hình Clerk secret key
3. ✅ Chạy application
4. ⬜ Tạo sample data (roles, users, warehouses)
5. ⬜ Test API endpoints với Postman/Thunder Client
6. ⬜ Implement frontend

---

**Tech Stack:** Spring Boot 4.0.2 | Java 21 | MSSQL | Hibernate | WebSocket | Clerk Auth
