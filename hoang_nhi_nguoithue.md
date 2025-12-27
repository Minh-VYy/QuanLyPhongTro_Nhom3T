# hoang_nhi_nguoithue.md (Hoàng Nhi) — Người thuê: danh sách phòng + chi tiết phòng + chat + đặt lịch

> File này tập trung **code chức năng + gọi API** cho luồng người thuê, đặc biệt phần chat/đặt lịch. Có kèm các câu hỏi thầy hay hỏi và cách trả lời dựa vào code.

---

## 0) Cấu trúc thư mục code (những thư mục quan trọng để tìm nhanh)

### `app/src/main/java/com/example/QuanLyPhongTro_App/ui/tenant/`
- Chứa UI (Activity/Fragment) phía **người thuê**:
  - Danh sách phòng, xem chi tiết phòng
  - Chat
  - Đặt lịch, danh sách lịch đặt

### `app/src/main/java/com/example/QuanLyPhongTro_App/data/repository/`
- Chứa repository gọi API (Retrofit) như:
  - `ChatRepository.java` (gửi/nhận tin nhắn)
  - (các repository khác nếu có: phòng/đặt lịch…)

### `app/src/main/java/com/example/QuanLyPhongTro_App/utils/`
- Hạ tầng dùng chung:
  - `ApiClient.java`, `ApiService.java` (gọi API)
  - `SessionManager.java` (token/userId)
  - `UserCache.java` (map userId → name để hiển thị chat)
  - `ChatTimeParser.java` (parse `ThoiGian` về millis để hiển thị đúng thời gian)

### `app/src/main/java/com/example/QuanLyPhongTro_App/data/model/`
- Model mapping JSON từ backend:
  - `ChatMessage.java` (API message: FromUserId/ToUserId/NoiDung/ThoiGian)
  - `Phong.java`, `DatPhong.java`… (tùy nghiệp vụ)

---

## 1) Luồng “Xem danh sách phòng → xem chi tiết phòng”

### 1.1 Danh sách phòng
- File chính:
  - `ui/tenant/Room.java` (model UI)
  - `ui/tenant/RoomAdapter.java` (RecyclerView adapter)
  - `ui/tenant/MainActivity.java` hoặc màn list phòng (tùy project)

**Thầy hay hỏi**: “Danh sách phòng lấy từ đâu?”
- Trả lời: từ API trong `ApiService` (endpoint danh sách phòng) và được gọi trong Activity; kết quả gắn vào `RoomAdapter`.

> Gợi ý: search `getPhong` / `getRooms` / `getDanhSachPhong` trong `ApiService.java`.

### 1.2 Chi tiết phòng
- File chính: `ui/tenant/RoomDetailActivity.java`

Trong `RoomDetailActivity`, thường có:
- Nhận `roomId`/`phongId` qua Intent.
- Gọi API lấy chi tiết phòng.
- Render UI: giá, địa chỉ, tiện ích...

**Câu hỏi khó**: “Thông tin chủ trọ hiển thị ở chi tiết phòng lấy từ đâu?”
- Có 2 trường hợp:
  1) API chi tiết phòng trả kèm `chuTroId` + `chuTroName` → dùng trực tiếp
  2) API chỉ trả `chuTroId` → app phải gọi thêm API hồ sơ chủ trọ hoặc dùng cache đã có.

---

## 2) Luồng Chat của người thuê

### 2.1 Danh sách đoạn chat (threads/contacts)
- File:
  - `ui/tenant/ChatListActivity.java`
  - `ui/tenant/ChatThreadListAdapter.java`
  - Repository: `data/repository/ChatThreadRepository.java` (nếu có dùng)

Vai trò:
- Gọi API lấy danh sách cuộc hội thoại.
- **Cache** tên người còn lại vào `UserCache.addUser(otherUserId, otherUserName)`.

**Thầy hỏi**: “Tại sao bên danh sách có tên, vào chat lại hiện userId?”
- Do chat history API chỉ trả `FromUserId/ToUserId`, không trả `name`.
- Nếu `UserCache` chưa có name, app fallback sang `userId`.
- Fix: đảm bảo `ChatListActivity` add cache trước khi mở `ChatActivity`.

### 2.2 Màn chat chi tiết (history + gửi tin)
- File chính:
  - `ui/tenant/ChatActivity.java`
  - `ui/tenant/ChatAdapter.java`
  - `data/repository/ChatRepository.java`
  - `data/model/ChatMessage.java` (API)
  - `data/ChatMessage.java` (UI message)
  - `utils/ChatTimeParser.java`

