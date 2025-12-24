package com.example.QuanLyPhongTro_App.ui.chatbot;

public class ChatMessage {
    private String message;
    private boolean isUser; // true = user, false = bot
    private long timestamp;

    public ChatMessage(String message, boolean isUser) {
        this.message = message;
        this.isUser = isUser;
        this.timestamp = System.currentTimeMillis();
    }

    public String getMessage() {
        return message;
    }

    public boolean isUser() {
        return isUser;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
