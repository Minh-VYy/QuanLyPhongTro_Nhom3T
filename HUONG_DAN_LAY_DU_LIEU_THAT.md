# Hướng Dẫn Lấy Dữ Liệu Thật Từ Database - ĐÃ CÓ DỮ LIỆU

## ✅ Tình Hình Hiện Tại
- **Kết nối database**: Thành công ✅
- **Dữ liệu demo**: Đã tạo cho landlord demo ✅  
- **3 yêu cầu đặt phòng**: Sẵn sàng hiển thị ✅

## Bước Test Ngay Bây Giờ

### 1. Đăng Nhập Demo
1. **Mở app** → màn hình đăng nhập
2. **Nhấn giữ nút "Đăng Nhập"** → chọn "Chủ Trọ"
3. App đăng nhập với: **Nguyễn Văn A (Chủ Trọ)**

### 2. Xem Dữ Liệu Thật
1. **Vào tab "Yêu Cầu"** ở bottom navigation
2. **Sẽ thấy toast**: "Tải thành công 3 yêu cầu đặt phòng từ database"
3. **Hiển thị 3 yêu cầu thật**:
   - **Trần Thị B** - Xem phòng 'Phòng 101 - Quận 1' (Chờ xác nhận)
   - **Trần Thị B** - Thuê phòng 'Phòng 102 - Quận 1' (Đã xác nhận)  
   - **Trần Thị B** - Xem phòng 'Phòng 101 - Quận 1' (Đã hủy)

### 3. Test Chức Năng
1. **Với yêu cầu "Chờ xác nhận"**:
   - Nhấn "Chấp nhận" → lưu vào database
   - Nhấn "Từ chối" → lưu vào database
2. **Trạng thái sẽ thay đổi** và được lưu thật vào database

## Dữ Liệu Demo Đã Tạo

### Nhà Trọ:
- **Tên**: "Nhà Trọ Demo - Quận 1"
- **Địa chỉ**: "123 Nguyễn Văn Linh, Quận 1, TP.HCM"

### Phòng Trọ:
1. **Phòng 101** - 25m² - 3.500.000đ
2. **Phòng 102** - 30m² - 4.000.000đ

### Yêu Cầu Đặt Phòng:
1. **Xem Phòng 101** - Chờ xác nhận (có nút action)
2. **Thuê Phòng 102** - Đã xác nhận (không có nút)
3. **Xem Phòng 101** - Đã hủy (không có nút)

## Kết Quả Mong Đợi

### ✅ Thành Công Khi Thấy:
- Toast: "Tải thành công 3 yêu cầu đặt phòng từ database"
- 3 yêu cầu hiển thị với tên thật (không có "(Test)")
- Có thể cập nhật trạng thái và lưu vào database
- Logcat hiển thị: "Loaded 3 booking requests from database"

### ❌ Nếu Vẫn Thấy:
- "Chưa có yêu cầu đặt phòng nào" → Có vấn đề query
- Test data với "(Test)" → Vẫn chưa kết nối database
- "Lỗi kết nối database" → Vấn đề mạng

## Debug Nếu Cần

### Kiểm tra Database Debug Tool:
1. **Nhấn giữ "Quên mật khẩu?"** → Database Debug Tool
2. **Test "3. Test Query Yêu Cầu Đặt Phòng"**
3. **Sẽ thấy**: "✅ Tìm thấy 3 yêu cầu đặt phòng"

### Kiểm tra Logcat:
```
BookingRequestDao: Found booking #1: Trần Thị B (Người Thuê) - Phòng 101 - Quận 1
BookingRequestDao: Found booking #2: Trần Thị B (Người Thuê) - Phòng 102 - Quận 1  
BookingRequestDao: Found booking #3: Trần Thị B (Người Thuê) - Phòng 101 - Quận 1
YeuCau: Loaded 3 booking requests from database
```

## Ghi Chú Quan Trọng

- **Dữ liệu thật**: Không còn cần test data nữa
- **Landlord ID**: `00000000-0000-0000-0000-000000000002`
- **Tenant ID**: `00000000-0000-0000-0000-000000000001`  
- **Database**: QuanLyPhongTro với dữ liệu demo hoàn chỉnh
- **Cập nhật trạng thái**: Sẽ lưu thật vào database

Bây giờ app sẽ hiển thị **dữ liệu thật từ database** thay vì test data!