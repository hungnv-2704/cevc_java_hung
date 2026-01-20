# Hướng dẫn cấu hình Google OAuth2

## Bước 1: Tạo Google OAuth2 Credentials

1. Truy cập [Google Cloud Console](https://console.cloud.google.com/)
2. Tạo project mới hoặc chọn project hiện có
3. Vào **APIs & Services** > **Credentials**
4. Nhấn **Create Credentials** > **OAuth 2.0 Client IDs**
5. Chọn **Application type**: Web application
6. Điền thông tin:
   - **Name**: Tour Booking OAuth Client
   - **Authorized JavaScript origins**: 
     - `http://localhost:8080`
     - `http://localhost:8080/`
   - **Authorized redirect URIs**:
     - `http://localhost:8080/login/oauth2/code/google`
7. Nhấn **Create**
8. Copy **Client ID** và **Client Secret**

## Bước 2: Cập nhật application.properties

Mở file `src/main/resources/application.properties` và cập nhật:

```properties
# Google OAuth2 Configuration
spring.security.oauth2.client.registration.google.client-id=YOUR_ACTUAL_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_ACTUAL_CLIENT_SECRET
```

Thay thế:
- `YOUR_ACTUAL_CLIENT_ID` bằng Client ID bạn đã copy
- `YOUR_ACTUAL_CLIENT_SECRET` bằng Client Secret bạn đã copy

## Bước 3: Khởi động lại ứng dụng

```bash
mvn clean install
mvn spring-boot:run
```

## Bước 4: Test Google OAuth Login

1. Truy cập http://localhost:8080/login
2. Nhấn vào nút "Đăng nhập với Google"
3. Chọn tài khoản Google để đăng nhập
4. Cho phép ứng dụng truy cập thông tin profile và email
5. Sau khi đăng nhập thành công, bạn sẽ được chuyển về trang chủ

## Các thay đổi đã thực hiện

### 1. Dependencies (pom.xml)
- Thêm `spring-boot-starter-oauth2-client`

### 2. User Model
- Thêm field `provider` (google, local, etc.)
- Thêm field `providerId` (ID from OAuth provider)
- Đặt `password` nullable cho OAuth users

### 3. Security Configuration
- Cấu hình OAuth2 login
- Thêm OAuth2 user service
- Thêm success handler cho OAuth2

### 4. New Files
- `OAuth2UserInfo.java`: Class để xử lý thông tin user từ OAuth2
- `CustomOAuth2UserService.java`: Service xử lý OAuth2 user
- `OAuth2AuthenticationSuccessHandler.java`: Handler xử lý sau khi OAuth2 thành công

### 5. Frontend
- Thêm nút "Đăng nhập với Google" trong login page
- Cập nhật auth.js để xử lý token từ OAuth2 redirect

## Lưu ý quan trọng

1. **Môi trường Production**: Khi deploy lên production, nhớ cập nhật:
   - Authorized JavaScript origins
   - Authorized redirect URIs
   với domain thật của bạn

2. **Bảo mật**: 
   - KHÔNG commit Client Secret vào Git
   - Sử dụng environment variables hoặc secret management
   - Ví dụ: `${GOOGLE_CLIENT_SECRET:default_value}`

3. **Database**: Chạy ứng dụng một lần để Hibernate tự động tạo/cập nhật bảng users với các cột mới.

## Troubleshooting

### Lỗi "redirect_uri_mismatch"
- Kiểm tra lại redirect URI trong Google Console phải chính xác: `http://localhost:8080/login/oauth2/code/google`

### Lỗi "invalid_client"
- Kiểm tra Client ID và Client Secret đã được cấu hình đúng trong application.properties

### User không được tạo
- Kiểm tra logs để xem lỗi database
- Đảm bảo database đang chạy và kết nối được
