# 🎓 CÂU HỎI & TRẢ LỜI CHO BÁO CÁO (QA Format)

## 📚 PHẦN 1: KIẾN TRÚC CHUNG

### Q1: Ứng dụng của bạn sử dụng kiến trúc gì?

**Trả lời:**
> "Ứng dụng sử dụng kiến trúc **3-Layer Architecture**:
> 1. **Presentation Layer**: Activity + Adapter - Giao diện
> 2. **Business Logic Layer**: Repository - Xử lý logic
> 3. **Data Layer**: API Service + SessionManager - Dữ liệu
> 
> Cơ chế: Activity → Repository → API Service → Backend"

---

### Q2: Tại sao phải dùng Repository Pattern?

**Trả lời:**
> "Repository Pattern giúp:
> 1. **Tách biệt**: Activity không biết API, chỉ gọi Repository
> 2. **Reusable**: Nhiều Activity dùng chung 1 Repository
> 3. **Testable**: Dễ test logic riêng biệt
> 4. **Bảo trì**: Đổi API cách gọi mà Activity không cần sửa
> 
> Ví dụ: Thay đổi từ HTTP sang WebSocket, chỉ sửa Repository"

---

### Q3: Retrofit là gì? Tại sao dùng?

**Trả lời:**
> "Retrofit là thư viện HTTP client:
> - **REST API calls**: Dễ định nghĩa endpoints
> - **Async**: Không block UI thread
> - **Callback**: Xử lý response tự động
> - **JSON conversion**: Tự convert JSON ↔ Object (qua Gson)
> 
> Code:
> ```java
> @POST("/api/Chat/send")
> Call<GenericResponse<Object>> sendMessage(@Body Object request);
> ```
> 
> Khi gọi: apiService.sendMessage(obj).enqueue(callback)"

---

## 📚 PHẦN 2: GỌIMANA API

### Q4: Quy trình gọi API như thế nào?

**Trả lời:**
> "Quy trình gọi API:
> 
> 1. **Activity** gọi **Repository method**
>    ```java
>    chatRepository.sendMessage(userId, toId, content, callback)
>    ```
> 
> 2. **Repository** tạo request body
>    ```java
>    Map<String, Object> request = new HashMap<>();
>    request.put("FromUserId", userId);
>    request.put("Content", content);
>    ```
> 
> 3. **Repository** gọi **API async**
>    ```java
>    apiService.sendMessage(request).enqueue(new Callback<...>() {
>        @Override
>        public void onResponse(Call<...> call, Response<...> response) {
>            if (response.isSuccessful()) {
>                callback.onSuccess(...);
>            } else {
>                callback.onError(...);
>            }
>        }
>    });
>    ```
> 
> 4. **Callback** trả kết quả về **Activity**
>    ```java
>    callback.onSuccess() → Update UI
>    callback.onError() → Show toast
>    ```"

---

### Q5: Tại sao phải dùng async callback?

**Trả lời:**
> "Vì:
> 1. **Network không instant**: API call mất 1-3 giây
> 2. **Không block UI**: Nếu dùng sync, UI lock cả giây
> 3. **Callback pattern**: Xử lý khi response về, không phải wait
> 
> Sai (Sync):
> ```java
> Response response = apiService.sendMessage(obj).execute();
> // ← App bị FREEZE 1-3 giây
> ```
> 
> Đúng (Async):
> ```java
> apiService.sendMessage(obj).enqueue(new Callback<...>() {
>     public void onResponse(...) {
>         // ← Xử lý khi response về, app mình chạy bình thường
>     }
> });
> ```"

---

### Q6: GenericResponse là gì?

**Trả lời:**
> "GenericResponse là wrapper do backend định nghĩa:
> ```java
> {
>     \"success\": true,
>     \"message\": \"Success message\",
>     \"data\": { ... }
> }
> ```
> 
> Java class:
> ```java
> public class GenericResponse<T> {
>     public boolean success;
>     public String message;
>     public T data;  // Generic type
> }
> ```
> 
> Lợi ích:
> - Tất cả API response có cấu trúc chung
> - Easy error handling: check success flag
> - Type-safe với generic T"

---

## 📚 PHẦN 3: REALTIME CHAT

### Q7: Ứng dụng nhận tin nhắn realtime như thế nào?

