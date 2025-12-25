# 📚 KIẾN TRÚC ỨNG DỤNG & HƯỚNG DẪN CODE CHI TIẾT

## 🎯 Tổng Quan

Ứng dụng "Quản Lý Phòng Trọ" được xây dựng với kiến trúc **3 lớp (3-Layer Architecture)**:
- **Presentation Layer**: Activity + Adapter (Giao diện)
- **Business Logic Layer**: Repository (Xử lý logic)
- **Data Layer**: API Service + SessionManager (Dữ liệu)

---

## 📋 PHẦN 1: KIẾN TRÚC GỌIMANA API

### 1.1 ApiService.java - Định nghĩa Endpoints

**Vị trí:** `app/src/main/java/.../utils/ApiService.java`

**Mục đích:** Interface Retrofit định nghĩa tất cả endpoints API

```java
public interface ApiService {
    
    // ==================== AUTH ====================
    @POST("/api/nguoidung/login")
    Call<LoginResponse> login(@Body LoginRequest request);
    
    @POST("/api/nguoidung/register")
    Call<GenericResponse<Object>> register(@Body RegisterRequest request);
    
    @GET("/api/nguoidung/me")
    Call<GenericResponse<Object>> getUserProfile();
    
    // ==================== PHÒNG ====================
    @GET("/api/phong")
    Call<GenericResponse<List<Object>>> getRooms(
        @Query("page") int page,
        @Query("pageSize") int pageSize,
        @Query("minPrice") long minPrice,
        @Query("maxPrice") long maxPrice
    );
    
    // ==================== CHAT ====================
    @POST("/api/Chat/send")  // ⚠️ Capital C
    Call<GenericResponse<Object>> sendMessage(@Body Object messageRequest);
    
    @GET("/api/Chat/history")
    Call<List<Object>> getMessageHistory(
        @Query("user1") String user1,
        @Query("user2") String user2,
        @Query("page") int page,
        @Query("pageSize") int pageSize
    );
}
```

**Giải thích:**
- `@POST/@GET` - HTTP method
- `@Body` - Dữ liệu gửi đi (JSON)
- `@Query` - Tham số URL query string
- `@Path` - Tham số trong URL path
- `Call<T>` - Response type (Callback)

**Có thể xóa được gì?**
- ❌ KHÔNG được xóa `@POST`, `@GET` - cần để gọi API
- ❌ KHÔNG được xóa `@Body` - cần để gửi dữ liệu
- ✅ Có thể xóa endpoint không dùng (nhưng ảnh hưởng khi cần)

---

### 1.2 ApiClient.java - Khởi tạo Retrofit

**Vị trí:** `app/src/main/java/.../utils/ApiClient.java`

```java
public class ApiClient {
    private static final String BASE_URL = "http://localhost:7039/";
    private static Retrofit retrofit = null;
    
    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        }
        return retrofit;
    }
}
```

**Giải thích:**
- `BASE_URL` - URL server API
- `Retrofit.Builder()` - Cấu hình Retrofit
- `GsonConverterFactory` - Convert JSON ↔ Object
- `getRetrofit()` - Singleton pattern (tạo 1 lần duy nhất)

**Có thể xóa được gì?**
- ❌ KHÔNG xóa `GsonConverterFactory` - cần convert JSON
- ✅ Có thể thay `BASE_URL` thành URL khác

---

## 📋 PHẦN 2: REPOSITORY PATTERN

### 2.1 ChatRepository.java - Xử lý Chat Logic

**Vị trí:** `app/src/main/java/.../data/repository/ChatRepository.java`

**Mục đích:** Trung gian giữa Activity và API

