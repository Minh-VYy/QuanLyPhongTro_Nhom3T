package com.example.QuanLyPhongTro_App.data;

import java.util.UUID;

public class ChatMessage {
    private final String id;
    private final long timestamp;
    private final String senderId; // GUID người gửi (FromUserId)
    private final String senderName;
    private final boolean fromLandlord;
    private final String content;
    private boolean read;

    /**
     * Constructor cũ (optimistic/local) - mặc định timestamp = now.
     */
    public ChatMessage(String senderId, String senderName, boolean fromLandlord, String content) {
        this(UUID.randomUUID().toString(), System.currentTimeMillis(), senderId, senderName, fromLandlord, content, false);
    }

    /**
     * Constructor mới - dùng khi load lịch sử từ API để giữ đúng thời gian backend trả về.
     */
    public ChatMessage(long timestamp, String senderId, String senderName, boolean fromLandlord, String content) {
        this(UUID.randomUUID().toString(), timestamp, senderId, senderName, fromLandlord, content, false);
    }

    private ChatMessage(String id, long timestamp, String senderId, String senderName, boolean fromLandlord, String content, boolean read) {
        this.id = id;
        this.timestamp = timestamp;
        this.senderId = senderId;
        this.senderName = senderName;
        this.fromLandlord = fromLandlord;
        this.content = content;
        this.read = read;
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
