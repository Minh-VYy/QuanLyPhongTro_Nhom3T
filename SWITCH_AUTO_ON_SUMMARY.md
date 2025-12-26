# Fix Switch Duyệt Phòng Mặc Định Bật

## Vấn đề
Khi chủ trọ xem danh sách phòng của mình, nút gạt (switch) duyệt phòng không bật mặc định, mặc dù phòng đã được tự động duyệt khi đăng tin.

## Nguyên nhân
- Dữ liệu cũ trong database có thể vẫn có `IsDuyet = 0`
- Logic switch dựa vào `phong.isDuyet() && !phong.isBiKhoa()` nên nếu `isDuyet = false` thì switch sẽ tắt

## Giải pháp
Thay đổi logic xác định trạng thái active của switch:

### Trước (dựa vào cả isDuyet và isBiKhoa):
```java
boolean isActive = phong.isDuyet() && !phong.isBiKhoa();
```

### Sau (chỉ dựa vào isBiKhoa):
```java
boolean isActive = !phong.isBiKhoa();
```

## Files đã sửa

### 1. `LandlordHomeActivity.java`
- **Dòng 409**: Thay đổi logic xác định `isActive`
- **Lý do**: Vì phòng đã tự động duyệt khi tạo, chỉ cần kiểm tra không bị khóa

### 2. `AllListingsActivity.java`  
- **Dòng 441-442**: Thay đổi logic xác định `isActive`
- **Lý do**: Đồng bộ với logic trong LandlordHomeActivity

## Kết quả
- ✅ Switch duyệt phòng sẽ mặc định bật (ON) cho tất cả phòng không bị khóa
- ✅ Phòng mới đăng sẽ có switch bật ngay lập tức
- ✅ Dữ liệu cũ cũng sẽ hiển thị switch bật (trừ phòng bị khóa)

## Script SQL hỗ trợ
Đã tạo file `update_existing_rooms_approved.sql` để update dữ liệu cũ nếu cần:
```sql
UPDATE Phong 
SET IsDuyet = 1, UpdatedAt = GETDATE() 
WHERE IsDuyet = 0 AND IsDeleted = 0;
```

## Build Status
✅ BUILD SUCCESSFUL - Tất cả thay đổi đã compile thành công