```java
public class ChatRepository {
    private ApiService apiService;
    
    public ChatRepository() {
        this.apiService = ApiClient.getRetrofit().create(ApiService.class);
    }
    
    // ==================== SEND MESSAGE ====================
    public void sendMessage(String fromUserId, String toUserId, 
                           String content, ChatCallback callback) {
        try {
            // [1] Validate dữ liệu
            if (fromUserId == null || fromUserId.isEmpty()) {
                callback.onError("Lỗi: Người gửi không hợp lệ");
                return;
            }
            
            // [2] Tạo request body
            Map<String, Object> messageRequest = new HashMap<>();
            messageRequest.put("FromUserId", fromUserId);
            messageRequest.put("ToUserId", toUserId);
            messageRequest.put("Content", content);
            messageRequest.put("MessageType", "text");
            
            // [3] Gọi API
            apiService.sendMessage(messageRequest)
                .enqueue(new Callback<GenericResponse<Object>>() {
                
                @Override
                public void onResponse(Call<...> call, Response<...> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().success) {
                            // [4a] Thành công
                            callback.onSuccess("Message sent");
                        } else {
                            // [4b] Server trả về false
                            callback.onError(response.body().message);
                        }
                    } else {
                        // [4c] HTTP error
                        callback.onError("HTTP " + response.code());
                    }
                }
                
                @Override
                public void onFailure(Call<...> call, Throwable t) {
                    // [4d] Network error
                    callback.onError("Network: " + t.getMessage());
                }
            });
            
        } catch (Exception e) {
            callback.onError("Exception: " + e.getMessage());
        }
    }
    
    // ==================== GET MESSAGE HISTORY ====================
    public void getMessageHistory(String user1, String user2, 
                                  HistoryCallback callback) {
        try {
            // [1] Validate
            if (user2 == null || user2.isEmpty()) {
                callback.onError("User ID không hợp lệ");
                return;
            }
            
            // [2] Gọi API
            apiService.getMessageHistory(user1, user2, 1, 50)
                .enqueue(new Callback<List<Object>>() {
                
                @Override
                public void onResponse(Call<List<Object>> call, 
                                      Response<List<Object>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        // [3] Convert raw JSON to ChatMessage objects
                        List<ChatMessage> messages = new ArrayList<>();
                        Gson gson = new Gson();
                        
                        for (Object msg : response.body()) {
                            ChatMessage chatMsg = gson.fromJson(
                                gson.toJson(msg), 
                                ChatMessage.class
                            );
                            messages.add(chatMsg);
                        }
                        
                        callback.onSuccess(messages);
                    } else {
                        callback.onError("HTTP " + response.code());
                    }
                }
                
                @Override
                public void onFailure(Call<List<Object>> call, Throwable t) {
                    callback.onError("Network error: " + t.getMessage());
                }
            });
            
        } catch (Exception e) {
            callback.onError("Exception: " + e.getMessage());
        }
    }
    
    // ==================== CALLBACKS ====================
    public interface ChatCallback {
        void onSuccess(String message);
        void onError(String error);
    }
    
    public interface HistoryCallback {
        void onSuccess(List<ChatMessage> messages);
        void onError(String error);
    }
}
```

**Bước-by-bước (Flow):**
1. **Validate** - Kiểm tra dữ liệu đầu vào
2. **Tạo request** - Chuẩn bị dữ liệu gửi API
3. **Gọi API** - Sử dụng Retrofit async
4. **Xử lý response** - Có 4 trường hợp:
   - `onResponse()` + `success=true` → Callback `onSuccess()`
   - `onResponse()` + `success=false` → Callback `onError()`
   - `onResponse()` + HTTP error → Callback `onError()`
   - `onFailure()` (network) → Callback `onError()`

**Có thể xóa được gì?**
- ❌ KHÔNG xóa validate - tránh null pointer exception
- ❌ KHÔNG xóa try-catch - bắt exception
- ❌ KHÔNG xóa callback - cần return data
- ✅ Có xóa debug log (Log.d) nếu không cần

---

### 2.2 RoomRepository.java - Xử lý Phòng Logic

```java
public class RoomRepository {
    private ApiService apiService;
    
    public RoomRepository() {
        this.apiService = ApiClient.getRetrofit().create(ApiService.class);
    }
    
    public void getRooms(int page, int pageSize, long minPrice, 
                        long maxPrice, RoomsCallback callback) {
        try {
            apiService.getRooms(page, pageSize, minPrice, maxPrice)
                .enqueue(new Callback<GenericResponse<List<Object>>>() {
                
                @Override
                public void onResponse(Call<...> call, Response<...> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().success) {
                            List<Room> rooms = new ArrayList<>();
                            Gson gson = new Gson();
                            
                            for (Object room : response.body().data) {
                                Room r = gson.fromJson(
                                    gson.toJson(room), 
                                    Room.class
                                );
                                rooms.add(r);
                            }
                            
                            callback.onSuccess(rooms);
                        } else {
                            callback.onError(response.body().message);
                        }
                    } else {
                        callback.onError("HTTP " + response.code());
                    }
                }
                
                @Override
                public void onFailure(Call<...> call, Throwable t) {
                    callback.onError("Network error: " + t.getMessage());
                }
            });
            
        } catch (Exception e) {
            callback.onError("Exception: " + e.getMessage());
        }
    }
    
    public interface RoomsCallback {
        void onSuccess(List<Room> rooms);
        void onError(String error);
    }
}
```

---

## 📋 PHẦN 3: REALTIME CHAT IMPLEMENTATION

### 3.1 ChatActivity.java - Activity Chính Chat

**Vị trí:** `app/src/main/java/.../ui/tenant/ChatActivity.java`

**Kiến trúc:**
```
ChatActivity
├─ UI Components
│  ├─ RecyclerView (chatList)
│  ├─ EditText (messageInput)
│  └─ Button (sendButton)
├─ Data
│  ├─ currentUserId
│  ├─ otherUserId
│  └─ ChatAdapter
├─ Logic
│  ├─ loadMessageHistory()
│  ├─ autoLoadMessageHistory()
│  ├─ sendMessage()
│  └─ setupAutoRefresh()
└─ Polling (Realtime)
   ├─ Handler
   ├─ Runnable
   └─ POLLING_INTERVAL = 2000ms
```

