package com.example.QuanLyPhongTro_App.data.response;

import com.google.gson.annotations.SerializedName;

public class ChatThreadDto {
    @SerializedName("ThreadId")
    private String threadId;

    @SerializedName("OtherUserId")
    private String otherUserId;  // ✅ NEW: Generic field for the other user in conversation

    @SerializedName("TenantId")
    private String tenantId;

    @SerializedName("TenantName")
    private String tenantName;

    @SerializedName("LandlordId")
    private String landlordId;

    @SerializedName("LandlordName")
    private String landlordName;

    @SerializedName("LastMessage")
    private String lastMessage;

    @SerializedName("LastMessageTime")
    private String lastMessageTime;

    @SerializedName("UnreadCount")
    private int unreadCount;

    public String getThreadId() { return threadId; }
    public String getOtherUserId() { return otherUserId; }  // ✅ NEW Getter
    public String getTenantId() { return tenantId; }
    public String getTenantName() { return tenantName; }
    public String getLandlordId() { return landlordId; }
    public String getLandlordName() { return landlordName; }
    public String getLastMessage() { return lastMessage; }
    public String getLastMessageTime() { return lastMessageTime; }
    public int getUnreadCount() { return unreadCount; }

    // Setters
    public void setThreadId(String threadId) { this.threadId = threadId; }
    public void setOtherUserId(String otherUserId) { this.otherUserId = otherUserId; }  // ✅ NEW Setter
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public void setTenantName(String tenantName) { this.tenantName = tenantName; }
    public void setLandlordId(String landlordId) { this.landlordId = landlordId; }
    public void setLandlordName(String landlordName) { this.landlordName = landlordName; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }
    public void setLastMessageTime(String lastMessageTime) { this.lastMessageTime = lastMessageTime; }
    public void setUnreadCount(int unreadCount) { this.unreadCount = unreadCount; }
}

