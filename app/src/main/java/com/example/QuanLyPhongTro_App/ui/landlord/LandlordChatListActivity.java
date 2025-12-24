package com.example.QuanLyPhongTro_App.ui.landlord;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.data.ChatThread;
import com.example.QuanLyPhongTro_App.data.MockData;
import com.example.QuanLyPhongTro_App.ui.tenant.ChatActivity;
import com.example.QuanLyPhongTro_App.ui.tenant.ChatListAdapter;
import com.example.QuanLyPhongTro_App.utils.SessionManager;

import java.util.List;

public class LandlordChatListActivity extends AppCompatActivity {

    private RecyclerView recyclerViewChatList;
    private ChatListAdapter chatListAdapter;
    private String currentUserEmail;
    private SessionManager sessionManager;

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

        // Lấy user email từ Intent nếu truyền, nếu không lấy từ session
        if (getIntent().hasExtra("user_id")) {
            // Expecting string email
            currentUserEmail = getIntent().getStringExtra("user_id");
        }
        if (currentUserEmail == null) {
            currentUserEmail = sessionManager.getUserEmail();
        }
    }

    private void loadChatList() {
        List<ChatThread> chatThreads = MockData.getChatThreadsForLandlord(currentUserEmail);

        if (chatThreads.isEmpty()) {
            Toast.makeText(this, "Chưa có cuộc trò chuyện nào", Toast.LENGTH_SHORT).show();
        }

        chatListAdapter = new ChatListAdapter(chatThreads, currentUserEmail, thread -> {
            Intent intent = new Intent(LandlordChatListActivity.this, ChatActivity.class);
            intent.putExtra("thread_id", thread.getThreadId());
            intent.putExtra("user_id", currentUserEmail);
            startActivity(intent);
        });

        recyclerViewChatList.setAdapter(chatListAdapter);
    }
}
