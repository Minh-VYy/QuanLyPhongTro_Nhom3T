package com.example.QuanLyPhongTro_App.data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatThread {
    private final String threadId;
    private final String tenantId;
    private final String tenantName;
    private final String landlordId;
    private final String landlordName;
    private final List<ChatMessage> messages = new ArrayList<>();

    public ChatThread(String tenantId, String tenantName, String landlordId, String landlordName) {
        this.threadId = UUID.randomUUID().toString();
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.landlordId = landlordId;
        this.landlordName = landlordName;
    }

    public String getThreadId() {
        return threadId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public String getLandlordId() {
        return landlordId;
    }

    public String getLandlordName() {
        return landlordName;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void addMessage(ChatMessage message) {
        messages.add(message);
    }

    public void markThreadReadForLandlord() {
        for (ChatMessage msg : messages) {
            if (!msg.isFromLandlord()) msg.markRead();
        }
    }

    public void markThreadReadForTenant() {
        for (ChatMessage msg : messages) {
            if (msg.isFromLandlord()) msg.markRead();
        }
    }
}
