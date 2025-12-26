# Hướng Dẫn Test Trang Yêu Cầu Đặt Lịch - FIXED VERSION

## Vấn Đề Đã Khắc Phục
- ✅ App hiển thị 2 toast: "DEBUG..." và "Chưa có yêu cầu..."
- ✅ Database task ghi đè lên test data
- ✅ Thêm cách tạo test data thủ công

## Cách Test Mới - 2 Phương Pháp

### Phương Pháp 1: Tự Động (Sau 1 Giây)
1. **Bypass login**: Nhấn giữ nút "Đăng Nhập" → chọn "Chủ Trọ"
2. **Vào tab "Yêu Cầu"** ở bottom navigation
3. **Đợi 1 giây** - test data sẽ tự động xuất hiện
4. Sẽ thấy toast "DEBUG: Tạo 2 yêu cầu test"

### Phương Pháp 2: Thủ Công (Khuyến Nghị)
1. **Bypass login** và **vào tab "Yêu Cầu"**
2. **Nhấn giữ tab "Đặt lịch"** (long press trên chữ "Đặt lịch")
3. Ngay lập tức sẽ thấy:
   - Toast "DEBUG: Tạo 2 yêu cầu test"
   - 2 yêu cầu xuất hiện trong danh sách

## Dữ Liệu Test Sẽ Hiển Thị

1. **Nguyễn Văn A (Test)** - Xem phòng 'Phòng 101 - Quận 1'
   - Trạng thái: ChoXacNhan (màu cam)
   - Có nút "Chấp nhận" và "Từ chối"

2. **Trần Thị B (Test)** - Thuê phòng 'Phòng 205 - Quận 7'
   - Trạng thái: DaXacNhan (màu xanh)
   - Không có nút action

## Test Chức Năng

### Test Cập Nhật Trạng Thái
1. Với yêu cầu "Nguyễn Văn A (Test)" (trạng thái ChoXacNhan)
2. Nhấn "Chấp nhận" → trạng thái chuyển thành "DaXacNhan" (màu xanh)
3. Nhấn "Từ chối" → trạng thái chuyển thành "DaHuy" (màu đỏ)
4. Sau khi cập nhật, các nút sẽ biến mất

## Debug Bằng Logcat

### Filter theo tag "YeuCau":
```
YeuCau: === STARTING YeuCau Activity ===
YeuCau: Long clicked Dat Lich - creating test data
YeuCau: Creating immediate test booking data
YeuCau: Immediate test data added: 2 items
```

### Filter theo tag "BookingsAdapter":
```
BookingsAdapter: getItemCount: 2
BookingsAdapter: Creating view holder
BookingsAdapter: Binding view holder at position: 0
BookingsAdapter: Accept clicked for: IMMEDIATE_TEST001
```

## Troubleshooting

### Nếu Vẫn Không Thấy Dữ Liệu
1. **Thử phương pháp 2**: Nhấn giữ tab "Đặt lịch"
2. **Kiểm tra logcat**: Tìm "Creating immediate test booking data"
3. **Kiểm tra tab**: Đảm bảo đang ở tab "Đặt lịch" (màu xanh)

### Nếu App Crash
1. Kiểm tra logcat cho stack trace
2. Thường do layout issues hoặc null pointer

### Nếu Nút Không Hoạt Động
1. Kiểm tra logcat cho "Accept clicked" hoặc "Reject clicked"
2. Đảm bảo đang click đúng nút

## Kết Quả Mong Đợi

✅ **Thành công khi:**
- Thấy toast "DEBUG: Tạo 2 yêu cầu test"
- 2 items hiển thị trong RecyclerView
- Có thể click "Chấp nhận"/"Từ chối"
- Trạng thái thay đổi màu sắc đúng
- Logcat hiển thị đầy đủ debug info

❌ **Cần debug thêm khi:**
- Không thấy toast debug
- RecyclerView vẫn trống
- Nút không hoạt động
- App crash

## Ghi Chú Quan Trọng

- **Phương pháp 2 (nhấn giữ tab) đáng tin cậy hơn**
- Test data có ID bắt đầu bằng "IMMEDIATE_TEST"
- Dữ liệu chỉ tồn tại trong phiên hiện tại
- Khi có database thật, sẽ ưu tiên hiển thị dữ liệu thật

## Lệnh Debug Nhanh

1. **Bypass login**: Long press "Đăng Nhập"
2. **Tạo test data**: Long press tab "Đặt lịch"
3. **Xem log**: Filter logcat theo "YeuCau" và "BookingsAdapter"