#### 2.2.1 Vì sao “thời gian tin nhắn toàn hiện giờ hiện tại”? (bug bạn gặp)
Nguyên nhân thường gặp trong project này:
- UI model `data/ChatMessage.java` trước đây luôn set `timestamp = System.currentTimeMillis()`.
- Khi load lịch sử từ API, code tạo new `ChatMessage(...)` nhưng không truyền thời gian thật từ `msg.thoiGian`.

Fix đã làm:
- Thêm constructor mới của `data/ChatMessage` nhận `timestamp`.
- Parse `msg.thoiGian` trong `ChatActivity` bằng `ChatTimeParser.parseToMillis(msg.thoiGian)`.

Chỗ trả lời thầy:
- Mở `ChatActivity.java`, tìm `ChatTimeParser.parseToMillis`.

#### 2.2.2 Vì sao hiện userId thay vì tên?
- Tin nhắn API (`data/model/ChatMessage`) không có trường tên.
- App hiển thị tên dựa vào `UserCache.getUserName(userId)`.

Fix đã làm:
- `ChatActivity` khi convert history: `displayName = cachedName != null ? cachedName : shortId(userId)`.

#### 2.2.3 Vì sao “mình nhắn mà tin nhảy qua cột nhận”? (bug bạn gặp)
Nguyên nhân:
- `ChatAdapter.getItemViewType()` xác định sent/received bằng:
  - `message.senderId == currentUserId`
- Nếu `senderId` bạn set sai (ví dụ set = `toUser` hoặc để thừa whitespace), so sánh lệch → tin của mình bị coi như received.

Fix đã làm:
- Trong `ChatActivity` map message từ API: luôn set `senderId = msg.fromUser.trim()`.
- Chuẩn hoá trim cả `currentUserId` và `senderId`.

Thầy hỏi: “Chỉ ra đoạn code quyết định tin nằm bên trái hay phải?”
- Mở `ui/tenant/ChatAdapter.java` → hàm `getItemViewType()`.

---

## 3) Đặt lịch (booking) của người thuê

### 3.1 Tạo đặt lịch
- File thường gặp:
  - `ui/tenant/BookingCreateActivity.java`

Luồng:
1) Người thuê chọn phòng + chọn thời gian.
2) Gọi API tạo yêu cầu đặt lịch.
3) Nhận response.
4) Hiển thị thông báo thành công (Toast) + điều hướng sang danh sách đặt lịch.

**Câu hỏi thầy**: “Làm sao để khi xác nhận/đặt lịch hiển thị thành công chứ không báo lỗi?”
- Kiểm tra:
  - `response.isSuccessful()`
  - body có `success=true` hay message.
- Nếu backend trả 200 nhưng body báo lỗi: phải đọc `response.body()`.
- Nếu backend trả 204/201: vẫn là success.

> Mẹo demo sửa cơ bản: đổi message Toast thành rõ ràng hơn, hoặc chỉ show error khi `!response.isSuccessful()`.

### 3.2 Danh sách lịch đặt
- File:
  - `ui/tenant/BookingListActivity.java`
  - `ui/tenant/BookingListFragment.java`
  - `ui/tenant/BookingAdapter.java`

Thầy hay hỏi:
- “Danh sách lịch đặt lấy từ đâu?” → API trong `ApiService` + bind vào Adapter.

---

## 4) Nhắc nhanh: những file quan trọng nhất để trả lời thầy
- Sent/Received hiển thị lệch: `ui/tenant/ChatAdapter.java#getItemViewType`
- Map API message → UI message: `ui/tenant/ChatActivity.java` (3 chỗ: `loadMessageHistory`, `loadMessageHistoryWithRetry`, `autoLoadMessageHistory`)
- Parse thời gian: `utils/ChatTimeParser.java`
- Lưu token/userId: `utils/SessionManager.java`
- Endpoint chat: `utils/ApiService.java`

---

## 5) Những thay đổi nhỏ có thể demo ngay khi báo cáo
- Đổi format giờ tin nhắn ở `ChatAdapter`.
- Đổi text Toast “Tin nhắn đã gửi”.
- Đổi interval polling trong `ChatActivity` (`POLLING_INTERVAL`).
  - Thầy hỏi “đây có phải realtime không?” → hiện tại là **polling** (giả realtime).

---

## 6) “Code nào xóa không lỗi?” (trả lời an toàn)
- Có thể xóa/giảm: `Log.d`, comment, tài liệu `.md`.
- Không nên xóa: `ApiClient`, `ApiService`, `SessionManager`, `ChatRepository`, `ChatActivity`, `ChatAdapter`, models `ChatMessage`.

