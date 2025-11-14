# 📖 Hướng Dẫn Hoàn Thiện Flow Đăng Nhập/Đăng Ký

## ✅ Đã Hoàn Thành

### 1. Xóa ChonVaiTroActivity
- ✅ Đã xóa file `ChonVaiTroActivity.java`
- ✅ Đã xóa file `activity_role_selection.xml`
- ✅ Đã xóa khỏi `AndroidManifest.xml`

### 2. Splash Flow
- ✅ `SplashActivity` → `MainActivity` (Guest Mode)

### 3. SessionManager
- ✅ Tạo `SessionManager.java` để quản lý session
- ✅ Lưu thông tin: userId, userName, userEmail, userRole

### 4. MainActivity với Role Dropdown
- ✅ Dropdown "Người thuê" / "Chủ trọ" ở header
- ✅ Chọn "Người thuê": Mặc định
- ✅ Chọn "Chủ trọ": Hiển thị dialog Đăng nhập/Đăng ký Chủ trọ

### 5. Logic Phân Quyền
- ✅ Đăng ký Chủ trọ → Tự động có cả quyền Người thuê
- ✅ Đăng ký Người thuê → Chỉ có quyền Người thuê
- ✅ Đăng nhập dùng chung (kiểm tra role trên server)

---

## ⚠️ CẦN SỬA LỖI

### Lỗi Layout File

**Vấn đề**: `DangNhapNguoiThueActivity` đang dùng `R.layout.activity_tenant_login` nhưng file này không tồn tại.

**Giải pháp**: Sửa layout reference

#### File: `DangNhapNguoiThueActivity.java`

Dòng 30, sửa từ:
```java
setContentView(R.layout.activity_tenant_login);
```

Thành:
```java
setContentView(R.layout.activity_login);
```

#### File: `DangNhapChuTroActivity.java`

Kiểm tra xem file `activity_login.xml` có đủ các ID sau không:

**Cho Tenant**:
- `email_sdt_thue`
- `mat_khau_thue`
- `btn_dang_nhap_thue`
- `btn_google_thue`
- `quen_mat_khau_thue`
- `chuyen_chu_tro_thue`
- `dang_ky_thue`

**Cho Landlord**:
- `email_sdt_chu_tro`
- `mat_khau_chu_tro`
- `btn_dang_nhap_chu_tro`
- `btn_google_chu_tro`
- `quen_mat_khau_chu_tro`
- `chuyen_nguoi_thue_chu_tro`
- `dang_ky_chu_tro`

---

## 🔧 Lệnh Sửa Nhanh

### Option 1: Tạo 2 layout riêng (Khuyến nghị)

**Tạo `activity_tenant_login.xml`** (copy từ `activity_login.xml` và đổi ID)

**Tạo `activity_landlord_login.xml`** (copy từ `activity_login.xml` và đổi ID)

### Option 2: Dùng chung 1 layout

**Sửa `DangNhapNguoiThueActivity.java`**:
```java
setContentView(R.layout.activity_login);
```

**Sửa `activity_login.xml`** để có đủ ID cho cả 2 role hoặc dùng ID chung:
```xml
android:id="@+id/email_input"
android:id="@+id/password_input"
android:id="@+id/btn_login"
...
```

Rồi update Java code:
```java
emailSdtThue = findViewById(R.id.email_input);
matKhauThue = findViewById(R.id.password_input);
btnDangNhapThue = findViewById(R.id.btn_login);
```

---

## 📋 Flow Hoàn Chỉnh

### 1. App khởi động
```
SplashActivity (2s) 
  ↓
MainActivity (Guest Mode - Người thuê)
```

### 2. Khách vãng lai xem phòng
```
MainActivity
  - Xem danh sách phòng: ✅ OK
  - Xem chi tiết phòng: ✅ OK
  - Bộ lọc: ✅ OK
```

### 3. Khách muốn đặt lịch/xem thông báo/vào profile
```
Tap bottom nav → Kiểm tra session
  ↓
Nếu chưa đăng nhập:
  Dialog: "Đăng nhập" / "Đăng ký" / "Hủy"
    ↓
  Chọn "Đăng nhập" → DangNhapNguoiThueActivity
  Chọn "Đăng ký" → DangKyNguoiThueActivity
```

### 4. Khách muốn chuyển sang Chủ trọ
```
Tap dropdown "Người thuê" 
  ↓
Chọn "Chủ trọ"
  ↓
Dialog: "Đăng nhập" / "Đăng ký" / "Hủy"
  ↓
Chọn "Đăng nhập" → DangNhapChuTroActivity
Chọn "Đăng ký" → DangKyChuTroActivity
```

### 5. Sau khi đăng nhập/đăng ký

**Người thuê**:
```
DangNhapNguoiThueActivity
  ↓
session.createLoginSession(userId, userName, email, "tenant")
  ↓
MainActivity (Logged in as Tenant)
```

**Chủ trọ**:
```
DangNhapChuTroActivity hoặc DangKyChuTroActivity
  ↓
session.createLoginSession(userId, userName, email, "landlord")
  ↓
MainActivity (Logged in as Landlord - có cả quyền Tenant)
```

### 6. Đăng xuất
```
ProfileActivity → Tap "Đăng xuất"
  ↓
sessionManager.logout()
  ↓
MainActivity (Guest Mode)
```

---

## 🎯 Tính Năng Phân Quyền

| Tài khoản | Đăng ký | Quyền |
|-----------|---------|-------|
| **Người thuê** | Đăng ký Người thuê | Chỉ xem phòng, đặt lịch (Tenant role) |
| **Chủ trọ** | Đăng ký Chủ trọ | Cả Người thuê + Chủ trọ (Landlord role) |

**Ví dụ**:
- User A đăng ký "Người thuê" → Chỉ có 1 role: `tenant`
- User B đăng ký "Chủ trọ" → Có 2 role: `landlord` (chính) + `tenant` (phụ)
  - User B có thể chuyển dropdown để xem giao diện Người thuê hoặc Chủ trọ

---

## 🚀 Lệnh Chạy App

```bash
cd E:\lap_trinh_dien_thoai_di_dong\QuanLyPhongTro_App

# Clean build
.\gradlew clean

# Build APK
.\gradlew assembleDebug

# Install to device/emulator
.\gradlew installDebug

# Hoặc run trực tiếp
adb install app\build\outputs\apk\debug\app-debug.apk
```

---

## ✨ Checklist Hoàn Thành

- [x] Xóa ChonVaiTroActivity
- [x] Splash → MainActivity
- [x] SessionManager
- [x] Role Dropdown ở MainActivity
- [x] Guest Mode authentication check
- [x] Đăng ký Chủ trọ → Auto có quyền Tenant
- [ ] **Sửa lỗi layout reference** ← CẦN LÀM NGAY
- [ ] Test flow đầy đủ
- [ ] Connect backend API

---

**Sau khi sửa lỗi layout, build lại sẽ thành công!**

