# IE103 Frontend

Frontend cho hệ thống quản lý kho IE103. Ứng dụng dùng React, TypeScript và Ant Design để quản lý sản phẩm, đối tác, nhân viên, nhập kho, xuất kho, kiểm kê, dashboard và lịch sử đơn.

## Công nghệ

- React 19
- TypeScript
- React Router
- Ant Design
- Axios
- Create React App / React Scripts

## Cấu hình môi trường

File `.env` local nằm trong thư mục `Frontend`.

```properties
PORT=3000
REACT_APP_API_BASE_URL=http://localhost:8080
REACT_APP_NAME=IE103
REACT_APP_ENV=local
```

Frontend sẽ gọi API theo dạng:

```text
${REACT_APP_API_BASE_URL}/api
```

Với cấu hình mặc định, API backend là:

```text
http://localhost:8080/api
```

## Cài đặt và chạy

```bash
cd Frontend
npm install
npm start
```

Frontend mặc định chạy tại:

```text
http://localhost:3000
```

## Lệnh thường dùng

```bash
npm start
npm run build
npm test
npm run lint
npm run lint:fix
```

## Các màn hình chính

| Đường dẫn | Chức năng |
| --- | --- |
| `/login` | Đăng nhập và đăng ký tài khoản |
| `/warehouse/dashboard` | Tổng quan kho và báo cáo |
| `/warehouse/categories` | Quản lý loại sản phẩm |
| `/warehouse/products` | Quản lý sản phẩm |
| `/warehouse/partners` | Quản lý đối tác |
| `/warehouse/employees` | Quản lý nhân viên |
| `/warehouse/inbound` | Lập phiếu nhập và xem chi tiết phiếu nhập |
| `/warehouse/outbound` | Lập phiếu xuất và xem chi tiết phiếu xuất |
| `/warehouse/stocktake` | Lập, duyệt và xem chi tiết phiếu kiểm kê |
| `/warehouse/history` | Xem lịch sử nhập/xuất và chi tiết từng đơn |

## Phân quyền giao diện

Ứng dụng dùng loại nhân viên để giới hạn một số màn:

| Loại | Vai trò |
| --- | --- |
| `0` | Quản lý kho |
| `1` | Nhân viên nhập kho |
| `2` | Nhân viên xuất kho |

Một số route như nhân viên, nhập kho và xuất kho được bọc bởi `RequireRole`.

## Build production

```bash
npm run build
```

Kết quả build nằm trong:

```text
Frontend/build
```

## Lưu ý khi chạy local

- Chạy backend trước tại `http://localhost:8080`.
- Nếu báo port `3000` đã được dùng:

```bash
lsof -nP -iTCP:3000 -sTCP:LISTEN
```

- Nếu đổi backend sang port khác, cập nhật `REACT_APP_API_BASE_URL` trong `Frontend/.env`, sau đó restart frontend.
