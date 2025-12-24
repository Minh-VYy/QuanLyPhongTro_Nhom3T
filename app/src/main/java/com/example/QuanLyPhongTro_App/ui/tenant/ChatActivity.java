package com.example.QuanLyPhongTro_App.ui.tenant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.data.ChatMessage;
import com.example.QuanLyPhongTro_App.data.ChatThread;
import com.example.QuanLyPhongTro_App.data.MockData;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerViewChat;
    private EditText etMessageInput;
    private ImageButton btnSendMessage;
    private TextView tvChatHeader;

    private ChatAdapter chatAdapter;
    private ChatThread currentChatThread;
    private String currentUserId; // now using email/string id
    private String threadId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initViews();
        loadChatData();
        setupRecyclerView();
        setupSendButton();
    }

    private void initViews() {
        recyclerViewChat = findViewById(R.id.recycler_view_chat);
        etMessageInput = findViewById(R.id.et_message_input);
        btnSendMessage = findViewById(R.id.btn_send_message);
        tvChatHeader = findViewById(R.id.tv_chat_header);
    }

    private void loadChatData() {
        // Lấy threadId từ Intent
        threadId = getIntent().getStringExtra("thread_id");
        currentUserId = getIntent().getStringExtra("user_id");

        if (threadId == null || currentUserId == null) {
            Toast.makeText(this, "Dữ liệu chat không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Lấy thread từ MockData
        currentChatThread = MockData.getChatThreadById(threadId);
        if (currentChatThread == null) {
            Toast.makeText(this, "Không tìm thấy cuộc trò chuyện", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Cập nhật header với tên đối tượng
        String otherPersonName;
        if (currentUserId.equals(currentChatThread.getTenantId())) {
            otherPersonName = currentChatThread.getLandlordName();
            currentChatThread.markThreadReadForTenant();
        } else {
            otherPersonName = currentChatThread.getTenantName();
            currentChatThread.markThreadReadForLandlord();
        }
        tvChatHeader.setText(otherPersonName);
    }

    private void setupRecyclerView() {
        chatAdapter = new ChatAdapter(currentChatThread.getMessages(), currentUserId);
        recyclerViewChat.setAdapter(chatAdapter);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewChat.scrollToPosition(chatAdapter.getItemCount() - 1);
    }

    private void setupSendButton() {
        btnSendMessage.setOnClickListener(v -> {
            String messageContent = etMessageInput.getText().toString().trim();
            if (messageContent.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tin nhắn", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gửi tin nhắn qua MockData
            boolean success = MockData.sendChatMessage(threadId, currentUserId, messageContent);
            if (success) {
                etMessageInput.setText("");
                chatAdapter.notifyItemInserted(currentChatThread.getMessages().size() - 1);
                recyclerViewChat.scrollToPosition(chatAdapter.getItemCount() - 1);

                // --- AUTO-REPLY SIMULATION: tạo phản hồi giả từ phía đối phương trên cùng một máy ---
                String otherEmail = (currentUserId.equals(currentChatThread.getTenantId())) ? currentChatThread.getLandlordId() : currentChatThread.getTenantId();
                String cannedReply = getCannedReplyFor(messageContent);
                // Delay 1.2s để mô phỏng phản hồi
                recyclerViewChat.postDelayed(() -> {
                    boolean replySuccess = MockData.sendChatMessage(threadId, otherEmail, cannedReply);
                    if (replySuccess) {
                        // Cập nhật adapter và cuộn xuống
                        runOnUiThread(() -> {
                            chatAdapter.notifyItemInserted(currentChatThread.getMessages().size() - 1);
                            recyclerViewChat.scrollToPosition(chatAdapter.getItemCount() - 1);
                        });
                    }
                }, 1200);

            } else {
                Toast.makeText(this, "Không thể gửi tin nhắn", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Trả về câu phản hồi tự động đơn giản dựa trên nội dung gửi lên (để mô phỏng cuộc hội thoại trên cùng một máy)
     */
    private String getCannedReplyFor(String incoming) {
        String low = incoming.toLowerCase();
        if (low.contains("xin chào") || low.contains("chào")) return "Chào bạn, mình còn phòng, bạn muốn xem vào hôm nào?";
        if (low.contains("giá") || low.contains("bao nhiêu")) return "Bạn tham khảo giá trên tin đăng nhé, nếu cần mình sẽ báo cụ thể.";
        if (low.contains("xem") || low.contains("xem phòng")) return "Mình có thể sắp xếp lịch xem vào chiều mai 3-5pm.";
        if (low.contains("cảm ơn") || low.contains("thanks")) return "Không có gì, nếu cần thêm thông tin liên hệ mình nhé.";
        return "Cám ơn bạn, mình sẽ phản hồi sớm.";
    }
}