**Trả lời:**
> "Sử dụng **Auto-Refresh Polling**:
> 
> 1. **Setup** khi activity mở:
>    ```java
>    setupAutoRefresh() {
>        pollingHandler = new Handler(Looper.getMainLooper());
>        pollingRunnable = new Runnable() {
>            public void run() {
>                autoLoadMessageHistory();  // Fetch tin
>                postDelayed(this, 2000);  // Lặp lại sau 2 giây
>            }
>        };
>    }
>    ```
> 
> 2. **Bắt đầu** khi resume:
>    ```java
>    onResume() {
>        pollingHandler.postDelayed(pollingRunnable, 2000);
>    }
>    ```
> 
> 3. **Dừng** khi pause (tiết kiệm pin):
>    ```java
>    onPause() {
>        pollingHandler.removeCallbacks(pollingRunnable);
>    }
>    ```
> 
> 4. **Fetch** tin mỗi 2 giây:
>    ```java
>    autoLoadMessageHistory() {
>        apiService.getMessageHistory(...).enqueue(callback);
>    }
>    ```
> 
> Lợi ích:
> - ✅ Simple, không cần WebSocket
> - ✅ Hoạt động với API REST thường
> - ❌ Delay tối đa 2 giây (acceptable)"

---

### Q8: Tại sao phải dừng polling khi pause?

**Trả lời:**
> "Để tiết kiệm pin:
> 
> **Nếu KHÔNG dừng:**
> - Khi user rời app, vẫn call API mỗi 2 giây
> - Pin tổn hao rất nhanh (API + network)
> - Bạn sẽ bị complain người dùng 😠
> 
> **Nếu dừng (đúng cách):**
> - Khi user minimize app → onPause() → removeCallbacks()
> - Không call API nữa
> - Pin được tiết kiệm
> - Khi quay lại → onResume() → postDelayed()
> - Lại bắt đầu polling"

---

### Q9: Optimistic update là gì? Tại sao dùng?

**Trả lời:**
> "Optimistic update = **Thêm tin vào UI ngay trước khi API confirm**
> 
> **Sai cách (không optimistic):**
> ```java
> btnSend.click() 
>     → gọi API 
>     → chờ 2 giây 
>     → response về 
>     → mới thêm tin vào UI
> // ← User phải chờ 2 giây mới thấy tin gửi của mình
> ```
> 
> **Đúng cách (optimistic):**
> ```java
> btnSend.click() 
>     → thêm tin vào UI ngay (optimistic)
>     → user thấy tin ngay! ✅
>     → API call (async)
>     → 2 giây sau response về
>     → reload từ server (replace optimistic)
> // ← User thấy tin ngay, không phải chờ
> ```
> 
> Nếu API fail:
> - Reload từ server → xóa optimistic message
> - Hiển thị error toast
> 
> Lợi ích: UX linh hoạt, tương tự WhatsApp/Messenger"

---

## 📚 PHẦN 4: LOGIN & SESSION

### Q10: Quy trình login như thế nào?

**Trả lời:**
> "Quy trình:
> 
> 1. User nhập email + password
> 
> 2. Activity validate:
>    ```java
>    if (email.isEmpty()) return;
>    if (password.length() < 6) return;
>    ```
> 
> 3. Gọi API:
>    ```java
>    authRepository.login(email, password, callback)
>    ```
> 
> 4. Backend trả về:
>    ```json
>    {
>        \"token\": \"JWT_TOKEN\",
>        \"userId\": \"GUID\",
>        \"userName\": \"Nguyễn Văn A\",
>        \"userType\": \"tenant\"
>    }
>    ```
> 
> 5. Lưu vào SessionManager:
>    ```java
>    sessionManager.saveToken(token);  // Lưu JWT
>    sessionManager.createLoginSession(userId, name, email, type);
>    UserCache.addUser(userId, name);  // Cache tên
>    ```
> 
> 6. Redirect tùy role:
>    ```java
>    if (\"landlord\".equals(userType)) {
>        startActivity(new Intent(this, LandlordActivity.class));
>    } else {
>        startActivity(new Intent(this, TenantActivity.class));
>    }
>    ```"

---

### Q11: JWT token để làm gì?

**Trả lời:**
> "JWT token là Authorization:
> 
> **Khi login:**
> - Backend tạo JWT token chứa user info
> - App lưu token vào SessionManager
> 
> **Khi gọi API:**
> - Thêm token vào Authorization header
> ```java
> // Interceptor trong Retrofit
> request.addHeader(\"Authorization\", \"Bearer \" + token);
> ```
> 
> **Backend xác thực:**
> - Verify signature
> - Extract user ID từ token
> - Xác nhận người dùng hợp lệ
> 
> **Lợi ích:**
> - ✅ Secure: Token có signature
> - ✅ Stateless: Backend không cần lưu session
> - ✅ Scalable: Có thể scale server"

---

### Q12: Tại sao phải extract userId từ JWT?