**Quy trình:**

#### 📍 onCreate() - Khởi tạo
```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chat);
    
    // [1] Khởi tạo thành phần
    sessionManager = new SessionManager(this);
    chatRepository = new ChatRepository();
    
    // [2] Thiết lập UI
    initViews();
    loadChatData();
    setupRecyclerView();
    setupSendButton();
    
    // [3] Thiết lập polling realtime
    setupAutoRefresh();
    
    // [4] Load dữ liệu ban đầu
    loadMessageHistory();
}
```

**Có thể xóa được gì?**
- ❌ KHÔNG xóa bất kỳ dòng nào - tất cả đều cần

---

#### 📍 setupAutoRefresh() - Thiết lập Polling

```java
private void setupAutoRefresh() {
    // [1] Tạo Handler (chạy UI thread)
    pollingHandler = new Handler(Looper.getMainLooper());
    
    // [2] Tạo Runnable (tác vụ lặp lại)
    pollingRunnable = new Runnable() {
        @Override
        public void run() {
            // [2a] Kiểm tra tin nhắn mới
            autoLoadMessageHistory();
            
            // [2b] Lên lịch chạy lại sau 2 giây
            pollingHandler.postDelayed(this, POLLING_INTERVAL);
        }
    };
}
```

**Giải thích:**
- `Handler` - Thực thi task trên main thread (UI thread)
- `Looper.getMainLooper()` - Main thread looper
- `Runnable` - Công việc cần thực hiện
- `postDelayed()` - Lên lịch sau X milliseconds

**Có thể xóa được gì?**
- ❌ KHÔNG xóa `Handler` - cần để chạy UI
- ❌ KHÔNG xóa `Looper` - cần UI thread
- ❌ KHÔNG xóa `postDelayed()` - cần lặp lại

---

#### 📍 onResume() - Bắt đầu Polling

```java
@Override
protected void onResume() {
    super.onResume();
    // [1] Bắt đầu polling khi activity hiển thị
    if (pollingHandler != null && pollingRunnable != null) {
        pollingHandler.postDelayed(pollingRunnable, POLLING_INTERVAL);
    }
}
```

**Mục đích:** Khi người dùng quay lại chat, bắt đầu kiểm tra tin mới

---

#### 📍 onPause() - Dừng Polling

```java
@Override
protected void onPause() {
    super.onPause();
    // [1] Dừng polling khi activity ẩn (tiết kiệm pin)
    if (pollingHandler != null && pollingRunnable != null) {
        pollingHandler.removeCallbacks(pollingRunnable);
    }
}
```

**Mục đích:** Khi người dùng rời khỏi chat, dừng kiểm tra (tiết kiệm pin)

---

#### 📍 loadMessageHistory() - Load Tin Ban Đầu

```java
private void loadMessageHistory() {
    // [1] Validate
    if (otherUserId == null || otherUserId.isEmpty()) {
        Toast.makeText(this, "Lỗi: Không xác định người nhận", 
            Toast.LENGTH_SHORT).show();
        return;
    }
    
    // [2] Gọi Repository (Async)
    chatRepository.getMessageHistory(currentUserId, otherUserId, 
        new ChatRepository.HistoryCallback() {
        
        @Override
        public void onSuccess(List<ChatMessage> messages) {
            // [3] Xử lý UI trên main thread
            runOnUiThread(() -> {
                // [3a] Convert từ API model sang local model
                List<ChatMessage> chatMessages = new ArrayList<>();
                for (ChatMessage msg : messages) {
                    // [3b] Lấy tên từ cache
                    String senderName = UserCache.getUserName(msg.fromUser);
                    String displayName = (senderName != null) 
                        ? senderName : msg.fromUser;
                    
                    // [3c] Tạo ChatMessage với tên
                    ChatMessage chatMsg = new ChatMessage(
                        msg.fromUser,
                        displayName,
                        !msg.fromUser.equals(currentUserId),
                        msg.noiDung
                    );
                    chatMessages.add(chatMsg);
                }
                
                // [3d] Cập nhật adapter
                chatAdapter.updateMessages(chatMessages);
                lastMessageCount = messages.size();
                
                // [3e] Scroll xuống dưới cùng
                if (chatAdapter.getItemCount() > 0) {
                    recyclerViewChat.scrollToPosition(
                        chatAdapter.getItemCount() - 1
                    );
                }
            });
        }
        
        @Override
        public void onError(String error) {
            Toast.makeText(ChatActivity.this, 
                "Lỗi tải tin: " + error, 
                Toast.LENGTH_LONG).show();
        }
    });
}
```

**Bước-by-bước:**
1. **Validate** - Kiểm tra otherUserId
2. **Async Call** - Gọi Repository (không block UI)
3. **onSuccess** - Khi API trả về dữ liệu
   - Convert từ API model sang local model
   - Thêm tên từ cache
   - Update adapter
   - Scroll xuống
