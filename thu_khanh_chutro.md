# thu_khanh_chutro.md (Thu Khánh) — Chủ trọ: CRUD phòng + xem yêu cầu đặt lịch + chat

> File này chỉ tập trung các file **có code chức năng + gọi API** ở phía chủ trọ: hiển thị danh sách phòng, thêm/sửa, và xem yêu cầu (đặt lịch). Chat chủ trọ reuse logic chat của người thuê.

---

## 0) Cấu trúc thư mục cần nhớ

### `app/src/main/java/com/example/QuanLyPhongTro_App/ui/landlord/`
- UI chủ trọ:
  - Danh sách tin đăng/phòng
  - Thêm/sửa phòng
  - Danh sách yêu cầu đặt lịch
  - (chat list / chat wrapper)

### `app/src/main/java/com/example/QuanLyPhongTro_App/utils/`
- Hạ tầng gọi API giống người thuê:
  - `ApiClient.java`, `ApiService.java`
  - `SessionManager.java`
  - `UserCache.java`

### `app/src/main/java/com/example/QuanLyPhongTro_App/data/model/`
- Model mapping JSON (phòng/đặt lịch/chat)

---

## 1) Hiển thị danh sách phòng của chủ trọ

### File UI thường gặp
- `ui/landlord/AllListingsActivity.java` (hoặc màn quản lý phòng/tin đăng)

Luồng:
1) Lấy `token` + `userId` từ `SessionManager`.
2) Gọi API danh sách phòng thuộc chủ trọ (endpoint nằm trong `ApiService.java`).
3) Bind vào RecyclerView Adapter.

Thầy hay hỏi:
- “Dữ liệu hiển thị từ đâu?” → từ API, không phải hardcode.
- “Nếu đổi layout item có ảnh hưởng API không?” → không, vì API chỉ ở Repository/ApiService.

---

## 2) Thêm phòng / sửa phòng (CRUD)

### File UI thường gặp
- Màn thêm: Activity/Fragment thêm phòng (search `Add`/`Create`/`Listing`).
- Màn sửa: Activity/Fragment edit phòng.

Các điểm thầy hay hỏi:
- “Thêm phòng gửi những field nào?”
  - Tìm request body trong function gọi API (thường `Map<String,Object>` hoặc model).
  - Mở `ApiService.java` để thấy endpoint + @Body.

- “Nếu backend đổi tên field JSON thì sửa ở đâu?”
  - Sửa ở **model** (`@SerializedName`) hoặc map request body.

**Chỗ cuối cùng đổ dữ liệu lên UI**
- Adapter + Activity setData.

---

## 3) Xem yêu cầu ở mục Đặt lịch (Booking Requests)

### File UI
- `ui/landlord/BookingRequest.java` (màn/fragment hiển thị yêu cầu)
- `ui/landlord/BookingsAdapter.java` (adapter list)

Luồng:
1) Chủ trọ mở menu “Yêu cầu/Đặt lịch”.
2) App gọi API lấy danh sách yêu cầu.
3) Hiển thị danh sách (tên người thuê, phòng, thời gian).
4) Khi bấm duyệt/từ chối: gọi API update trạng thái.
5) Hiển thị thông báo thành công.

**Câu hỏi thầy**: “Nút xác nhận làm sao để hiện ‘thành công’ thay vì báo lỗi?”
- Quy tắc chuẩn:
  - Nếu `response.isSuccessful()` → show Toast thành công.
  - Nếu `!isSuccessful` → đọc `errorBody` và show lỗi.
- Nếu backend trả về 200 nhưng body có `success=false` → phải check `response.body().success`.

> Gợi ý tìm nhanh: search `Toast.makeText` trong `BookingRequest.java`.

---

## 4) Chat của chủ trọ (reuse code người thuê)

### File chính
- `ui/landlord/LandlordChatActivity.java`
  - Đây là wrapper kế thừa từ `ui/tenant/ChatActivity.java`.
  - Nghĩa là: logic gửi/nhận/polling/map message… **nằm ở ChatActivity**.

- `ui/landlord/LandlordChatListActivity.java`
  - Danh sách đoạn chat của chủ trọ.
  - Cũng có nhiệm vụ cache tên người đối diện vào `UserCache`.

Thầy hay hỏi:
- “Tại sao chủ trọ với người thuê cùng một màn chat?”
  - Vì dùng chung endpoint chat (FromUserId/ToUserId); khác nhau chỉ là role/entrypoint.

---

## 5) Những file quan trọng để chỉ nhanh khi thầy hỏi

- CRUD phòng (list/add/edit)
  - UI: `ui/landlord/AllListingsActivity.java` + màn add/edit
  - API: `utils/ApiService.java`

- Booking requests
  - UI: `ui/landlord/BookingRequest.java`
  - Adapter: `ui/landlord/BookingsAdapter.java`

- Chat chủ trọ
  - Wrapper: `ui/landlord/LandlordChatActivity.java`
  - Logic core: `ui/tenant/ChatActivity.java`, `data/repository/ChatRepository.java`

---

## 6) Demo “sửa cơ bản” để trả lời thầy

- Đổi chữ hiển thị trên header chat:
  - `ChatActivity.java` set `tvChatHeader`.
- Đổi interval polling (giả realtime) để thầy thấy khác:
  - `ChatActivity.java`: `POLLING_INTERVAL`.
- Đổi text Toast duyệt yêu cầu:
  - `BookingRequest.java`: tìm `Toast.makeText`.

---

## 7) Code nào xóa không lỗi?

- Có thể xóa: log debug (`Log.d`) trong một số màn.
- Không nên xóa:
  - `ApiClient`, `ApiService`, `SessionManager`
  - Repository/Model liên quan endpoint (phòng/booking/chat)
  - Activities chính của chủ trọ (list/add/edit/booking)

