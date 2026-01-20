# Hệ thống Phân Quyền (Role-Based Access Control)

## Giới thiệu
Hệ thống có 2 loại role:
- **USER**: Người dùng thông thường - có thể xem tour, đặt tour, quản lý booking của mình
- **ADMIN**: Quản trị viên - có toàn quyền quản lý tour, xem tất cả booking, quản lý users

## Phân quyền theo trang

### USER (Người dùng thông thường)
✅ Có thể truy cập:
- Xem danh sách tour
- Đặt tour
- Xem danh sách booking của mình
- Chỉnh sửa/hủy booking của mình

❌ Không thể truy cập:
- Tạo/sửa/xóa tour
- Xem booking của người khác
- Quản lý users

### ADMIN (Quản trị viên)
✅ Có toàn quyền:
- Quản lý tour: tạo, sửa, xóa (`/tours/create`, `/tours/*/edit`, `/tours/*/delete`)
- Xem tất cả booking của tất cả users
- Quản lý users (`/users/**`)
- Tất cả quyền của USER

## Cách thiết lập Admin

### 1. Cập nhật qua SQL (Khuyến nghị)
```sql
-- Xem danh sách users hiện tại
SELECT id, username, email, name, role, provider FROM users;

-- Set user làm admin bằng email
UPDATE users SET role = 'ADMIN' WHERE email = 'admin@example.com';

-- Hoặc set bằng username
UPDATE users SET role = 'ADMIN' WHERE username = 'admin';

-- Verify
SELECT id, username, email, name, role FROM users WHERE role = 'ADMIN';
```

### 2. Chạy SQL script
```bash
# Kết nối PostgreSQL
psql -U spring_user -d practice_db

# Chạy script
\i set_admin_role.sql

# Hoặc trực tiếp
UPDATE users SET role = 'ADMIN' WHERE email = 'your_email@example.com';
```

## Mặc định khi đăng ký
- Tất cả user mới (đăng ký bằng form hoặc OAuth2) đều có role **USER** mặc định
- Phải sử dụng SQL để nâng cấp lên **ADMIN**

## API Endpoints với phân quyền

### Public (Không cần đăng nhập)
- `GET /` - Trang chủ
- `GET /login` - Trang đăng nhập
- `POST /api/auth/login` - API đăng nhập
- `POST /api/auth/register` - API đăng ký
- `GET /users/register` - Form đăng ký

### Authenticated (Cần đăng nhập - USER hoặc ADMIN)
- `GET /bookings` - Xem booking của mình
- `POST /bookings/create` - Đặt tour
- `PUT /bookings/*/edit` - Sửa booking của mình

### Admin Only (Chỉ ADMIN)
- `GET /tours/create` - Form tạo tour
- `POST /tours/create` - Tạo tour mới
- `GET /tours/*/edit` - Form sửa tour
- `PUT /tours/*/edit` - Cập nhật tour
- `DELETE /tours/*/delete` - Xóa tour
- `GET /users/**` - Quản lý users

## Testing

### 1. Test với USER
```bash
# Đăng ký user mới
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "user@test.com",
    "name": "Test User",
    "password": "password123"
  }'

# Đăng nhập
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'

# Thử truy cập trang admin (sẽ bị từ chối)
# Vào trình duyệt: http://localhost:8080/tours/create
```

### 2. Test với ADMIN
```sql
-- Set user thành admin
UPDATE users SET role = 'ADMIN' WHERE email = 'user@test.com';
```

```bash
# Đăng nhập lại
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'

# Truy cập trang admin (thành công)
# Vào trình duyệt: http://localhost:8080/tours/create
```

## Kiểm tra role trong code

### Controller
```java
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/admin-only")
public String adminOnly() {
    return "admin-page";
}

@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
@GetMapping("/user-or-admin")
public String userOrAdmin() {
    return "user-page";
}
```

### Service/Business Logic
```java
public void someMethod() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    boolean isAdmin = auth.getAuthorities().stream()
        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    
    if (isAdmin) {
        // Admin logic
    } else {
        // User logic
    }
}
```

## Troubleshooting

### User không có quyền truy cập
1. Kiểm tra role trong database:
```sql
SELECT username, email, role FROM users WHERE email = 'your_email@example.com';
```

2. Nếu role = 'USER' nhưng cần admin:
```sql
UPDATE users SET role = 'ADMIN' WHERE email = 'your_email@example.com';
```

3. Đăng xuất và đăng nhập lại để token được refresh với role mới

### OAuth2 user không có role
- OAuth2 users tự động được set role = 'USER'
- Nếu cần admin, update database như trên
- Đăng nhập lại qua Google để nhận token mới với role mới