4. **onError** - Nếu có lỗi
   - Hiển thị error toast

**Có thể xóa được gì?**
- ❌ KHÔNG xóa validate
- ❌ KHÔNG xóa runOnUiThread() - gây crash nếu update UI từ background thread
- ❌ KHÔNG xóa scroll
- ✅ Có xóa toast nếu không cần thông báo

---

#### 📍 autoLoadMessageHistory() - Load Realtime

```java
private void autoLoadMessageHistory() {
    // Tương tự loadMessageHistory() nhưng:
    // [1] KHÔNG hiển thị toast (silent mode)
    // [2] Chỉ update nếu có tin mới (so sánh count)
    
    chatRepository.getMessageHistory(currentUserId, otherUserId,
        new ChatRepository.HistoryCallback() {
        
        @Override
        public void onSuccess(List<ChatMessage> messages) {
            // [1] Chỉ update nếu có tin mới
            if (messages.size() > lastMessageCount) {
                Log.d(TAG, "Có tin mới: " + lastMessageCount + 
                    " -> " + messages.size());
            }
            
            runOnUiThread(() -> {
                // [2] Convert và update
                List<ChatMessage> chatMessages = new ArrayList<>();
                for (ChatMessage msg : messages) {
                    String senderName = UserCache.getUserName(msg.fromUser);
                    ChatMessage chatMsg = new ChatMessage(
                        msg.fromUser,
                        (senderName != null) ? senderName : msg.fromUser,
                        !msg.fromUser.equals(currentUserId),
                        msg.noiDung
                    );
                    chatMessages.add(chatMsg);
                }
                
                // [3] Update adapter
                chatAdapter.updateMessages(chatMessages);
                lastMessageCount = messages.size();
                
                // [4] Scroll
                if (chatAdapter.getItemCount() > 0) {
                    recyclerViewChat.scrollToPosition(
                        chatAdapter.getItemCount() - 1
                    );
                }
            });
        }
        
        @Override
        public void onError(String error) {
            // KHÔNG hiển thị toast - silent mode
            Log.e(TAG, "Auto-load failed: " + error);
        }
    });
}
```

**Khác với loadMessageHistory():**
- ❌ KHÔNG hiển thị toast
- ✅ Chỉ log lỗi
- ✅ Chỉ update khi có tin mới (lastMessageCount check)

---

#### 📍 sendMessage() - Gửi Tin

```java
private void sendMessage() {
    // [1] Lấy tin nhắn từ input
    String messageContent = etMessageInput.getText().toString().trim();
    
    // [2] Validate
    if (messageContent.isEmpty()) {
        Toast.makeText(this, "Vui lòng nhập tin nhắn", 
            Toast.LENGTH_SHORT).show();
        return;
    }
    
    // [3] Disable button (tránh double click)
    btnSendMessage.setEnabled(false);
    
    // [4] OPTIMISTIC UPDATE: Thêm tin vào UI ngay lập tức
    String displayName = (currentUserName != null) 
        ? currentUserName : currentUserId;
    ChatMessage optimisticMessage = new ChatMessage(
        currentUserId,
        displayName,
        false,  // false = tin gửi
        messageContent
    );
    chatAdapter.addMessage(optimisticMessage);
    recyclerViewChat.scrollToPosition(chatAdapter.getItemCount() - 1);
    
    // [5] Xóa input
    etMessageInput.setText("");
    
    // [6] Gọi API gửi tin (Async)
    chatRepository.sendMessage(currentUserId, otherUserId, 
        messageContent, new ChatRepository.ChatCallback() {
        
        @Override
        public void onSuccess(String message) {
            runOnUiThread(() -> {
                // [7a] Thành công
                btnSendMessage.setEnabled(true);
                Toast.makeText(ChatActivity.this, 
                    "Tin đã gửi", Toast.LENGTH_SHORT).show();
                
                // [7b] Reload để sync với server
                loadMessageHistory();
            });
        }
        
        @Override
        public void onError(String error) {
            runOnUiThread(() -> {
                // [7c] Lỗi
                btnSendMessage.setEnabled(true);
                Toast.makeText(ChatActivity.this, 
                    "Lỗi gửi: " + error, 
                    Toast.LENGTH_LONG).show();
                
                // [7d] Reload để xóa optimistic message
                loadMessageHistory();
            });
        }
    });
}
```

**Quy trình (Optimistic Update):**
1. Lấy nội dung
2. Validate
3. Disable button
4. **Thêm tin vào UI ngay** (optimistic)
5. Xóa input
6. Gọi API async
7. Khi API trả về:
   - **Success:** Reload từ server (replace optimistic)
   - **Error:** Reload để xóa optimistic

**Có thể xóa được gì?**
- ❌ KHÔNG xóa optimistic update - UX sẽ chậm
- ❌ KHÔNG xóa reload - cần sync
- ❌ KHÔNG xóa disable/enable button - tránh double send
- ✅ Có xóa toast nếu không cần