**Trả lời:**
> "Vì:
> 
> 1. **Không lưu userId trực tiếp** (vì có thể đổi):
>    - App login → token được tạo
>    - Backend xác thực → extract userId từ token
> 
> 2. **Code để extract:**
>    ```java
>    public String extractUserIdFromJWT(String token) {
>        String[] parts = token.split(\"\\\\.\");  // 3 parts
>        byte[] decoded = Base64.decode(parts[1], Base64.DEFAULT);
>        String payload = new String(decoded);  // JSON
>        JSONObject json = new JSONObject(payload);
>        return json.getString(\"nameid\");  // Extract claim
>    }
>    ```
> 
> 3. **Lưu vào session:**
>    ```java
>    String userId = extractUserIdFromJWT(token);
>    sessionManager.putString(\"userId\", userId);
>    ```
> 
> 4. **Lần sau gọi API dùng userId:**
>    ```java
>    String userId = sessionManager.getUserId();
>    chatRepository.getMessageHistory(userId, ...);
>    ```"

---

## 📚 PHẦN 5: DANH SÁCH PHÒNG

### Q13: Cách load danh sách phòng?

**Trả lời:**
> "Quy trình:
> 
> 1. **Activity gọi Repository:**
>    ```java
>    roomRepository.getRooms(page, size, minPrice, maxPrice, callback)
>    ```
> 
> 2. **Repository gọi API:**
>    ```java
>    apiService.getRooms(page, size, minPrice, maxPrice)
>        .enqueue(new Callback<GenericResponse<List<Object>>>() {
>            public void onResponse(...) {
>                // Convert từ JSON sang Room objects
>                List<Room> rooms = new ArrayList<>();
>                for (Object obj : response.body().data) {
>                    Room room = gson.fromJson(gson.toJson(obj), Room.class);
>                    rooms.add(room);
>                }
>                callback.onSuccess(rooms);
>            }
>        });
>    ```
> 
> 3. **Activity update adapter:**
>    ```java
>    runOnUiThread(() -> {
>        roomAdapter.updateRooms(rooms);
>    });
>    ```
> 
> 4. **Adapter display:**
>    ```java
>    public void onBindViewHolder(ViewHolder holder, int pos) {
>        Room room = rooms.get(pos);
>        holder.bind(room);  // Display price, name, area
>    }
>    ```"

---

### Q14: Tại sao cần convert từ JSON sang Room object?

**Trả lời:**
> "Vì:
> 
> 1. **API trả về raw JSON:**
>    ```json
>    {
>        \"PhongTroId\": \"123\",
>        \"TenPhong\": \"Phòng A\",
>        \"GiaTien\": 3000000,
>        \"DienTich\": 25.5
>    }
>    ```
> 
> 2. **Java cần typed objects:**
>    ```java
>    public class Room {
>        public String id;
>        public String name;
>        public long price;
>        public float area;
>    }
>    ```
> 
> 3. **Gson convert:**
>    ```java
>    Gson gson = new Gson();
>    Room room = gson.fromJson(jsonString, Room.class);
>    // ← Tự match field name + convert type
>    ```
> 
> 4. **Lợi ích:**
>    - Type-safe: Có thể gọi room.getPrice() (autocomplete)
>    - Easier: Không cần parse JSON thủ công
>    - Less error-prone"

---

## 📚 PHẦN 6: DISPLAY NAMES

### Q15: Tại sao không hiển thị user ID mà phải show tên?

**Trả lời:**
> "Vì UX:
> 
> **Bad UX (show ID):**
> ```
> Chat header: 550e8400-e29b-41d4-a716-446655440000
> Tin nhắn: 550e8400-e29b-41d4-a716-446655440000: Hello
> // ← Khó đọc, không friendly
> ```
> 
> **Good UX (show name):**
> ```
> Chat header: Nguyễn Văn A
> Tin nhắn: Nguyễn Văn A: Hello
> // ← Clear, easy to read
> ```
> 
> **Cách làm:**
> 1. API không trả tên (chỉ có ID)
> 2. Dùng UserCache để lưu tên:
>    ```java
>    UserCache.addUser(userId, \"Nguyễn Văn A\");
>    ```
> 3. Khi display:
>    ```java
>    String name = UserCache.getUserName(userId);
>    textView.setText(name);  // Show tên, not ID
>    ```"

---

### Q16: Sao phải cache tên? Không lấy từ API lần nào được?

**Trả lời:**
> "Vì performance:
> 
> **Nếu KHÔNG cache:**
> ```java
> // Mỗi tin nhắn gọi API 1 lần
> for (ChatMessage msg : messages) {
>     User user = apiService.getUser(msg.fromUser).execute();
>     display(user.name);  // ← N tin = N API calls
> }
> // 10 tin = 10 API calls = 20 giây chậm 😡
> ```
> 
> **Nếu cache (đúng):**
> ```java
> // Login: cache tên
> UserCache.addUser(userId, userName);
> 
> // Display: lấy từ cache
> String name = UserCache.getUserName(userId);  // O(1) instant
> display(name);
> // 10 tin = 0 API calls = instant ✅
> ```
> 
> **Lợi ích:**
> - ✅ Instant display (O(1) lookup)
> - ✅ Giảm API calls
> - ✅ Không phải chờ network"

