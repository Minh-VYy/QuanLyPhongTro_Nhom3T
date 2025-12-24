package com.example.QuanLyPhongTro_App.data;

import java.util.UUID;

public class ChatMessage {
    private final String id;
    private final long timestamp;
    private final String senderId; // now using email/string id
    private final String senderName;
    private final boolean fromLandlord;
    private final String content;
    private boolean read;

    public ChatMessage(String senderId, String senderName, boolean fromLandlord, String content) {
        this.id = UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
        this.senderId = senderId;
        this.senderName = senderName;
        this.fromLandlord = fromLandlord;
        this.content = content;
        this.read = false;
    }

    public String getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public boolean isFromLandlord() {
        return fromLandlord;
    }

    public String getContent() {
        return content;
    }

    public boolean isRead() {
        return read;
    }

    public void markRead() {
        this.read = true;
    }
}
