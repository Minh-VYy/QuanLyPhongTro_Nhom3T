package com.example.QuanLyPhongTro_App.ui.tenant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.data.response.ChatThreadDto;
import com.example.QuanLyPhongTro_App.data.repository.ChatThreadRepository;
import com.example.QuanLyPhongTro_App.utils.SessionManager;

import java.util.List;

public class ChatListActivity extends AppCompatActivity {
    private static final String TAG = "ChatListActivity";

    private RecyclerView recyclerViewChatList;
    private ChatThreadListAdapter adapter;
    private String currentUserEmail;
    private String userRole;
    private SessionManager sessionManager;
    private ChatThreadRepository chatThreadRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        sessionManager = new SessionManager(this);
        chatThreadRepository = new ChatThreadRepository();
        initViews();
        loadChatList();
    }

    private void initViews() {
        recyclerViewChatList = findViewById(R.id.recycler_view_chat_list);
        recyclerViewChatList.setLayoutManager(new LinearLayoutManager(this));

        currentUserEmail = sessionManager.getUserEmail();
        userRole = sessionManager.getUserRole();

        if (currentUserEmail == null) {
            currentUserEmail = getIntent().getStringExtra("user_id");
        }
        if (userRole == null) {
            userRole = getIntent().getStringExtra("user_role");
            if (userRole == null) {
                userRole = "tenant";
            }
        }

        Log.d(TAG, "Current user: " + currentUserEmail + ", Role: " + userRole);

        // Check if user is logged in
        if (currentUserEmail == null || currentUserEmail.isEmpty()) {
            Log.e(TAG, "❌ User email is null or empty");
            Toast.makeText(this, "Vui lòng đăng nhập trước", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    private void loadChatList() {
        Log.d(TAG, "========== Loading Chat Threads ==========");

        // Get userId (GUID) from session - must be actual GUID from JWT
        String userId = sessionManager.getUserId();
        String userEmail = sessionManager.getUserEmail();

        Log.d(TAG, "userId from session: " + userId);
        Log.d(TAG, "userEmail from session: " + userEmail);

        // ⚠️ CRITICAL: userId MUST be actual GUID from JWT token
        // C# backend expects GUID format: "00000000-0000-0000-0000-000000000000"
        // NOT email, NOT generated UUID

        if (userId == null || userId.isEmpty()) {
            Log.e(TAG, "❌ userId is NULL - JWT token not extracted!");
            Log.e(TAG, "This means login didn't properly save/extract JWT token");

            // Try to re-extract from current token
            String token = sessionManager.getToken();
            if (token != null && !token.isEmpty()) {
                Log.d(TAG, "Attempting to re-save token to extract userId...");
                sessionManager.saveToken(token); // This will trigger extraction
                userId = sessionManager.getUserId();
                Log.d(TAG, "After re-extraction, userId: " + userId);
            }

            if (userId == null || userId.isEmpty()) {
                Log.e(TAG, "❌ FATAL: Cannot get userId even after re-extraction");
                Toast.makeText(this,
                    "❌ Lỗi: Không thể xác định user ID từ token.\nVui lòng đăng nhập lại.",
                    Toast.LENGTH_LONG).show();

                // Redirect to login
                Intent intent = new Intent(this, com.example.QuanLyPhongTro_App.ui.auth.LoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        }

        // Validate userId format (must be valid GUID)
        if (!isValidGUID(userId)) {
            Log.e(TAG, "❌ userId is not a valid GUID: " + userId);
            Toast.makeText(this,
                "❌ Lỗi: User ID không hợp lệ.\nVui lòng đăng nhập lại.",
                Toast.LENGTH_LONG).show();

            // Redirect to login
            Intent intent = new Intent(this, com.example.QuanLyPhongTro_App.ui.auth.LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        final String finalUserId = userId;
        Log.d(TAG, "✅ Final userId (GUID): " + finalUserId);
        Log.d(TAG, "Calling: GET /api/Chat/contacts?userId=" + finalUserId);

        chatThreadRepository.getChatThreads(finalUserId, new ChatThreadRepository.ThreadsCallback() {
            @Override
            public void onSuccess(List<ChatThreadDto> threads) {
                Log.d(TAG, "✅ SUCCESS: Got " + threads.size() + " threads");
                runOnUiThread(() -> {
                    if (threads.isEmpty()) {
                        Toast.makeText(ChatListActivity.this, "Chưa có cuộc trò chuyện nào", Toast.LENGTH_SHORT).show();
                    }

                    adapter = new ChatThreadListAdapter(threads, thread -> {
                        Intent intent = new Intent(ChatListActivity.this, ChatActivity.class);
                        intent.putExtra("thread_id", thread.getThreadId());
                        intent.putExtra("user_id", finalUserId);
                        intent.putExtra("user_name", sessionManager.getUserName());  // ✅ Pass current user name
                        intent.putExtra("other_user_id", thread.getOtherUserId());  // ✅ FIXED: Use generic otherUserId
                        intent.putExtra("other_user_name", thread.getLandlordName());
                        startActivity(intent);
                    });

                    recyclerViewChatList.setAdapter(adapter);
                });
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "❌ FAILED - Error: " + error);
                runOnUiThread(() -> {
                    Toast.makeText(ChatListActivity.this, "Lỗi: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    /**
     * Validate if string is a valid GUID format
     * Format: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
     */
    private boolean isValidGUID(String guid) {
        if (guid == null || guid.isEmpty()) {
            return false;
        }

        // GUID regex pattern
        String guidPattern = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
        return guid.matches(guidPattern);
    }
}