---

### 3.2 ChatAdapter.java - Hiển thị Tin

```java
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    
    private List<ChatMessage> messages;
    private String currentUserId;
    
    public ChatAdapter(List<ChatMessage> messages, String currentUserId) {
        this.messages = messages;
        this.currentUserId = currentUserId;
    }
    
    @Override
    public int getItemViewType(int position) {
        // [1] Determine: tin gửi hay nhận
        ChatMessage msg = messages.get(position);
        if (msg.getSenderId().equals(currentUserId)) {
            return VIEW_TYPE_SENT;  // Tin gửi - align right
        } else {
            return VIEW_TYPE_RECEIVED;  // Tin nhận - align left
        }
    }
    
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // [2] Tạo view holder tùy loại
        View view;
        if (viewType == VIEW_TYPE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_sent, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_received, parent, false);
        }
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // [3] Bind dữ liệu vào view
        ChatMessage message = messages.get(position);
        holder.bind(message);
    }
    
    @Override
    public int getItemCount() {
        return messages.size();
    }
    
    // [4] Update toàn bộ list
    public void updateMessages(List<ChatMessage> newMessages) {
        this.messages.clear();
        this.messages.addAll(newMessages);
        notifyDataSetChanged();  // Refresh toàn bộ
    }
    
    // [5] Thêm 1 tin (optimistic)
    public void addMessage(ChatMessage message) {
        this.messages.add(message);
        notifyItemInserted(this.messages.size() - 1);  // Efficient!
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvContent;
        private TextView tvName;
        
        ViewHolder(View view) {
            super(view);
            tvContent = view.findViewById(R.id.tv_message_content);
            tvName = view.findViewById(R.id.tv_sender_name);
        }
        
        void bind(ChatMessage message) {
            // [6] Hiển thị tên + nội dung
            tvName.setText(message.getSenderName());
            tvContent.setText(message.getContent());
        }
    }
}
```

**Giải thích:**
- `getItemViewType()` - Quyết định layout (sent vs received)
- `onCreateViewHolder()` - Tạo view từ layout
- `onBindViewHolder()` - Bind dữ liệu vào view
- `updateMessages()` - Replace toàn bộ (dùng khi reload)
- `addMessage()` - Thêm 1 (dùng khi optimistic)
- `notifyDataSetChanged()` vs `notifyItemInserted()` - Cái nào nhanh hơn?

**Có thể xóa được gì?**
- ❌ KHÔNG xóa `getItemViewType()` - cần để distinguish sent/received
- ❌ KHÔNG xóa `updateMessages()` - cần khi reload
- ❌ KHÔNG xóa `addMessage()` - cần khi optimistic
- ✅ Có xóa binding logic nếu không cần display tên

---

## 📋 PHẦN 4: DANH SÁCH PHÒNG

### 4.1 TenantListActivity.java - Liệt kê Phòng

```java
public class TenantListActivity extends AppCompatActivity {
    
    private RecyclerView recyclerView;
    private RoomAdapter adapter;
    private RoomRepository roomRepository;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_list);
        
        // [1] Khởi tạo
        recyclerView = findViewById(R.id.recycler_view);
        roomRepository = new RoomRepository();
        
        // [2] Thiết lập adapter
        adapter = new RoomAdapter(new ArrayList<>(), room -> {
            // [2a] Khi click vào room
            Intent intent = new Intent(this, RoomDetailActivity.class);
            intent.putExtra("room_id", room.getId());
            intent.putExtra("room_name", room.getTenPhong());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // [3] Load phòng
        loadRooms();
    }
    
    private void loadRooms() {
        // [1] Gọi repository
        roomRepository.getRooms(1, 10, 0, 10000000,
            new RoomRepository.RoomsCallback() {
            
            @Override
            public void onSuccess(List<Room> rooms) {
                // [2] Update adapter
                runOnUiThread(() -> {
                    adapter.updateRooms(rooms);
                });
            }
            
            @Override
            public void onError(String error) {
                // [3] Xử lý lỗi
                runOnUiThread(() -> {
                    Toast.makeText(TenantListActivity.this,
                        "Lỗi tải phòng: " + error,
                        Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}
```

---

## 📋 PHẦN 5: LOGIN & SESSION MANAGEMENT

### 5.1 LoginActivity.java - Đăng Nhập

