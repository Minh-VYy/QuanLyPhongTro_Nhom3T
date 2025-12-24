package com.example.QuanLyPhongTro_App.ui.chatbot;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.QuanLyPhongTro_App.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;
import java.util.List;

public class ChatbotActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private List<ChatMessage> messages;
    private EditText etMessage;
    private ImageView btnSend, btnBack;
    private SimpleChatbot chatbot;
    private ChipGroup chipGroupQuestions;
    
    private String userType;
    private String context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        getIntentData();
        initViews();
        setupRecyclerView();
        setupButtons();
        initChatbot();
        setupQuickQuestions();
        showWelcomeMessage();
    }

    private void getIntentData() {
        userType = getIntent().getStringExtra("user_type");
        context = getIntent().getStringExtra("context");
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewChat);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        btnBack = findViewById(R.id.btnBack);
        chipGroupQuestions = findViewById(R.id.chipGroupQuestions);
    }

    private void setupRecyclerView() {
        messages = new ArrayList<>();
        adapter = new ChatAdapter(messages);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void setupButtons() {
        btnBack.setOnClickListener(v -> finish());
        
        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void initChatbot() {
        chatbot = new SimpleChatbot(userType != null ? userType : "tenant");
    }

    /**
     * Thiết lập các câu hỏi gợi ý nhanh
     */
    private void setupQuickQuestions() {
        String[] questions;
        
        if ("tenant".equals(userType)) {
            questions = new String[]{
                "Tìm phòng",
                "Giá phòng",
                "Đặt lịch xem",
                "Tiện ích",
                "Thanh toán"
            };
        } else {
            questions = new String[]{
                "Đăng tin",
                "Quản lý tin",
                "Xử lý yêu cầu",
                "Thống kê",
                "Viết mô tả"
            };
        }
        
        for (String question : questions) {
            Chip chip = new Chip(this);
            chip.setText(question);
            chip.setChipBackgroundColorResource(R.color.chip_background);
            chip.setTextColor(getResources().getColor(R.color.primary, null));
            chip.setChipStrokeColorResource(R.color.primary);
            chip.setChipStrokeWidth(2f);
            chip.setOnClickListener(v -> {
                etMessage.setText(question);
                sendMessage();
            });
            chipGroupQuestions.addView(chip);
        }
    }

    private void showWelcomeMessage() {
        String welcomeMsg = getWelcomeMessage();
        addBotMessage(welcomeMsg);
    }

    private String getWelcomeMessage() {
        if ("tenant".equals(userType)) {
            return "Xin chào! 👋\n\nTôi là trợ lý AI giúp bạn tìm phòng trọ phù hợp.\n\nBạn có thể hỏi tôi về:\n• Tìm kiếm phòng\n• Giá cả & tiện ích\n• Cách đặt lịch xem phòng\n• Thanh toán & hợp đồng\n\nHãy thử các câu hỏi gợi ý bên dưới! 😊";
        } else if ("landlord".equals(userType)) {
            return "Xin chào Chủ trọ! 👋\n\nTôi là trợ lý AI hỗ trợ quản lý phòng trọ.\n\nTôi có thể giúp bạn:\n• Đăng tin hiệu quả\n• Quản lý yêu cầu\n• Xem thống kê\n• Tối ưu tin đăng\n\nChọn câu hỏi gợi ý hoặc hỏi tôi bất cứ điều gì! 😊";
        }
        return "Xin chào! Tôi là trợ lý AI. Tôi có thể giúp gì cho bạn?";
    }

    private void sendMessage() {
        String message = etMessage.getText().toString().trim();
        if (message.isEmpty()) return;

        addUserMessage(message);
        etMessage.setText("");

        String response = chatbot.getResponse(message);
        addBotMessage(response);
    }

    private void addUserMessage(String message) {
        messages.add(new ChatMessage(message, true));
        adapter.notifyItemInserted(messages.size() - 1);
        recyclerView.smoothScrollToPosition(messages.size() - 1);
    }

    private void addBotMessage(String message) {
        messages.add(new ChatMessage(message, false));
        adapter.notifyItemInserted(messages.size() - 1);
        recyclerView.smoothScrollToPosition(messages.size() - 1);
    }
}
