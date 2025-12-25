package com.example.QuanLyPhongTro_App.ui.landlord;

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
import com.example.QuanLyPhongTro_App.ui.tenant.ChatThreadListAdapter;
import com.example.QuanLyPhongTro_App.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class LandlordChatListActivity extends AppCompatActivity {
    private static final String TAG = "LandlordChatList";

    private RecyclerView recyclerViewChatList;
    private ChatThreadListAdapter adapter;
    private String currentUserEmail;
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
        if (currentUserEmail == null) {
            currentUserEmail = getIntent().getStringExtra("user_id");
        }

        Log.d(TAG, "Current landlord: " + currentUserEmail);

        // Check if user email is valid
        if (currentUserEmail == null || currentUserEmail.isEmpty()) {
            Log.e(TAG, "❌ User email is null or empty");
            Toast.makeText(this, "Vui lòng đăng nhập trước", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadChatList() {
        Log.d(TAG, "Loading chat threads from API...");

        // Get actual GUID from session, NOT hardcoded "landlord"
        String userId = sessionManager.getUserId();
        String userEmail = sessionManager.getUserEmail();

        Log.d(TAG, "userId from session: " + userId);
        Log.d(TAG, "userEmail from session: " + userEmail);

        // Validate userId
        if (userId == null || userId.isEmpty()) {
            Log.e(TAG, "❌ userId is NULL - JWT token not extracted!");

            // Try to re-extract from current token
            String token = sessionManager.getToken();
            if (token != null && !token.isEmpty()) {
                Log.d(TAG, "Attempting to re-save token to extract userId...");
                sessionManager.saveToken(token);
                userId = sessionManager.getUserId();
                Log.d(TAG, "After re-extraction, userId: " + userId);
            }

            if (userId == null || userId.isEmpty()) {
                Log.e(TAG, "❌ FATAL: Cannot get userId even after re-extraction");
                Toast.makeText(this,
                    "❌ Lỗi: Không thể xác định user ID từ token.\nVui lòng đăng nhập lại.",
                    Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        }

        final String finalUserId = userId;
        Log.d(TAG, "✅ Final userId (GUID): " + finalUserId);
        Log.d(TAG, "Calling: GET /api/Chat/contacts?userId=" + finalUserId);

        chatThreadRepository.getChatThreads(finalUserId, new ChatThreadRepository.ThreadsCallback() {
            @Override
            public void onSuccess(List<ChatThreadDto> threads) {
                Log.d(TAG, "✅ Got " + threads.size() + " threads");
                runOnUiThread(() -> {
                    if (threads.isEmpty()) {
                        Toast.makeText(LandlordChatListActivity.this, "Chưa có cuộc trò chuyện nào", Toast.LENGTH_SHORT).show();
                    }

                    adapter = new ChatThreadListAdapter(threads, thread -> {
                        Log.d(TAG, "Opening thread: " + thread.getThreadId());
                        Intent intent = new Intent(LandlordChatListActivity.this, LandlordChatActivity.class);
                        intent.putExtra("thread_id", thread.getThreadId());
                        intent.putExtra("user_id", finalUserId);
                        intent.putExtra("user_name", sessionManager.getUserName());  // ✅ Pass current user name
                        intent.putExtra("other_user_id", thread.getOtherUserId());  // ✅ FIXED: Use generic otherUserId
                        intent.putExtra("other_user_name", thread.getTenantName());
                        startActivity(intent);
                    });

                    recyclerViewChatList.setAdapter(adapter);
                });
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "❌ Error: " + error);
                runOnUiThread(() -> {
                    Toast.makeText(LandlordChatListActivity.this, "Lỗi tải chat: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