```java
public class LoginActivity extends AppCompatActivity {
    
    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private SessionManager sessionManager;
    private AuthRepository authRepository;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        // [1] Khởi tạo
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        
        sessionManager = new SessionManager(this);
        authRepository = new AuthRepository();
        
        // [2] Check đã đăng nhập?
        if (sessionManager.isLoggedIn()) {
            redirectToDashboard();
            return;
        }
        
        // [3] Click login
        btnLogin.setOnClickListener(v -> handleLogin());
    }
    
    private void handleLogin() {
        // [1] Lấy dữ liệu
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        
        // [2] Validate
        if (email.isEmpty() || !email.contains("@")) {
            Toast.makeText(this, "Email không hợp lệ", 
                Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (password.isEmpty() || password.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải >= 6 ký tự", 
                Toast.LENGTH_SHORT).show();
            return;
        }
        
        // [3] Disable button (tránh double click)
        btnLogin.setEnabled(false);
        
        // [4] Gọi API login (Async)
        authRepository.login(email, password, 
            new AuthRepository.LoginCallback() {
            
            @Override
            public void onSuccess(LoginResponse response) {
                runOnUiThread(() -> {
                    // [5a] Thành công
                    // [5a-1] Lưu token
                    sessionManager.saveToken(response.token);
                    
                    // [5a-2] Lưu user info
                    sessionManager.createLoginSession(
                        response.userId,
                        response.userName,  // HoTen
                        response.email,
                        response.userType   // "tenant" hay "landlord"
                    );
                    
                    // [5a-3] Cache user name
                    UserCache.addUser(response.userId, response.userName);
                    
                    // [5a-4] Redirect
                    redirectToDashboard();
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    // [5b] Lỗi
                    btnLogin.setEnabled(true);
                    Toast.makeText(LoginActivity.this,
                        "Đăng nhập thất bại: " + error,
                        Toast.LENGTH_LONG).show();
                });
            }
        });
    }
    
    private void redirectToDashboard() {
        String userType = sessionManager.getUserType();
        
        if ("landlord".equals(userType)) {
            startActivity(new Intent(this, LandlordActivity.class));
        } else {
            startActivity(new Intent(this, TenantActivity.class));
        }
        
        finish();
    }
}
```

**Quy trình Login:**
1. Lấy email + password từ input
2. Validate
3. Disable button
4. Gọi API async
5. Khi thành công:
   - Lưu token (cho API authentication)
   - Lưu user info (userId, name, role)
   - Cache name (cho chat)
   - Redirect tùy role

---

### 5.2 SessionManager.java - Lưu Thông Tin User

```java
public class SessionManager {
    
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER_TYPE = "userType";
    
    public SessionManager(Context context) {
        pref = context.getSharedPreferences("UserSession", 
            Context.MODE_PRIVATE);
        editor = pref.edit();
    }
    
    // [1] Lưu thông tin login
    public void createLoginSession(String userId, String userName, 
                                   String email, String userType) {
        editor.putBoolean("isLoggedIn", true);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USER_NAME, userName);
        editor.putString("userEmail", email);
        editor.putString(KEY_USER_TYPE, userType);
        editor.apply();  // Commit to disk
    }
    
    // [2] Lấy user ID
    public String getUserId() {
        return pref.getString(KEY_USER_ID, null);
    }
    
    // [3] Lấy user name
    public String getUserName() {
        return pref.getString(KEY_USER_NAME, null);
    }
    
    // [4] Lấy token (để call API)
    public String getToken() {
        return pref.getString(KEY_TOKEN, null);
    }
    
    // [5] Lưu token
    public void saveToken(String token) {
        editor.putString(KEY_TOKEN, token);
        editor.apply();
        
        // [5a] Extract userId từ JWT
        String userId = extractUserIdFromJWT(token);
        if (userId != null) {
            editor.putString(KEY_USER_ID, userId);
            editor.apply();
        }
    }
    
    // [6] Kiểm tra đã login?
    public boolean isLoggedIn() {
        return pref.getBoolean("isLoggedIn", false);
    }
    
    // [7] Logout
    public void logout() {
        editor.clear();
        editor.apply();
        UserCache.clearCache();
    }
    
    private String extractUserIdFromJWT(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return null;
            
            byte[] decoded = Base64.decode(parts[1], Base64.DEFAULT);
            String payload = new String(decoded, "UTF-8");
            
            // Parse JSON để lấy "nameid"
            JSONObject json = new JSONObject(payload);
            return json.optString("nameid", null);
        } catch (Exception e) {
            Log.e("SessionManager", "Error extracting userId", e);
            return null;
        }
    }
}
```

**Giải thích:**
- `SharedPreferences` - Lưu data local (disk)
- `editor.putString()` - Set value
- `editor.apply()` - Commit async
- `isLoggedIn()` - Check đã đăng nhập
- `extractUserIdFromJWT()` - Parse token để lấy userId

**Có thể xóa được gì?**
- ❌ KHÔNG xóa `editor.apply()` - dữ liệu không lưu
- ❌ KHÔNG xóa `extractUserIdFromJWT()` - cần userId từ token
- ✅ Có xóa nếu không cần extract UUID từ JWT

---

## 📋 PHẦN 6: MODEL CLASSES

### 6.1 ChatMessage.java (Local)

