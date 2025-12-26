# Debug Steps - Tìm Nguyên Nhân Không Có Dữ Liệu

## Bước 1: Test Database Debug Tool

1. **Mở app**
2. **Nhấn giữ "Quên mật khẩu?"** → Database Debug Tool
3. **Nhấn "3. Test Query Yêu Cầu Đặt Phòng"**
4. **Xem kết quả**:
   - ✅ "Tìm thấy X yêu cầu đặt phòng" = Database có dữ liệu
   - ❌ "Không tìm thấy yêu cầu" = Database trống hoặc query lỗi
   - ❌ "Kết nối thất bại" = Vấn đề kết nối

## Bước 2: Test App Chính

1. **Bypass login** (nhấn giữ "Đăng Nhập") → chọn "Chủ Trọ"
2. **Vào tab "Yêu Cầu"**
3. **Xem toast**:
   - "Tải thành công X yêu cầu từ database" = Thành công
   - "Chưa có yêu cầu đặt phòng nào trong database" = Query trả về rỗng
   - "Lỗi kết nối database" = Không kết nối được

## Bước 3: Tạo Test Data Tạm Thời

Nếu vẫn không có dữ liệu:
1. **Vào tab "Yêu Cầu"**
2. **Nhấn giữ tab "Đặt lịch"** (long press)
3. **Sẽ thấy test data** xuất hiện ngay

## Bước 4: Kiểm tra Logcat (Nếu Có)

Nếu có thể xem logcat, filter theo `BookingRequestDao` và tìm:
```
BookingRequestDao: Input ChuTroId: 00000000-0000-0000-0000-000000000002
BookingRequestDao: Direct ChuTroId match count: X
BookingRequestDao: Total DatPhong records: Y
BookingRequestDao: Records for landlord: Z
BookingRequestDao: Final result size: 0
```

## Kết Quả Mong Đợi

### ✅ Nếu Database Debug Tool Thành Công:
- Có nghĩa là database có dữ liệu
- Vấn đề là ở app logic hoặc query

### ❌ Nếu Database Debug Tool Thất Bại:
- Vấn đề kết nối database
- Cần khắc phục network/firewall

### 🔧 Nếu Cần Test Data Ngay:
- Long press tab "Đặt lịch" để tạo test data
- Đảm bảo UI hoạt động đúng

## Ghi Chú

- Database Debug Tool test với cùng landlord ID: `00000000-0000-0000-0000-000000000002`
- Nếu Debug Tool OK nhưng app không có dữ liệu = vấn đề query hoặc logic
- Test data luôn hoạt động để đảm bảo UI không bị lỗi