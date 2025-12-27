# vy_chat_login.md (Vy) — Login + nền tảng gọi API + Chat (cập nhật theo code hiện tại)

> Mục tiêu: giúp bạn **mở đúng file trong 5 giây** khi thầy hỏi. Tài liệu này chỉ tập trung **file code chức năng + gọi API + chat**.

---

## Checklist nhanh (thầy hỏi gì mở file nào?)

- **Token lưu ở đâu?** → `app/src/main/java/com/example/QuanLyPhongTro_App/utils/SessionManager.java`
- **UserId (GUID) lấy ở đâu?** → `SessionManager.getUserId()` và/hoặc `JwtTokenParser.java`
- **Retrofit / Authorization header nằm ở đâu?** → `utils/ApiClient.java`
- **Endpoint API khai báo ở đâu?** → `utils/ApiService.java`
- **Gửi tin nhắn gọi API ở đâu?** → `data/repository/ChatRepository.java#sendMessage`
- **Load lịch sử/polling nằm ở đâu?** → `ui/tenant/ChatActivity.java`
- **Quyết định tin nằm trái/phải?** → `ui/tenant/ChatAdapter.java#getItemViewType`
- **Parse thời gian backend (ThoiGian) ở đâu?** → `utils/ChatTimeParser.java`
- **Tên hiển thị trên tiêu đề chat lấy từ đâu?** → `ui/tenant/ChatActivity.java#tvChatHeader`

---

## 1) Cấu trúc thư mục quan trọng (để tìm nhanh trong project)

### 1.1 `app/src/main/java/com/example/QuanLyPhongTro_App/utils/`
Chứa hạ tầng chung (gần như màn nào gọi API cũng đụng):
- `ApiClient.java`: tạo Retrofit, set token
- `ApiService.java`: khai báo các endpoint chat/room/booking/profile...
- `SessionManager.java`: lưu token, userId, userName
- `JwtTokenParser.java`: parse userId từ JWT (nếu backend nhét claim)
- `UserCache.java`: map `userId → hoTen` để hiển thị (chat list/header)
- `ChatTimeParser.java`: parse `ThoiGian` từ backend về epoch millis

### 1.2 `app/src/main/java/com/example/QuanLyPhongTro_App/data/repository/`
Chứa các class gọi API theo nghiệp vụ:
- `ChatRepository.java`: `sendMessage()`, `getMessageHistory()`

### 1.3 `app/src/main/java/com/example/QuanLyPhongTro_App/ui/tenant/`
Chứa UI người thuê (chat nằm ở đây):
- `ChatActivity.java`: load intent + polling + map dữ liệu
- `ChatAdapter.java`: render bubble + quyết định sent/received
- `ChatListActivity.java`: list thread + cache tên user

### 1.4 `app/src/main/java/com/example/QuanLyPhongTro_App/ui/landlord/`
Chứa UI chủ trọ (chat reuse):
- `LandlordChatActivity.java`: wrapper (extends `ChatActivity`)

---

## 2) Luồng Login → có Token → gọi API

### 2.1 Sau login cần có 3 thứ
1) `token` (JWT)
2) `userId` (GUID) — **khóa chính** để chat/booking/CRUD
3) `userName` (tên hiển thị)

Trong code hiện tại, để chat chạy ổn:
- **token** phải được lưu vào `SessionManager`.
- Trước khi gọi API cần `ApiClient.setToken(token)`.

### 2.2 `SessionManager.java` (nơi lưu token/userId)
- `getToken()` → dùng để set vào `ApiClient`.
- `getUserId()` → GUID dùng trong `ChatActivity`, `ChatRepository`.

Thầy hỏi: “Nếu thiếu userId thì sao?”
- `ChatActivity` sẽ báo lỗi và không gửi được (vì backend chat dùng GUID).

---

## 3) Chat realtime hiện tại là gì?

Hiện tại chat không dùng websocket/firebase, mà là:
- **polling** (giả realtime): `ChatActivity` gọi `getMessageHistory()` mỗi `POLLING_INTERVAL`.