```java
public class ChatMessage {
    private String id;
    private long timestamp;
    private String senderId;
    private String senderName;
    private boolean fromLandlord;
    private String content;
    
    public ChatMessage(String senderId, String senderName, 
                      boolean fromLandlord, String content) {
        this.id = UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
        this.senderId = senderId;
        this.senderName = senderName;
        this.fromLandlord = fromLandlord;
        this.content = content;
    }
    
    // Getters
    public String getSenderId() { return senderId; }
    public String getSenderName() { return senderName; }
    public String getContent() { return content; }
    // ...
}
```

---

### 6.2 ChatMessage.java (API Model)

```java
public class ChatMessage {
    @SerializedName("FromUserId")
    public String fromUser;
    
    @SerializedName("ToUserId")
    public String toUser;
    
    @SerializedName("NoiDung")
    public String noiDung;
    
    @SerializedName("ThoiGian")
    public String thoiGian;
    
    @SerializedName("DaDoc")
    public boolean daDoc;
}
```

**Lưu ý:** Có 2 ChatMessage classes - 1 local (UI), 1 API (từ server)

---

### 6.3 Room.java

```java
public class Room {
    @SerializedName("PhongTroId")
    public String id;
    
    @SerializedName("TenPhong")
    public String tenPhong;
    
    @SerializedName("GiaTien")
    public long giaTien;
    
    @SerializedName("DienTich")
    public float dienTich;
    
    // Getters
    public String getId() { return id; }
    public String getTenPhong() { return tenPhong; }
    public long getGiaTien() { return giaTien; }
}
```

---

### 6.4 LoginRequest / LoginResponse

```java
public class LoginRequest {
    @SerializedName("Email")
    public String email;
    
    @SerializedName("Password")
    public String password;
}

public class LoginResponse {
    @SerializedName("Token")
    public String token;
    
    @SerializedName("UserId")
    public String userId;
    
    @SerializedName("UserName")
    public String userName;
    
    @SerializedName("Email")
    public String email;
    
    @SerializedName("UserType")
    public String userType;
}
```

---

## 📋 PHẦN 7: USER CACHE

### 7.1 UserCache.java - Cache Tên Người Dùng

```java
public class UserCache {
    
    private static final Map<String, String> userNameCache = 
        new HashMap<>();
    
    // [1] Lưu tên vào cache
    public static void addUser(String userId, String userName) {
        if (userId != null && !userId.isEmpty() && 
            userName != null && !userName.isEmpty()) {
            userNameCache.put(userId, userName);
        }
    }
    
    // [2] Lấy tên từ cache
    public static String getUserName(String userId) {
        if (userId == null || userId.isEmpty()) {
            return "Ẩn danh";
        }
        
        // [2a] Có trong cache?
        if (userNameCache.containsKey(userId)) {
            return userNameCache.get(userId);
        }
        
        // [2b] Không có - trả về userId (fallback)
        return userId;
    }
    
    // [3] Xóa cache (logout)
    public static void clearCache() {
        userNameCache.clear();
    }
}
```

**Giải thích:**
- `HashMap` - O(1) lookup time
- `addUser()` - Cache tên khi login
- `getUserName()` - Lấy từ cache, fallback to ID
- `clearCache()` - Xóa khi logout

---

## 📋 PHẦN 8: LỘ TRÌNH HOÀN CHỈNH

### Flow: Login → Chat → Send/Receive

