package com.example.QuanLyPhongTro_App.ui.tenant;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.QuanLyPhongTro_App.R;

import java.util.ArrayList;

public class MessageDetailActivity extends AppCompatActivity {

    private RecyclerView messagesRecyclerView;
    private EditText messageInput;
    private Button sendButton;
    private MessageAdapter messageAdapter;
    private ArrayList<Message> messageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get user name from intent
        String userName = getIntent().getStringExtra("USER_NAME");
        if (userName != null) {
            getSupportActionBar().setTitle(userName);
        }

        // Init views
        messagesRecyclerView = findViewById(R.id.messagesRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);

        // Setup RecyclerView
        setupRecyclerView();

        // Load mock messages
        loadMockMessages();

        // Send button click listener
        sendButton.setOnClickListener(v -> sendMessage());
    }

    private void setupRecyclerView() {
        messageAdapter = new MessageAdapter(messageList);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messagesRecyclerView.setAdapter(messageAdapter);
    }

    private void loadMockMessages() {
        // Mock messages
        messageList.add(new Message("Chào bạn, tôi có thể xem phòng vào chiều nay không?", false));
        messageList.add(new Message("Được chứ, bạn có thể qua lúc 3h nhé.", true));
        messageAdapter.notifyDataSetChanged();
        messagesRecyclerView.scrollToPosition(messageList.size() - 1);
    }

    private void sendMessage() {
        String messageText = messageInput.getText().toString().trim();
        if (!messageText.isEmpty()) {
            messageList.add(new Message(messageText, true));
            messageAdapter.notifyDataSetChanged();
            messagesRecyclerView.scrollToPosition(messageList.size() - 1);
            messageInput.setText("");

            // Simulate a reply
            // In a real app, you would send this to your backend and receive a reply
            new android.os.Handler().postDelayed(() -> {
                messageList.add(new Message("Cảm ơn bạn, tôi sẽ trả lời sớm.", false));
                messageAdapter.notifyDataSetChanged();
                messagesRecyclerView.scrollToPosition(messageList.size() - 1);
            }, 1000);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Go back to the previous activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // You'll need a Message model class
    // static class Message {
    //     String text;
    //     boolean isSentByUser;
    //
    //     Message(String text, boolean isSentByUser) {
    //         this.text = text;
    //         this.isSentByUser = isSentByUser;
    //     }
    // }

    // You'll also need a MessageAdapter
}