---

## 📚 PHẦN 7: CODE STRUCTURE

### Q17: Tại sao phải có try-catch?

**Trả lời:**
> "Để bắt lỗi:
> 
> **Nếu KHÔNG try-catch:**
> ```java
> public void sendMessage(String userId, ...) {
>     Map<String, Object> request = new HashMap<>();
>     request.put(\"FromUserId\", userId);
>     // ← Nếu null → NullPointerException → App crash
>     apiService.sendMessage(request).enqueue(...);
> }
> ```
> 
> **Nếu có try-catch (đúng):**
> ```java
> public void sendMessage(String userId, ...) {
>     try {
>         if (userId == null) {
>             callback.onError(\"UserId không hợp lệ\");
>             return;
>         }
>         Map<String, Object> request = new HashMap<>();
>         request.put(\"FromUserId\", userId);
>         apiService.sendMessage(request).enqueue(...);
>     } catch (Exception e) {
>         callback.onError(\"Exception: \" + e.getMessage());
>         // ← Graceful error, không crash
>     }
> }
> ```"

---

### Q18: Tại sao phải dùng runOnUiThread?

**Trả lời:**
> "Vì:
> 
> **Callback chạy trên background thread:**
> ```java
> apiService.getMessages(...).enqueue(new Callback() {
>     public void onResponse(...) {
>         // ← Thread này không phải Main Thread!
>         recyclerView.setAdapter(adapter);  // ← CRASH!
>     }
> });
> // ← Error: Only main thread can update UI
> ```
> 
> **Phải dùng runOnUiThread:**
> ```java
> apiService.getMessages(...).enqueue(new Callback() {
>     public void onResponse(...) {
>         runOnUiThread(() -> {
>             recyclerView.setAdapter(adapter);  // ← OK, main thread
>         });
>     }
> });
> ```
> 
> **Lý do:**
> - Android rule: Chỉ Main Thread được update UI
> - Network call (Retrofit) chạy background thread
> - Phải post back to main thread"

---

## 📚 PHẦN 8: CÓ THỂỈNH XÓA CODE

### Q19: Có thể xóa Log.d statements?

**Trả lời:**
> "Có được, vì chỉ là debug:
> 
> ```java
> // ❌ CÓ THỂ XÓA
> Log.d(TAG, \"✅ Chat initialized\");
> Log.d(TAG, \"Loading messages...\");
> 
> // Vẫn hoạt động bình thường mà không log
> ```
> 
> Nhưng **GIỮ lại Log.e (error):**
> ```java
> // ⚠️ NÊN GIỮ (tracking errors)
> Log.e(TAG, \"❌ Failed to send: \" + error);
> ```"

---

### Q20: Có thể xóa validation không?

**Trả lời:**
> "KHÔNG, sẽ crash:
> 
> ```java
> // ❌ KHÔNG XÓA ĐƯỢC
> if (currentUserId == null || currentUserId.isEmpty()) {
>     Toast.makeText(this, \"Lỗi\", LENGTH_SHORT).show();
>     return;
> }
> 
> // Nếu xóa:
> chatRepository.getHistory(currentUserId, ...);
> // ← Gọi API với null → Backend error → Crash
> ```"

---

### Q21: Có thể xóa onPause không?

**Trả lời:**
> "KHÔNG, sẽ tốn pin:
> 
> ```java
> // ❌ KHÔNG XÓA
> @Override
> protected void onPause() {
>     super.onPause();
>     if (pollingHandler != null) {
>         pollingHandler.removeCallbacks(pollingRunnable);
>     }
> }
> 
> // Nếu xóa:
> - User rời app
> - Polling vẫn chạy mỗi 2 giây
> - Pin chảy rất nhanh
> - Người dùng complain
> ```"

---

## 🎓 TỔNG KẾT

### Checklist cho báo cáo:

- [x] Giải thích kiến trúc 3-layer
- [x] Giải thích Repository pattern
- [x] Giải thích Retrofit + Async + Callback
- [x] Giải thích Polling realtime
- [x] Giải thích Optimistic update
- [x] Giải thích Login + JWT
- [x] Giải thích UserCache
- [x] Giải thích runOnUiThread
- [x] Biết được gì XÓA được, gì KHÔNG được xóa

### Các điểm chính:
1. **Async**: Mọi API call phải async (không block UI)
2. **Callback**: Response xử lý trong callback
3. **UI Thread**: Update UI luôn dùng runOnUiThread
4. **Lifecycle**: Dừng polling khi pause (pin)
5. **Optimistic**: Thêm UI trước confirm (UX)
6. **Cache**: Cache tên để tránh API calls
7. **Validation**: Luôn check null trước gọi API
8. **Error handling**: Dùng try-catch + callback onError