```
[1] LOGIN
    ↓
    User nhập email + password
    ↓
    LoginActivity.handleLogin()
    ↓
    AuthRepository.login(email, password)
    ↓
    API: POST /api/nguoidung/login
    ↓
    Response: { token, userId, userName, userType }
    ↓
    SessionManager.saveToken(token)  // Lưu token
    SessionManager.createLoginSession(...)  // Lưu user info
    UserCache.addUser(userId, userName)  // Cache tên
    ↓
    Redirect based on userType (tenant/landlord)
    
═══════════════════════════════════════════════════════

[2] OPEN CHAT LIST
    ↓
    ChatListActivity.loadChatList()
    ↓
    ChatThreadRepository.getChatThreads(userId)
    ↓
    API: GET /api/Chat/contacts?userId=...
    ↓
    Response: [{ ThreadId, OtherUserId, OtherUserName, ... }]
    ↓
    ChatThreadListAdapter displays threads
    ↓
    User clicks thread

═══════════════════════════════════════════════════════

[3] OPEN CHAT
    ↓
    Intent extras:
    ├─ user_id: currentUserId
    ├─ user_name: currentUserName (from session)
    ├─ other_user_id: otherUserId
    └─ other_user_name: otherUserName (from thread)
    ↓
    ChatActivity.onCreate()
    ↓
    Cache names:
    ├─ UserCache.addUser(currentUserId, currentUserName)
    └─ UserCache.addUser(otherUserId, otherUserName)
    ↓
    ChatActivity.loadMessageHistory()
    ↓
    ChatRepository.getMessageHistory(user1, user2)
    ↓
    API: GET /api/Chat/history?user1=...&user2=...
    ↓
    Response: [ { fromUser, noiDung }, ... ]
    ↓
    Convert with names from cache
    ↓
    ChatAdapter.updateMessages(messages)
    ↓
    Display in RecyclerView
    ↓
    ChatActivity.setupAutoRefresh()
    ↓
    Start polling (every 2 seconds)

═══════════════════════════════════════════════════════

[4] SEND MESSAGE
    ↓
    User types message + clicks send
    ↓
    ChatActivity.sendMessage()
    ↓
    [OPTIMISTIC] Immediately add to UI
    ├─ ChatAdapter.addMessage(optimisticMsg)
    ├─ Scroll to bottom
    └─ Clear input
    ↓
    ChatRepository.sendMessage(fromId, toId, content)
    ↓
    API: POST /api/Chat/send
    Body: { FromUserId, ToUserId, Content }
    ↓
    Response: { success: true/false }
    ↓
    On Success:
    ├─ ChatActivity.loadMessageHistory()
    ├─ Reload từ server (replace optimistic)
    └─ Toast: "Tin đã gửi"
    ↓
    On Error:
    ├─ ChatActivity.loadMessageHistory()
    ├─ Reload từ server (xóa optimistic)
    └─ Toast: "Lỗi gửi"

═══════════════════════════════════════════════════════

[5] RECEIVE MESSAGE (Auto-polling)
    ↓
    ChatActivity.onResume()
    ↓
    setupAutoRefresh() starts polling
    ↓
    Every 2 seconds:
    ├─ autoLoadMessageHistory()
    ├─ ChatRepository.getMessageHistory()
    ├─ Compare message count
    ├─ If new: Convert with names
    ├─ ChatAdapter.updateMessages()
    ├─ Scroll to bottom
    └─ Schedule next in 2 seconds
    ↓
    User sees message from other person
    ↓
    (Continue polling while activity is visible)

═══════════════════════════════════════════════════════

[6] BACKGROUND (Save Battery)
    ↓
    User switches app / minimizes
    ↓
    ChatActivity.onPause()
    ↓
    pollingHandler.removeCallbacks(pollingRunnable)
    ↓
    Polling STOPS
    ↓
    (No more API calls)

═══════════════════════════════════════════════════════

[7] LOGOUT
    ↓
    User clicks logout
    ↓
    SessionManager.logout()
    ├─ editor.clear()  // Xóa toàn bộ data
    └─ UserCache.clearCache()  // Xóa cache tên
    ↓
    Redirect to LoginActivity
```

---

## 📋 PHẦN 9: CÓ THỂ XÓA ĐƯỢC GÌ?

### ❌ KHÔNG Được Xóa (Essential)

```java
// API Call
❌ Call<T> callback pattern
❌ try-catch blocks
❌ Validation checks (if userId == null)
❌ Handler + Looper (polling)
❌ runOnUiThread() (UI updates)

// Chat Realtime
❌ onResume() + onPause() + onDestroy() lifecycle
❌ pollingHandler.removeCallbacks() (battery)
❌ ChatAdapter.updateMessages() (reload)
❌ ChatAdapter.addMessage() (optimistic)
❌ Optimistic update pattern

// Login
❌ SessionManager.saveToken()
❌ SessionManager.createLoginSession()
❌ extractUserIdFromJWT()
❌ editor.apply()

// Names
❌ UserCache (nếu muốn show names)
❌ UserCache.addUser() (cache)
❌ UserCache.getUserName() (display)
```

---

### ✅ Có Thể Xóa (Optional)

```java
// Log statements
✅ Log.d(TAG, "Debug message")
✅ Log.e(TAG, "Error message")

// Toast notifications
✅ Toast.makeText(...).show()  (có thể xóa nếu không cần feedback)

// Optional validations
✅ if (minPrice < 0) { ... }  (nếu backend validate)

// Cache methods (nếu show ID là được)
✅ UserCache.clearCache()
✅ UserCache.getCacheSize()
✅ UserCache.printCache()

// Không dùng endpoints
✅ Xóa getNotifications() từ ApiService nếu không dùng
```

---

## 🎓 Kết Luận

**Kiến trúc này gồm:**
1. **API Layer** (ApiService + ApiClient)
2. **Repository Layer** (ChatRepository + RoomRepository)
3. **Activity/UI Layer** (ChatActivity + TenantListActivity)
4. **Session Layer** (SessionManager + UserCache)

**Các thành phần chính:**
- ✅ **Async API calls** (Retrofit)
- ✅ **Callback pattern** (onSuccess + onError)
- ✅ **Optimistic updates** (instant feedback)
- ✅ **Real-time polling** (auto-refresh)
- ✅ **Session persistence** (SharedPreferences)
- ✅ **User name caching** (HashMap)

**Để báo cáo thầy:**
- Có thể xóa Log.d, Log.e, Toast (optional)
- KHÔNG được xóa gì trong API calls, callbacks, lifecycle
- KHÔNG được xóa optimistic update + polling (realtime)
- KHÔNG được xóa SessionManager + UserCache (authentication + UI)

