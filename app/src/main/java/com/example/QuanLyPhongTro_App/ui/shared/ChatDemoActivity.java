package com.example.QuanLyPhongTro_App.ui.shared;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.ui.landlord.LandlordChatListActivity;
import com.example.QuanLyPhongTro_App.ui.tenant.ChatListActivity;

public class ChatDemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_demo);

        setupButtons();
    }

    private void setupButtons() {
        // Button để kiểm thử Tenant 201 -> tenant1@gmail.com
        Button btnTenant201 = findViewById(R.id.btn_tenant_201);
        btnTenant201.setOnClickListener(v -> {
            Intent intent = new Intent(ChatDemoActivity.this, ChatListActivity.class);
            intent.putExtra("user_id", "tenant1@gmail.com");
            intent.putExtra("is_tenant", true);
            startActivity(intent);
        });

        // Button để kiểm thử Tenant 202 -> tenant2@gmail.com
        Button btnTenant202 = findViewById(R.id.btn_tenant_202);
        btnTenant202.setOnClickListener(v -> {
            Intent intent = new Intent(ChatDemoActivity.this, ChatListActivity.class);
            intent.putExtra("user_id", "tenant2@gmail.com");
            intent.putExtra("is_tenant", true);
            startActivity(intent);
        });

        // Button để kiểm thử Landlord 101 -> landlord1@gmail.com
        Button btnLandlord101 = findViewById(R.id.btn_landlord_101);
        btnLandlord101.setOnClickListener(v -> {
            Intent intent = new Intent(ChatDemoActivity.this, LandlordChatListActivity.class);
            intent.putExtra("user_id", "landlord1@gmail.com");
            startActivity(intent);
        });

        // Button để kiểm thử Landlord 102 -> landlord2@gmail.com
        Button btnLandlord102 = findViewById(R.id.btn_landlord_102);
        btnLandlord102.setOnClickListener(v -> {
            Intent intent = new Intent(ChatDemoActivity.this, LandlordChatListActivity.class);
            intent.putExtra("user_id", "landlord2@gmail.com");
            startActivity(intent);
        });
    }
}
