# ✅ Cập nhật Form Đăng Ký - Thêm Trường Địa Chỉ

## 📋 Tổng Quan

Đã bổ sung trường **Địa chỉ** vào form đăng ký cho cả:
- ✅ Người thuê (Tenant)
- ✅ Chủ trọ (Landlord)

---

## 🎯 Các Thay Đổi Đã Thực Hiện

### 1. **Layout XML - Người Thuê**

**File**: `activity_tenant_register.xml`

**Thêm field mới** (sau số điện thoại):
```xml
<!-- Địa chỉ -->
<EditText
    android:id="@+id/dia_chi_thue"
    android:layout_width="match_parent"
    android:layout_height="50dp"
    android:hint="Địa chỉ hiện tại"
    android:inputType="textPostalAddress"
    android:padding="15dp"
    android:background="@drawable/edit_text_border"
    android:textColorHint="@color/mau_xam_dam"
    android:layout_marginBottom="15dp" />
```

**Thứ tự các trường trong form**:
1. Họ và tên
2. Email
3. Số điện thoại
4. **Địa chỉ** ← MỚI
5. Mật khẩu
6. Xác nhận mật khẩu
7. Checkbox điều khoản

---

### 2. **Layout XML - Chủ Trọ**

**File**: `activity_landlord_register.xml`

**Thêm field mới** (sau số điện thoại):
```xml
<!-- Địa chỉ -->
<EditText
    android:id="@+id/dia_chi_chu_tro"
    android:layout_width="match_parent"
    android:layout_height="50dp"
    android:hint="Địa chỉ hiện tại"
    android:inputType="textPostalAddress"
    android:padding="15dp"
    android:background="@drawable/edit_text_border"
    android:textColorHint="@color/mau_xam_dam"
    android:layout_marginBottom="15dp" />
```

**Thứ tự các trường trong form**:
1. Họ và tên
2. Email
3. Số điện thoại
4. **Địa chỉ** ← MỚI
5. Mật khẩu
6. Xác nhận mật khẩu
7. Loại giấy tờ (Spinner)
8. Số giấy tờ
9. Tải lên ảnh giấy tờ
10. Checkbox điều khoản

---

### 3. **Java Code - Người Thuê**

**File**: `DangKyNguoiThueActivity.java`

**Khai báo biến**:
```java
private EditText diaChiThue;
```

**Khởi tạo trong onCreate**:
```java
diaChiThue = findViewById(R.id.dia_chi_thue);
```

**Validation trong xuLyDangKy()**:
```java
String diaChi = diaChiThue.getText().toString().trim();

if (diaChi.isEmpty()) {
    Toast.makeText(this, "Vui lòng nhập địa chỉ", Toast.LENGTH_SHORT).show();
    return;
}
```

---

### 4. **Java Code - Chủ Trọ**

**File**: `DangKyChuTroActivity.java`

**Khai báo biến**:
```java
private EditText diaChiChuTro;
```

**Khởi tạo trong onCreate**:
```java
diaChiChuTro = findViewById(R.id.dia_chi_chu_tro);
```

**Validation trong xuLyDangKy()**:
```java
String diaChi = diaChiChuTro.getText().toString().trim();

if (diaChi.isEmpty()) {
    Toast.makeText(this, "Vui lòng nhập địa chỉ", Toast.LENGTH_SHORT).show();
    return;
}
```

---

## 🎨 Đặc Điểm Trường Địa Chỉ

| Thuộc tính | Giá trị |
|-----------|---------|
| **Input Type** | `textPostalAddress` |
| **Hint** | "Địa chỉ hiện tại" |
| **Required** | ✅ Bắt buộc nhập |
| **Validation** | Không được để trống |
| **Style** | Giống các field khác (border, padding, margin) |

---

## ✅ Kiểm Tra Build

```bash
cd E:\lap_trinh_dien_thoai_di_dong\QuanLyPhongTro_App
.\gradlew assembleDebug
```

**Kết quả**: ✅ BUILD SUCCESSFUL

---

## 📱 Hướng Dẫn Sử Dụng

### Đăng Ký Người Thuê:

1. Mở app → Chọn "Người thuê"
2. Tap "Đăng ký"
3. Điền form:
   - Họ tên
   - Email
   - Số điện thoại
   - **Địa chỉ hiện tại** ← Ví dụ: "123 Nguyễn Huệ, Q.1, TP.HCM"
   - Mật khẩu
   - Xác nhận mật khẩu
4. Check "Đồng ý điều khoản"
5. Tap "Đăng Ký"

### Đăng Ký Chủ Trọ:

1. Mở app → Chọn "Chủ trọ"
2. Tap "Đăng ký"
3. Điền form (tương tự người thuê + thêm giấy tờ)
   - **Địa chỉ hiện tại** ← Phải điền
4. Upload ảnh giấy tờ
5. Check "Đồng ý điều khoản"
6. Tap "Đăng Ký"

---

## 🔧 Integration với Backend (TODO)

Khi kết nối API, thêm field `diaChi` vào request:

### Tenant Registration:
```java
JSONObject userData = new JSONObject();
userData.put("hoTen", hoTen);
userData.put("email", email);
userData.put("sdt", sdt);
userData.put("diaChi", diaChi);  // ← MỚI
userData.put("matKhau", matKhau);
```

### Landlord Registration:
```java
JSONObject userData = new JSONObject();
userData.put("hoTen", hoTen);
userData.put("email", email);
userData.put("sdt", sdt);
userData.put("diaChi", diaChi);  // ← MỚI
userData.put("matKhau", matKhau);
userData.put("loaiGiayTo", loaiGiayToChon);
userData.put("soGiayTo", soGiayToNhap);
// ... upload ảnh
```

---

## 📊 Database Schema Update (Gợi Ý)

Nếu dùng SQLite hoặc Room Database:

```sql
-- Tenant Table
CREATE TABLE tenants (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    hoTen TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    sdt TEXT NOT NULL,
    diaChi TEXT NOT NULL,  -- MỚI
    matKhau TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Landlord Table
CREATE TABLE landlords (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    hoTen TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    sdt TEXT NOT NULL,
    diaChi TEXT NOT NULL,  -- MỚI
    matKhau TEXT NOT NULL,
    loaiGiayTo TEXT NOT NULL,
    soGiayTo TEXT NOT NULL,
    hinhGiayTo TEXT,
    verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## ✨ Tính Năng Đã Hoàn Thành

- ✅ Thêm trường địa chỉ vào layout người thuê
- ✅ Thêm trường địa chỉ vào layout chủ trọ
- ✅ Validation không được để trống
- ✅ Cập nhật Java code cho cả 2 activity
- ✅ Build thành công
- ✅ Sẵn sàng cho backend integration

---

## 🎯 Lợi Ích

1. **Thu thập thông tin đầy đủ**: Biết địa chỉ người dùng giúp:
   - Xác minh danh tính
   - Gợi ý phòng trọ gần nhà
   - Liên hệ khi cần thiết

2. **Tăng tính bảo mật**: Thông tin chi tiết hơn

3. **Hỗ trợ tính năng "Gần tôi"**: Có thể dùng địa chỉ để tính khoảng cách

---

**Ngày cập nhật**: 15/11/2025
**Trạng thái**: ✅ Hoàn thành & Kiểm thử

