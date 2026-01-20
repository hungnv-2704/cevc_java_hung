# Hướng dẫn Đăng nhập

Ứng dụng Tour Booking hỗ trợ **2 phương thức đăng nhập**:

## 1. 🔑 Đăng nhập bằng Username & Password

### Đăng ký tài khoản mới:
1. Truy cập: http://localhost:8080/users/register
2. Điền thông tin:
   - Username (tên đăng nhập)
   - Họ tên
   - Email
   - Số điện thoại
   - Mật khẩu (tối thiểu 6 ký tự)
3. Nhấn "Đăng ký"

### Đăng nhập:
1. Truy cập: http://localhost:8080/login
2. Nhập username và password
3. Nhấn "Đăng nhập"

**Provider**: `local`

---

## 2. 🌐 Đăng nhập bằng Google OAuth

### Cấu hình (lần đầu):
1. Tạo Google OAuth2 Credentials - xem chi tiết trong [GOOGLE_OAUTH_SETUP.md](GOOGLE_OAUTH_SETUP.md)
2. Cập nhật Client ID và Client Secret trong `application.properties`

### Đăng nhập:
1. Truy cập: http://localhost:8080/login
2. Nhấn nút "Đăng nhập với Google"
3. Chọn tài khoản Google
4. Cho phép truy cập thông tin

**Provider**: `google`

**Lưu ý**: 
- Tài khoản Google sẽ tự động được tạo lần đầu đăng nhập
- Password được tạo ngẫu nhiên, không thể đăng nhập bằng username/password
- Email từ Google phải chưa được sử dụng trong hệ thống

---

## Phân biệt 2 loại tài khoản

### Tài khoản Local (`provider = "local"`):
- ✅ Có thể đăng nhập bằng username/password
- ❌ Không thể đăng nhập bằng Google OAuth

### Tài khoản Google (`provider = "google"`):
- ✅ Có thể đăng nhập bằng Google OAuth
- ❌ Không thể đăng nhập bằng username/password
- Thông báo lỗi: "User registered with OAuth. Please use Google login."

---

## Kiểm tra trong Database

```sql
-- Xem tất cả users và provider của họ
SELECT username, email, provider, provider_id FROM users;

-- Lọc users đăng ký bằng local
SELECT * FROM users WHERE provider = 'local';

-- Lọc users đăng ký bằng Google
SELECT * FROM users WHERE provider = 'google';
```

---

## Xử lý lỗi thường gặp

### "User registered with OAuth. Please use Google login."
- **Nguyên nhân**: Bạn đang cố đăng nhập bằng username/password cho tài khoản Google
- **Giải pháp**: Sử dụng nút "Đăng nhập với Google"

### "Username is already taken!"
- **Nguyên nhân**: Username đã tồn tại
- **Giải pháp**: Chọn username khác

### "Email is already in use!"
- **Nguyên nhân**: Email đã được đăng ký
- **Giải pháp**: 
  - Nếu đã đăng ký bằng Google → Dùng Google OAuth
  - Nếu đã đăng ký bằng local account → Dùng username/password

### "Invalid username or password"
- **Nguyên nhân**: Sai thông tin đăng nhập
- **Giải pháp**: Kiểm tra lại username và password

---

## Luồng xử lý

### Đăng nhập Username/Password:
```
User nhập username/password
    ↓
POST /api/auth/login
    ↓
CustomUserDetailsService kiểm tra
    ↓
Nếu provider = "google" → Lỗi
Nếu provider = "local" → So sánh password
    ↓
Tạo JWT token
    ↓
Trả về token + user info
```

### Đăng nhập Google OAuth:
```
User nhấn "Đăng nhập với Google"
    ↓
Redirect to Google
    ↓
User chọn tài khoản Google
    ↓
Callback /login/oauth2/code/google
    ↓
CustomOAuth2UserService xử lý
    ↓
Tạo/cập nhật user (provider = "google")
    ↓
OAuth2AuthenticationSuccessHandler
    ↓
Tạo JWT token
    ↓
Redirect về "/" với token trong URL
    ↓
auth.js lưu token vào localStorage
```
