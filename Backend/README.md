# IE103 Backend

Backend cho hệ thống quản lý kho IE103, cung cấp REST API cho sản phẩm, đối tác, nhân viên, nhập kho, xuất kho, kiểm kê, báo cáo và đăng nhập demo.

## Công nghệ

- Java 25
- Spring Boot 4.0.6
- Spring Web MVC
- Spring Data JPA
- Spring Security
- PostgreSQL
- Flyway
- Maven Wrapper

## Cấu hình môi trường

Ứng dụng đọc cấu hình từ `application.properties` và file `.env` nếu có. Tạo file `.env` trong thư mục `Backend` khi chạy local:

```properties
PORT=8080
DATABASE_HOST=localhost
DATABASE_PORT=5432
DATABASE_DB=ie103
DATABASE_USER=postgres
DATABASE_PASSWORD=your_password
DATABASE_SCHEMA=public
```

Nếu dùng đúng giá trị mặc định, database cần có tên `ie103` trên PostgreSQL local.

## Cài đặt và chạy

```bash
cd Backend
./mvnw spring-boot:run
```

Backend mặc định chạy tại:

```text
http://localhost:8080
```

Flyway sẽ kiểm tra và chạy các migration trong:

```text
src/main/resources/db/migration
```

## Lệnh thường dùng

```bash
./mvnw test
./mvnw clean package
./mvnw spring-boot:run
```

## API chính

Tất cả endpoint nghiệp vụ nằm dưới prefix `/api`.

| Nhóm | Endpoint |
| --- | --- |
| Đăng nhập | `POST /api/auth/login`, `POST /api/auth/register`, `GET /api/auth/me` |
| Loại sản phẩm | `/api/loaisp` |
| Sản phẩm | `/api/sanpham` |
| Đối tác | `/api/doitac` |
| Nhân viên | `/api/nhanvien` |
| Phiếu nhập | `/api/phieunhap` |
| Phiếu xuất | `/api/phieuxuat` |
| Phiếu kiểm kê | `/api/phieukiemke` |
| Dashboard | `GET /api/warehouse/dashboard` |
| Báo cáo tồn thấp | `GET /api/reports/low-stock` |
| Báo cáo nhập theo đối tác | `GET /api/reports/import-by-partner` |
| Báo cáo chênh lệch kiểm kê | `GET /api/reports/stocktake-differences` |

Các nhóm CRUD thường hỗ trợ:

```text
GET /
GET /{id}
POST /
PUT /{id}
DELETE /{id}
```

Phiếu nhập, phiếu xuất và phiếu kiểm kê có endpoint xem chi tiết theo mã phiếu:

```text
GET /api/phieunhap/{maPn}
GET /api/phieuxuat/{maPx}
GET /api/phieukiemke/{maPkk}
PATCH /api/phieukiemke/{maPkk}/approve
```

## Lưu ý khi chạy local

- Nếu báo `Port 8080 was already in use`, kiểm tra process đang giữ port:

```bash
lsof -nP -iTCP:8080 -sTCP:LISTEN
```

- Nếu frontend gọi API lỗi, kiểm tra backend còn chạy ở `http://localhost:8080` và file `Frontend/.env` đang trỏ đúng `REACT_APP_API_BASE_URL`.
- PostgreSQL 18 có thể làm Flyway hiện cảnh báo version mới hơn bản hỗ trợ chính thức, nhưng migration vẫn chạy nếu log báo schema đã validate thành công.
