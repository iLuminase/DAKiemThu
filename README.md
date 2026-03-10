# 🏭 Hệ thống Quản lý Kho - Inventory Management System

Hệ thống quản lý kho hàng cho doanh nghiệp, xây dựng với Spring Boot + MSSQL + Docker.

## ✨ Tính năng chính

- **Quản lý sản phẩm** - Products, Categories, Suppliers
- **Quản lý kho** - Multi-warehouse, Stock In/Out, Inventory tracking
- **Phân quyền** - ADMIN / MANAGER / STAFF với warehouse scope
- **Real-time** - WebSocket updates khi nhập/xuất kho
- **Audit log** - Theo dõi mọi thao tác

## 🛠️ Tech Stack

- **Backend:** Java 21, Spring Boot 4.0.2, JPA/Hibernate
- **Database:** Microsoft SQL Server 2025 (Docker)
- **Auth:** Clerk Authentication
- **Real-time:** WebSocket (STOMP)

## 🚀 Khởi chạy nhanh (Windows)

### Yêu cầu
- Docker Desktop
- Java 21+
- Maven 3.8+

### Bước 1: Clone project
```powershell
git clone <repository-url>
cd DAKiemThu
```

### Bước 2: Chạy database
```powershell
docker-compose up -d
```

### Bước 3: Setup database
```powershell
cd database
.\docker-setup.ps1
```

### Bước 4: Chạy backend
```powershell
cd backend\inventory
.\mvnw.cmd spring-boot:run
```

✅ **API:** `http://localhost:8080/api`

## 📡 API Endpoints

**Authentication:** Tất cả API cần header `X-Clerk-User-Id`

```
GET/POST/PUT/DELETE  /products      # Sản phẩm
GET/POST/PUT/DELETE  /categories    # Danh mục
GET/POST/PUT/DELETE  /warehouses    # Kho
GET/POST/PUT/DELETE  /suppliers     # Nhà cung cấp
GET                  /inventory     # Tồn kho
POST                 /stock-in      # Nhập kho
POST                 /stock-out     # Xuất kho
GET/POST/PUT/DELETE  /users         # User (ADMIN only)
GET                  /audit-logs    # Lịch sử (ADMIN only)
```

## 🔐 Phân quyền

| Role | Quyền hạn |
|------|-----------|
| ADMIN | Full access toàn hệ thống |
| MANAGER | Quản lý các kho được assign |
| STAFF | Chỉ xem kho được assign |

## 📖 Tài liệu

- **[DOCKER_SETUP.md](DOCKER_SETUP.md)** - Hướng dẫn Docker chi tiết
- **API Test:** Dùng Postman với header `X-Clerk-User-Id: <your_clerk_id>`

## 🗂️ Cấu trúc

```
DAKiemThu/
├── backend/inventory/     # Spring Boot backend
├── database/              # SQL scripts & setup
├── docker-compose.yml     # Docker config
└── README.md
```

## 📝 License

MIT License - Free to use
