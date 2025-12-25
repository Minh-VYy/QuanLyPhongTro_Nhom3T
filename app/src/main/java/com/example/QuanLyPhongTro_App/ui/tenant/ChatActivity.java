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
    private ImageButton btnSendMessage, btnBackChat;
    private TextView tvChatHeader;

    private ChatAdapter chatAdapter;
    private String currentUserId;
    private String otherUserId;
    private String currentUserName;

    private ChatRepository chatRepository;
    private SessionManager sessionManager;

    // ✅ Auto-refresh polling
    private Handler pollingHandler;
    private Runnable pollingRunnable;
    private static final long POLLING_INTERVAL = 500; // ⚡ 500ms để refresh nhanh hơn
    private int lastMessageCount = 0;
    private static final int MAX_RETRIES = 3;
    private int messageLoadRetries = 0;

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

        // ⚡ Load message history from API immediately (with retry)
        loadMessageHistoryWithRetry();

        // ✅ Start polling
        if (pollingHandler != null && pollingRunnable != null) {
            pollingHandler.postDelayed(pollingRunnable, POLLING_INTERVAL);
        }
    }

    private void initViews() {
        recyclerViewChat = findViewById(R.id.recycler_view_chat);
        etMessageInput = findViewById(R.id.et_message_input);
        btnSendMessage = findViewById(R.id.btn_send_message);
        btnBackChat = findViewById(R.id.btn_back_chat);
        tvChatHeader = findViewById(R.id.tv_chat_header);
    }

    private void loadChatData() {
        // Lấy thông tin từ Intent
        currentUserId = getIntent().getStringExtra("user_id");
        otherUserId = getIntent().getStringExtra("other_user_id");

        // ✅ CRITICAL: Trim ALL IDs immediately on load
        if (currentUserId != null) currentUserId = currentUserId.trim();
        if (otherUserId != null) otherUserId = otherUserId.trim();

        Log.d(TAG, "📥 Raw intent data:");
        Log.d(TAG, "   currentUserId: '" + currentUserId + "' (length: " + (currentUserId != null ? currentUserId.length() : "null") + ")");
        Log.d(TAG, "   otherUserId: '" + otherUserId + "' (length: " + (otherUserId != null ? otherUserId.length() : "null") + ")");

        // Fallback: try to get otherUserId from threadId (passed from ChatListActivity)
        // NOTE: threadId should NOT be used as otherUserId! This is wrong!
        // Only use it if otherUserId is genuinely null
        if (otherUserId == null || otherUserId.isEmpty()) {
            String threadId = getIntent().getStringExtra("thread_id");
            if (threadId != null && !threadId.isEmpty()) {
                Log.d(TAG, "⚠️ otherUserId was null! Got thread_id as fallback: " + threadId);
                // NOTE: In a proper implementation, threadId should be used to extract actual otherUserId from backend
                // For now, assuming thread_id might be otherUserId in some cases
                otherUserId = threadId.trim();
                Log.d(TAG, "⚠️ Using thread_id as otherUserId (NOT IDEAL - should be passed explicitly)");
            }
        }

        // Get user names from Intent
        currentUserName = getIntent().getStringExtra("user_name");
        String otherUserName = getIntent().getStringExtra("other_user_name");

        // ✅ Trim names too
        if (currentUserName != null) currentUserName = currentUserName.trim();
        if (otherUserName != null) otherUserName = otherUserName.trim();

        // ...existing code...
        if (currentUserId == null || currentUserId.isEmpty()) {
            currentUserId = sessionManager.getUserId();
            if (currentUserId != null) currentUserId = currentUserId.trim();
            Log.d(TAG, "Using currentUserId from session: '" + currentUserId + "' (length: " + (currentUserId != null ? currentUserId.length() : "null") + ")");
        }

        if (currentUserId == null || currentUserId.isEmpty()) {
            currentUserId = sessionManager.getUserEmail();
            if (currentUserId != null) currentUserId = currentUserId.trim();
            Log.d(TAG, "Using email as currentUserId: '" + currentUserId + "' (length: " + (currentUserId != null ? currentUserId.length() : "null") + ")");
        }

        // Get current user name from session if not from intent
        if (currentUserName == null || currentUserName.isEmpty()) {
            currentUserName = sessionManager.getUserName();
            Log.d(TAG, "Using currentUserName from session: " + currentUserName);
        }


        // ✅ CRITICAL: Cache BOTH current and other user so names appear correctly
        // without relying on Intent (Intent might not have names for older messages)
        if (currentUserName != null && !currentUserName.isEmpty()) {
            UserCache.addUser(currentUserId, currentUserName);
            Log.d(TAG, "✅ Cached current user: " + currentUserId + " -> " + currentUserName);
        } else {
            // If no name, try from session
            String sessionName = sessionManager.getUserName();
            if (sessionName != null && !sessionName.isEmpty()) {
                UserCache.addUser(currentUserId, sessionName);
                Log.d(TAG, "✅ Cached current user from session: " + currentUserId + " -> " + sessionName);
            }
        }

        if (otherUserName != null && !otherUserName.isEmpty()) {
            UserCache.addUser(otherUserId, otherUserName);
            Log.d(TAG, "✅ Cached other user: " + otherUserId + " -> " + otherUserName);
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
        String headerText = (otherUserName != null && !otherUserName.isEmpty()) ? otherUserName : otherUserId;
        tvChatHeader.setText(headerText);

        Log.d(TAG, "✅ Chat initialized - Current User: " + currentUserId + " (" + currentUserName + "), Other User: " + otherUserId + " (" + otherUserName + ")");
    }

    private void setupRecyclerView() {
        // ✅ Ensure currentUserId is trimmed before passing to adapter
        String trimmedCurrentUserId = currentUserId != null ? currentUserId.trim() : "";
        chatAdapter = new ChatAdapter(new java.util.ArrayList<>(), trimmedCurrentUserId);
        recyclerViewChat.setAdapter(chatAdapter);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupSendButton() {
        btnSendMessage.setOnClickListener(v -> sendMessage());

        // ✅ Back button click listener
        if (btnBackChat != null) {
            btnBackChat.setOnClickListener(v -> {
                Log.d(TAG, "Back button clicked");
                finish();
            });
        }
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


    // Resume polling when activity resumes
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "🟢 Chat resumed - Starting auto-refresh polling");
        if (pollingHandler != null && pollingRunnable != null) {
            pollingHandler.removeCallbacks(pollingRunnable);
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

        // ✅ Validate user IDs EVERY TIME before sending (in case they change)
        String finalCurrentUserId = currentUserId;
        String finalOtherUserId = otherUserId;

        // ✅ Trim all IDs to ensure no whitespace issues
        if (finalCurrentUserId != null) finalCurrentUserId = finalCurrentUserId.trim();
        if (finalOtherUserId != null) finalOtherUserId = finalOtherUserId.trim();

        // Refresh IDs from session to ensure they're not stale
        if (finalCurrentUserId == null || finalCurrentUserId.isEmpty()) {
            finalCurrentUserId = sessionManager.getUserId();
            if (finalCurrentUserId != null) finalCurrentUserId = finalCurrentUserId.trim();
            Log.d(TAG, "⚠️ currentUserId was null, refreshed from session: " + finalCurrentUserId);
        }
        if (finalCurrentUserId == null || finalCurrentUserId.isEmpty()) {
            finalCurrentUserId = sessionManager.getUserEmail();
            if (finalCurrentUserId != null) finalCurrentUserId = finalCurrentUserId.trim();
            Log.d(TAG, "⚠️ currentUserId still null, using email: " + finalCurrentUserId);
        }

        // Validate final user IDs
        if (finalCurrentUserId == null || finalCurrentUserId.isEmpty()) {
            Log.e(TAG, "❌ currentUserId is still null or empty after refresh!");
            Toast.makeText(this, "Lỗi: Không xác định được người gửi", Toast.LENGTH_SHORT).show();
            return;
        }

        if (finalOtherUserId == null || finalOtherUserId.isEmpty()) {
            Log.e(TAG, "❌ otherUserId is null or empty!");
            Toast.makeText(this, "Lỗi: Không xác định được người nhận", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "=== SENDING MESSAGE ===");
        Log.d(TAG, "From: " + finalCurrentUserId);
        Log.d(TAG, "To: " + finalOtherUserId);
        Log.d(TAG, "Content: " + messageContent);

        btnSendMessage.setEnabled(false);

        // ✅ OPTIMISTIC UPDATE: Thêm tin nhắn vào adapter ngay lập tức
        String displayName = (currentUserName != null && !currentUserName.isEmpty()) ? currentUserName : finalCurrentUserId;
        ChatMessage optimisticMessage = new ChatMessage(
            finalCurrentUserId,
            displayName,
            false, // false = sent message
            messageContent
        );
        chatAdapter.addMessage(optimisticMessage);
        recyclerViewChat.scrollToPosition(chatAdapter.getItemCount() - 1);
        Log.d(TAG, "✅ Added optimistic message to UI");

        // Clear input immediately
        etMessageInput.setText("");

        // Gửi qua API
        Log.d(TAG, "📤 Calling API to send message...");
        chatRepository.sendMessage(finalCurrentUserId, finalOtherUserId, messageContent, new ChatRepository.ChatCallback() {
            @Override
            public void onSuccess(String message) {
                Log.d(TAG, "✅✅✅ MESSAGE SENT SUCCESSFULLY ✅✅✅");
                runOnUiThread(() -> {
                    btnSendMessage.setEnabled(true);
                    Toast.makeText(ChatActivity.this, "✅ Tin nhắn đã gửi", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Reloading message history after successful send...");
                    loadMessageHistory();
                });
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "❌❌❌ SEND FAILED ❌❌❌");
                Log.e(TAG, "Error: " + error);
                runOnUiThread(() -> {
                    btnSendMessage.setEnabled(true);
                    Toast.makeText(ChatActivity.this, "❌ Lỗi: " + error, Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Not reloading - keeping optimistic message");
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

        Log.d(TAG, "=== LOADING MESSAGE HISTORY ===");
        Log.d(TAG, "currentUserId: " + currentUserId);
        Log.d(TAG, "otherUserId: " + otherUserId);

        chatRepository.getMessageHistory(currentUserId, otherUserId, new ChatRepository.HistoryCallback() {
            @Override
            public void onSuccess(List<com.example.QuanLyPhongTro_App.data.model.ChatMessage> messages) {
                Log.d(TAG, "✅ onSuccess called with " + (messages == null ? "null" : messages.size()) + " messages");

                if (messages == null) {
                    Log.e(TAG, "❌ messages list is null!");
                    Toast.makeText(ChatActivity.this, "Lỗi: Dữ liệu tin nhắn null", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (messages.isEmpty()) {
                    Log.d(TAG, "⚠️ No messages found");
                    Toast.makeText(ChatActivity.this, "Chưa có tin nhắn. Hãy gửi tin nhắn đầu tiên!", Toast.LENGTH_SHORT).show();
                }

                Log.d(TAG, "Starting conversion of " + messages.size() + " messages...");
                runOnUiThread(() -> {
                    // Convert API ChatMessage to local ChatMessage format
                    java.util.List<ChatMessage> chatMessages = new java.util.ArrayList<>();
                    for (com.example.QuanLyPhongTro_App.data.model.ChatMessage msg : messages) {
                        try {
                            // ✅ CRITICAL: Trim both IDs before comparison
                            String msgFromUser = msg.fromUser != null ? msg.fromUser.trim() : "";
                            String currUserId = currentUserId != null ? currentUserId.trim() : "";

                            // Get user name from cache or use ID as fallback
                            String senderName = UserCache.getUserName(msgFromUser);
                            String displayName = (senderName != null && !senderName.isEmpty()) ? senderName : msgFromUser;

                            // ✅ FIX: isFromLandlord = tin nhắn được gửi từ người KHÁC (không phải currentUserId)
                            // Nếu fromUser != currentUserId, thì nó là từ landlord/người khác
                            boolean isFromLandlord = !msgFromUser.equals(currUserId);

                            Log.d(TAG, "✏️ Converting: from=" + msgFromUser + " current=" + currUserId + " isFromLandlord=" + isFromLandlord + " content=" + msg.noiDung);
                            Log.d(TAG, "   Sender display name: " + displayName + " (cached=" + senderName + ")");

                            ChatMessage chatMsg = new ChatMessage(
                                msgFromUser,
                                displayName,
                                isFromLandlord,
                                msg.noiDung
                            );
                            chatMessages.add(chatMsg);
                        } catch (Exception e) {
                            Log.e(TAG, "❌ Error converting message: " + e.getMessage(), e);
                        }
                    }

                    Log.d(TAG, "✅ Converted " + chatMessages.size() + " messages successfully");

                    // Update adapter
                    chatAdapter.updateMessages(chatMessages);
                    lastMessageCount = messages.size();

                    Log.d(TAG, "✅ Adapter updated, itemCount = " + chatAdapter.getItemCount());

                    if (chatAdapter.getItemCount() > 0) {
                        Log.d(TAG, "🔽 Scrolling to position " + (chatAdapter.getItemCount() - 1));
                        recyclerViewChat.scrollToPosition(chatAdapter.getItemCount() - 1);
                    }
                });
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "❌ onError called: " + error);
                runOnUiThread(() -> {
                    Toast.makeText(ChatActivity.this, "Lỗi tải lịch sử: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    // ✅ Load message history with retry mechanism
    private void loadMessageHistoryWithRetry() {
        loadMessageHistoryWithRetry(0);
    }

    private void loadMessageHistoryWithRetry(int retryCount) {
        if (otherUserId == null || otherUserId.isEmpty()) {
            Log.e(TAG, "❌ Cannot load history: otherUserId is null");
            Toast.makeText(this, "Lỗi: Không xác định được người nhận", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "=== LOADING MESSAGE HISTORY (Attempt " + (retryCount + 1) + "/" + (MAX_RETRIES + 1) + ") ===");

        chatRepository.getMessageHistory(currentUserId, otherUserId, new ChatRepository.HistoryCallback() {
            @Override
            public void onSuccess(List<com.example.QuanLyPhongTro_App.data.model.ChatMessage> messages) {
                Log.d(TAG, "✅ SUCCESS: Got " + (messages == null ? "null" : messages.size()) + " messages on attempt " + (retryCount + 1));
                messageLoadRetries = 0; // Reset retry counter on success

                if (messages == null || messages.isEmpty()) {
                    Log.d(TAG, "⚠️ No messages received");
                    runOnUiThread(() -> {
                        chatAdapter.updateMessages(new java.util.ArrayList<>());
                    });
                    return;
                }

                runOnUiThread(() -> {
                    java.util.List<ChatMessage> chatMessages = new java.util.ArrayList<>();
                    for (com.example.QuanLyPhongTro_App.data.model.ChatMessage msg : messages) {
                        // ✅ CRITICAL: Trim both IDs before comparison
                        String msgFromUser = msg.fromUser != null ? msg.fromUser.trim() : "";
                        String currUserId = currentUserId != null ? currentUserId.trim() : "";

                        String senderName = UserCache.getUserName(msgFromUser);
                        String displayName = (senderName != null && !senderName.isEmpty()) ? senderName : msgFromUser;
                        boolean isFromLandlord = !msgFromUser.equals(currUserId);

                        ChatMessage chatMsg = new ChatMessage(
                            msgFromUser,
                            displayName,
                            isFromLandlord,
                            msg.noiDung
                        );
                        chatMessages.add(chatMsg);
                    }

                    chatAdapter.updateMessages(chatMessages);
                    lastMessageCount = messages.size();

                    if (chatAdapter.getItemCount() > 0) {
                        recyclerViewChat.scrollToPosition(chatAdapter.getItemCount() - 1);
                    }
                });
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "❌ LOAD FAILED on attempt " + (retryCount + 1) + ": " + error);

                if (retryCount < MAX_RETRIES) {
                    Log.d(TAG, "🔄 Retrying... (" + (retryCount + 1) + "/" + MAX_RETRIES + ")");
                    // Retry after 500ms
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        loadMessageHistoryWithRetry(retryCount + 1);
                    }, 500);
                } else {
                    Log.e(TAG, "❌❌❌ MAX RETRIES EXCEEDED ❌❌❌");
                    runOnUiThread(() -> {
                        Toast.makeText(ChatActivity.this, "Lỗi tải tin nhắn: " + error, Toast.LENGTH_LONG).show();
                    });
                }
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
                        // ✅ CRITICAL: Trim both IDs before comparison
                        String msgFromUser = msg.fromUser != null ? msg.fromUser.trim() : "";
                        String currUserId = currentUserId != null ? currentUserId.trim() : "";

                        // Get user name from cache or use ID as fallback
                        String senderName = UserCache.getUserName(msgFromUser);
                        String displayName = (senderName != null && !senderName.isEmpty()) ? senderName : msgFromUser;

                        // ✅ FIX: isFromLandlord = tin nhắn được gửi từ người KHÁC
                        boolean isFromLandlord = !msgFromUser.equals(currUserId);

                        Log.d(TAG, "🔄 Auto-load converting: from=" + msgFromUser + " current=" + currUserId + " isFromLandlord=" + isFromLandlord);
                        Log.d(TAG, "   Display name: " + displayName + " (content: " + msg.noiDung + ")");

                        ChatMessage chatMsg = new ChatMessage(
                            msgFromUser,
                            displayName,
                            isFromLandlord,
                            msg.noiDung
                        );
                        chatMessages.add(chatMsg);
                    }

                    chatAdapter.updateMessages(chatMessages);
                    lastMessageCount = messages.size();

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
