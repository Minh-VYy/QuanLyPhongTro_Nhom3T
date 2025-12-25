package com.example.QuanLyPhongTro_App.ui.tenant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.data.ChatMessage;
import com.example.QuanLyPhongTro_App.data.repository.ChatRepository;
import com.example.QuanLyPhongTro_App.utils.SessionManager;
import com.example.QuanLyPhongTro_App.utils.UserCache;

import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";

    private RecyclerView recyclerViewChat;
    private EditText etMessageInput;
    private ImageButton btnSendMessage;
    private TextView tvChatHeader;

    private ChatAdapter chatAdapter;
    private String currentUserId;
    private String otherUserId;
    private String currentUserName;
    private String otherUserName;

    private ChatRepository chatRepository;
    private SessionManager sessionManager;

    // ✅ Auto-refresh polling
    private Handler pollingHandler;
    private Runnable pollingRunnable;
    private static final long POLLING_INTERVAL = 2000; // 2 seconds
    private int lastMessageCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        sessionManager = new SessionManager(this);
        chatRepository = new ChatRepository();

        initViews();
        loadChatData();
        setupRecyclerView();
        setupSendButton();

        // ✅ Setup auto-refresh polling
        setupAutoRefresh();

        // Load message history from API
        loadMessageHistory();
    }

    private void initViews() {
        recyclerViewChat = findViewById(R.id.recycler_view_chat);
        etMessageInput = findViewById(R.id.et_message_input);
        btnSendMessage = findViewById(R.id.btn_send_message);
        tvChatHeader = findViewById(R.id.tv_chat_header);
    }

    private void loadChatData() {
        // Lấy thông tin từ Intent
        currentUserId = getIntent().getStringExtra("user_id");
        otherUserId = getIntent().getStringExtra("other_user_id");

        // Fallback: try to get otherUserId from threadId (passed from ChatListActivity)
        if (otherUserId == null || otherUserId.isEmpty()) {
            otherUserId = getIntent().getStringExtra("thread_id");
            Log.d(TAG, "Using thread_id as otherUserId: " + otherUserId);
        }

        // Get user names from Intent
        currentUserName = getIntent().getStringExtra("user_name");
        otherUserName = getIntent().getStringExtra("other_user_name");

        // Fallback to session if not provided
        if (currentUserId == null || currentUserId.isEmpty()) {
            currentUserId = sessionManager.getUserId();
            Log.d(TAG, "Using currentUserId from session: " + currentUserId);
        }

        if (currentUserId == null || currentUserId.isEmpty()) {
            currentUserId = sessionManager.getUserEmail();
            Log.d(TAG, "Using email as currentUserId: " + currentUserId);
        }

        // Get current user name from session if not from intent
        if (currentUserName == null || currentUserName.isEmpty()) {
            currentUserName = sessionManager.getUserName();
            Log.d(TAG, "Using currentUserName from session: " + currentUserName);
        }


        // Cache both users
        if (currentUserName != null && !currentUserName.isEmpty()) {
            UserCache.addUser(currentUserId, currentUserName);
        }
        if (otherUserName != null && !otherUserName.isEmpty()) {
            UserCache.addUser(otherUserId, otherUserName);
        }

        // Validate user IDs
        if (currentUserId == null || currentUserId.isEmpty()) {
            Log.e(TAG, "❌ currentUserId is null or empty!");
            Toast.makeText(this, "Lỗi: Không xác định được người dùng. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (otherUserId == null || otherUserId.isEmpty()) {
            Log.e(TAG, "❌ otherUserId is null or empty!");
            Toast.makeText(this, "Lỗi: Không xác định được người nhận. Vui lòng thử lại.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Set header text - Use name if available, fallback to userId
        String headerText = (otherUserName != null && !otherUserName.isEmpty()) ? otherUserName : (otherUserId != null ? otherUserId : "Chat");
        tvChatHeader.setText(headerText);

        Log.d(TAG, "✅ Chat initialized - Current User: " + currentUserId + " (" + currentUserName + "), Other User: " + otherUserId + " (" + otherUserName + ")");
    }

    private void setupRecyclerView() {
        chatAdapter = new ChatAdapter(new java.util.ArrayList<>(), currentUserId);
        recyclerViewChat.setAdapter(chatAdapter);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupSendButton() {
        btnSendMessage.setOnClickListener(v -> sendMessage());
    }

    // ✅ Setup auto-refresh polling for real-time message receiving
    private void setupAutoRefresh() {
        pollingHandler = new Handler(Looper.getMainLooper());
        pollingRunnable = new Runnable() {
            @Override
            public void run() {
                // Auto-load message history every 2 seconds
                Log.d(TAG, "🔄 Auto-refresh: Checking for new messages...");
                autoLoadMessageHistory();

                // Schedule next refresh
                pollingHandler.postDelayed(this, POLLING_INTERVAL);
            }
        };
    }

    // Start polling when activity is resumed
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "🟢 Chat resumed - Starting auto-refresh polling");
        if (pollingHandler != null && pollingRunnable != null) {
            pollingHandler.postDelayed(pollingRunnable, POLLING_INTERVAL);
        }
    }

    // Stop polling when activity is paused
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "🔴 Chat paused - Stopping auto-refresh polling");
        if (pollingHandler != null && pollingRunnable != null) {
            pollingHandler.removeCallbacks(pollingRunnable);
        }
    }

    // Stop polling when activity is destroyed
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pollingHandler != null && pollingRunnable != null) {
            pollingHandler.removeCallbacks(pollingRunnable);
        }
    }

    private void sendMessage() {
        String messageContent = etMessageInput.getText().toString().trim();
        if (messageContent.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tin nhắn", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate user IDs before sending
        if (currentUserId == null || currentUserId.isEmpty()) {
            Toast.makeText(this, "Lỗi: Không xác định được người gửi", Toast.LENGTH_SHORT).show();
            return;
        }

        if (otherUserId == null || otherUserId.isEmpty()) {
            Toast.makeText(this, "Lỗi: Không xác định được người nhận", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Sending message from: " + currentUserId + " to: " + otherUserId + ", content: " + messageContent);

        btnSendMessage.setEnabled(false);

        // ✅ OPTIMISTIC UPDATE: Thêm tin nhắn vào adapter ngay lập tức
        // Use cached name or fallback to ID
        String displayName = (currentUserName != null && !currentUserName.isEmpty()) ? currentUserName : currentUserId;
        ChatMessage optimisticMessage = new ChatMessage(
            currentUserId,
            displayName,  // Use cached name
            false, // false = sent message
            messageContent
        );
        chatAdapter.addMessage(optimisticMessage);
        recyclerViewChat.scrollToPosition(chatAdapter.getItemCount() - 1);

        // Clear input immediately
        etMessageInput.setText("");

        // Gửi qua API - cần truyền BOTH fromUserId và toUserId
        chatRepository.sendMessage(currentUserId, otherUserId, messageContent, new ChatRepository.ChatCallback() {
            @Override
            public void onSuccess(String message) {
                Log.d(TAG, "✅ Message sent successfully");
                runOnUiThread(() -> {
                    btnSendMessage.setEnabled(true);
                    Toast.makeText(ChatActivity.this, "Tin nhắn đã gửi", Toast.LENGTH_SHORT).show();

                    // Reload message history to sync with server & remove optimistic message
                    loadMessageHistory();
                });
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "❌ Failed to send: " + error);
                runOnUiThread(() -> {
                    btnSendMessage.setEnabled(true);
                    Toast.makeText(ChatActivity.this, "Lỗi gửi tin nhắn: " + error, Toast.LENGTH_LONG).show();

                    // Remove the optimistic message if send failed
                    loadMessageHistory();
                });
            }
        });
    }

    private void loadMessageHistory() {
        if (otherUserId == null || otherUserId.isEmpty()) {
            Log.e(TAG, "❌ Cannot load history: otherUserId is null");
            Toast.makeText(this, "Lỗi: Không xác định được người nhận", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Loading message history with: " + otherUserId);

        chatRepository.getMessageHistory(currentUserId, otherUserId, new ChatRepository.HistoryCallback() {
            @Override
            public void onSuccess(List<com.example.QuanLyPhongTro_App.data.model.ChatMessage> messages) {
                Log.d(TAG, "✅ Got " + messages.size() + " messages");
                runOnUiThread(() -> {
                    if (messages.isEmpty()) {
                        Toast.makeText(ChatActivity.this, "Chưa có tin nhắn. Hãy gửi tin nhắn đầu tiên!", Toast.LENGTH_SHORT).show();
                    }

                    // Convert API ChatMessage to local ChatMessage format
                    java.util.List<ChatMessage> chatMessages = new java.util.ArrayList<>();
                    for (com.example.QuanLyPhongTro_App.data.model.ChatMessage msg : messages) {
                        // Get user name from cache or use ID as fallback
                        String senderName = UserCache.getUserName(msg.fromUser);
                        String displayName = (senderName != null && !senderName.isEmpty()) ? senderName : msg.fromUser;

                        ChatMessage chatMsg = new ChatMessage(
                            msg.fromUser,
                            displayName,  // Use cached name or user ID
                            !msg.fromUser.equals(currentUserId),
                            msg.noiDung
                        );
                        chatMessages.add(chatMsg);
                    }

                    // Update adapter
                    chatAdapter.updateMessages(chatMessages);
                    lastMessageCount = messages.size();
                    if (chatAdapter.getItemCount() > 0) {
                        recyclerViewChat.scrollToPosition(chatAdapter.getItemCount() - 1);
                    }
                });
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "❌ Failed to load history: " + error);
                runOnUiThread(() -> {
                    Toast.makeText(ChatActivity.this, "Lỗi tải lịch sử: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    // ✅ Auto-load message history (silent mode - for polling)
    private void autoLoadMessageHistory() {
        if (otherUserId == null || otherUserId.isEmpty()) {
            Log.e(TAG, "❌ Cannot auto-load history: otherUserId is null");
            return;
        }

        chatRepository.getMessageHistory(currentUserId, otherUserId, new ChatRepository.HistoryCallback() {
            @Override
            public void onSuccess(List<com.example.QuanLyPhongTro_App.data.model.ChatMessage> messages) {
                // Only update if there are new messages
                if (messages.size() > lastMessageCount) {
                    Log.d(TAG, "✅ New messages detected! " + lastMessageCount + " -> " + messages.size());
                }

                runOnUiThread(() -> {
                    // Convert API ChatMessage to local ChatMessage format
                    java.util.List<ChatMessage> chatMessages = new java.util.ArrayList<>();
                    for (com.example.QuanLyPhongTro_App.data.model.ChatMessage msg : messages) {
                        // Get user name from cache or use ID as fallback
                        String senderName = UserCache.getUserName(msg.fromUser);
                        String displayName = (senderName != null && !senderName.isEmpty()) ? senderName : msg.fromUser;

                        ChatMessage chatMsg = new ChatMessage(
                            msg.fromUser,
                            displayName,  // Use cached name or user ID
                            !msg.fromUser.equals(currentUserId),
                            msg.noiDung
                        );
                        chatMessages.add(chatMsg);
                    }

                    // Update adapter
                    chatAdapter.updateMessages(chatMessages);
                    lastMessageCount = messages.size();

                    // Auto-scroll to bottom if there are new messages
                    if (chatAdapter.getItemCount() > 0) {
                        recyclerViewChat.scrollToPosition(chatAdapter.getItemCount() - 1);
                    }
                });
            }

            @Override
            public void onError(String error) {
                // Silent error - don't show toast for auto-refresh
                Log.e(TAG, "⚠️ Auto-load failed: " + error);
            }
        });
    }
}