File cần mở:
- `ui/tenant/ChatActivity.java`
  - biến `POLLING_INTERVAL`
  - hàm `setupAutoRefresh()` + `autoLoadMessageHistory()`

Thầy hỏi: “Có phải realtime không?”
- Trả lời: “Reatime theo kiểu polling, app tự refresh lịch sử mỗi X ms.”

---

## 4) Vì sao trước đây hiện sai bên trái/phải? (để trả lời thầy)

### 4.1 Quy tắc quyết định bubble trái/phải
- Nằm ở `ui/tenant/ChatAdapter.java#getItemViewType()`
- Quy tắc chuẩn:
  - `senderId == currentUserId` ⇒ **SENT (bên phải)**
  - ngược lại ⇒ **RECEIVED (bên trái)**

### 4.2 GUID so sánh không phân biệt hoa/thường
Bug từng gặp: `senderId` từ backend có thể là chữ HOA, còn `currentUserId` trong session là chữ thường.
- Fix hiện tại: trong `ChatAdapter`, normalize:
  - `trim()` + `toLowerCase(Locale.US)` trước khi `equals()`.

Từ khoá tìm nhanh:
- search: `toLowerCase(Locale.US)` trong `ChatAdapter.java`

---

## 5) Vì sao thời gian tin nhắn trước đây toàn là "giờ hiện tại"?

Nguyên nhân:
- UI model `data/ChatMessage.java` từng set timestamp = `System.currentTimeMillis()`.

Fix hiện tại:
- Parse `msg.thoiGian` từ API:
  - `utils/ChatTimeParser.java#parseToMillis()`
- Khi map từ API → UI, truyền timestamp vào constructor `new ChatMessage(ts, ...)`

File cần mở:
- `utils/ChatTimeParser.java`
- `ui/tenant/ChatActivity.java` (đoạn convert history)

---

## 6) Theo yêu cầu hiện tại: bỏ hiển thị userId trong bubble

Bạn chỉ muốn:
- **Tên chỉ hiển thị ở tiêu đề** (`tv_chat_header`)
- Trong danh sách tin nhắn **không hiện senderName/userId**.

Fix hiện tại nằm ở:
- `ui/tenant/ChatAdapter.java`:
  - trong `bind()`: `tvSenderName.setVisibility(View.GONE)`

Lưu ý:
- `tv_sender_name` chỉ xuất hiện trong layout received (`item_chat_message_received.xml`).

---

## 7) Toast (thông báo) nằm ở đâu?

Ví dụ thầy hỏi: “Thanh báo đã gửi tin nhắn thành công nằm ở đâu?”
→ `ui/tenant/ChatActivity.java`, tìm:
- `"✅ Tin nhắn đã gửi"`
- `Toast.makeText(...).show()`

---

## 8) Keyword search để tìm nhanh trong IDE (rất hữu ích khi báo cáo)

- Tìm nơi gọi API send message:
  - `sendMessage(messageRequest)` hoặc `"/api/Chat/send"`
- Tìm nơi load lịch sử:
  - `getMessageHistory(`
- Tìm nơi phân trái/phải:
  - `getItemViewType`
- Tìm nơi parse thời gian:
  - `ChatTimeParser.parseToMillis`
- Tìm nơi set tiêu đề:
  - `tv_chat_header` hoặc `setText(headerText)`

---

## 9) Ví dụ “sửa cơ bản” để demo khi báo cáo

- Đổi format giờ hiển thị trong tin nhắn:
  - `ChatAdapter.java` → `new SimpleDateFormat("HH:mm")`
- Đổi tốc độ polling:
  - `ChatActivity.java` → `POLLING_INTERVAL`
- Đổi nội dung Toast:
  - `ChatActivity.java` → string trong `Toast.makeText`

---

## 10) Code nào xóa được mà ít bị lỗi? (trả lời an toàn)

- Có thể xóa/giảm: `Log.d(...)`, comment.
- Không nên xóa:
  - `ApiClient`, `ApiService`, `SessionManager`
  - `ChatRepository`, `ChatActivity`, `ChatAdapter`
  - model `data/model/ChatMessage.java` (mapping JSON)
