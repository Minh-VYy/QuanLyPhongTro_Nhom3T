package com.example.QuanLyPhongTro_App.ui.landlord;

import android.os.Bundle;

import com.example.QuanLyPhongTro_App.ui.tenant.ChatActivity;

/**
 * LandlordChatActivity - Chat wrapper cho Chủ trọ
 * Extends ChatActivity để dùng cùng logic nhưng clarify intent
 */
public class LandlordChatActivity extends ChatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // All logic is inherited from ChatActivity
        // Conversation handler là chung cho cả tenant + landlord
    }
}

