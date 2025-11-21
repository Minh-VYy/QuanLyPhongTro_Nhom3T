# HƯỚNG DẪN SỬ DỤNG VỚI DATABASE
## CÁC THAY ĐỔI ĐÃ THỰC HIỆN
### 1. Model Room
- Đã thêm các trường để mapping với database
- Giá lưu dạng số (double priceValue) thay vì String
- Có đánh giá riêng cho phòng (roomRating) và chủ trọ (landlordRating)
### 2. Hiển thị giá
- Sử dụng `room.getFormattedPrice()` để tự động format
- Không cần xử lý gì thêm, database trả về giá là số
### 3. Hiển thị đánh giá
- Đánh giá phòng: `room.getFormattedRoomRating()`
- Đánh giá chủ trọ: `room.getFormattedLandlordRating()`
## CÁCH SỬ DỤNG KHI CÓ DATABASE
### Ví dụ lấy dữ liệu từ database:
```java
// Giả sử bạn đã có database trả về JSON hoặc ResultSet
// Tạo Room object từ database:
Room room = new Room(
    resultSet.getInt("id"),                    // id
    resultSet.getString("title"),              // tiêu đề
    resultSet.getDouble("price"),              // giá (số)
    resultSet.getString("location"),           // vị trí
    resultSet.getString("address"),            // địa chỉ
    resultSet.getDouble("area"),               // diện tích
    resultSet.getString("description"),        // mô tả
    resultSet.getString("image_url"),          // URL ảnh
    resultSet.getDouble("room_rating"),        // rating phòng
    resultSet.getInt("room_review_count"),     // số đánh giá phòng
    resultSet.getInt("landlord_id"),           // id chủ trọ
    resultSet.getString("landlord_name"),      // tên chủ trọ
    resultSet.getDouble("landlord_rating"),    // rating chủ trọ
    resultSet.getInt("landlord_review_count"), // số đánh giá chủ trọ
    resultSet.getString("landlord_phone"),     // SĐT chủ trọ
    resultSet.getBoolean("is_new"),            // phòng mới
    resultSet.getBoolean("is_promo"),          // có khuyến mãi
    resultSet.getString("created_date")        // ngày đăng
);
// Thêm vào danh sách
roomList.add(room);
// Adapter sẽ tự động hiển thị đúng
adapter.notifyDataSetChanged();

Schema database gợi ý:
CREATE TABLE rooms (
    id INT PRIMARY KEY,
    title VARCHAR(255),
    price DECIMAL(10,2),          -- VD: 2500000
    location VARCHAR(100),
    address VARCHAR(255),
    area DECIMAL(5,2),            -- VD: 20.5
    description TEXT,
    image_url VARCHAR(500),
    room_rating DECIMAL(2,1),     -- VD: 4.5
    room_review_count INT,        -- VD: 8
    landlord_id INT,
    landlord_name VARCHAR(100),
    landlord_rating DECIMAL(2,1), -- VD: 4.8
    landlord_review_count INT,    -- VD: 25
    landlord_phone VARCHAR(20),
    is_new BOOLEAN,
    is_promo BOOLEAN,
    created_date DATE
);