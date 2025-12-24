package com.example.QuanLyPhongTro_App.ui.tenant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.data.ChatThread;
import com.example.QuanLyPhongTro_App.data.MockData;
import com.example.QuanLyPhongTro_App.utils.SessionManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChatListActivity extends AppCompatActivity {

    private RecyclerView recyclerViewChatList;
    private SessionManager sessionManager;
    private String currentUserEmail;
    private String currentUserName;
    private boolean isTenant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        sessionManager = new SessionManager(this);
        initViews();
        loadChatList();
    }

    private void initViews() {
        recyclerViewChatList = findViewById(R.id.recycler_view_chat_list);
        recyclerViewChatList.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadChatList() {
        // Lấy thông tin người dùng đã đăng nhập
        try {
            // use email as current user id
            currentUserEmail = sessionManager.getUserEmail();
            currentUserName = sessionManager.getUserName();
            isTenant = "tenant".equals(sessionManager.getUserType());

            android.util.Log.d("ChatList", "[DEBUG] start loadChatList - userEmail=" + currentUserEmail + ", userName=" + currentUserName + ", isTenant=" + isTenant);

            List<ChatThread> chatThreads = new ArrayList<>();

            if (isTenant) {
                // Tenant - lấy danh sách chủ trọ từ các phòng
                chatThreads = createTenantChatThreads();
            } else {
                // Landlord - lấy danh sách người thuê (from MockData bookings)
                android.util.Log.d("ChatList", "[DEBUG] calling MockData.getChatThreadsForLandlord with " + currentUserEmail);
                chatThreads = MockData.getChatThreadsForLandlord(currentUserEmail);
                android.util.Log.d("ChatList", "[DEBUG] got " + chatThreads.size() + " threads for landlord");
            }

            if (chatThreads.isEmpty()) {
                Toast.makeText(this, "Chưa có cuộc trò chuyện nào", Toast.LENGTH_SHORT).show();
            }

            ChatListAdapter chatListAdapter = new ChatListAdapter(chatThreads, currentUserEmail, thread -> {
                Intent intent = new Intent(ChatListActivity.this, ChatActivity.class);
                intent.putExtra("thread_id", thread.getThreadId());
                intent.putExtra("user_id", currentUserEmail);
                startActivity(intent);
            });

            recyclerViewChatList.setAdapter(chatListAdapter);

        } catch (Exception e) {
            android.util.Log.e("ChatList", "Error loading chat: ", e);
            Toast.makeText(this, "Lỗi tải chat: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Tạo danh sách chat thread cho Tenant
     * - Lấy từ danh sách phòng (MockData)
     * - Mỗi phòng = 1 chủ trọ = 1 thread chat
     */
    private List<ChatThread> createTenantChatThreads() {
        List<ChatThread> threads = new ArrayList<>();
        List<Room> rooms = MockData.getRooms();
        android.util.Log.d("ChatList", "[DEBUG] createTenantChatThreads - rooms count=" + rooms.size());
        Set<String> seenLandlords = new HashSet<>();

        for (Room room : rooms) {
            String landlordName = room.getLandlordName();
            android.util.Log.d("ChatList", "[DEBUG] room landlordName=" + landlordName);

            // Map landlordName -> landlordEmail
            String landlordEmail = MockData.findEmailByFullNameAndType(landlordName, "landlord");
            android.util.Log.d("ChatList", "[DEBUG] mapped landlordEmail=" + landlordEmail);
            if (landlordEmail == null) continue;

            // Tránh duplicate chủ trọ
            if (!seenLandlords.contains(landlordEmail)) {
                seenLandlords.add(landlordEmail);

                // Tạo thread chat
                ChatThread thread = MockData.createChatThread(
                        currentUserEmail,
                        landlordEmail
                );

                // Thêm tin nhắn mẫu
                if (thread.getMessages().isEmpty()) {
                    MockData.sendChatMessage(
                            thread.getThreadId(),
                            currentUserEmail,
                            "Xin chào, tôi quan tâm đến các phòng của bạn"
                    );
                }

                threads.add(thread);
            }
        }

        android.util.Log.d("ChatList", "[DEBUG] createTenantChatThreads - threads created=" + threads.size());
        return threads;
    }
}
