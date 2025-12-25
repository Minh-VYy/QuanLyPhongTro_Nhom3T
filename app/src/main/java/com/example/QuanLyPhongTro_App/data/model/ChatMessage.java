package com.example.QuanLyPhongTro_App.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

/**
 * Chat Message model - từ backend API
 * C# backend uses: FromUserId, ToUserId, NoiDung, etc. (PascalCase)
 */
public class ChatMessage {
    @SerializedName(value = "TinNhanId", alternate = {"MessageId", "messageId"})
    public String tinNhanId;

    // C# backend uses "FromUserId" not "FromUser"
    @SerializedName(value = "FromUserId", alternate = {"FromUser", "fromUser", "fromUserId"})
    public String fromUser;

    @SerializedName(value = "ToUserId", alternate = {"ToUser", "toUser", "toUserId"})
    public String toUser;

    @SerializedName(value = "NoiDung", alternate = {"Content", "content", "noiDung"})
    public String noiDung;

    @SerializedName(value = "TapTinId", alternate = {"tapTinId"})
    public String tapTinId;

    @SerializedName(value = "ThoiGian", alternate = {"Timestamp", "timestamp", "thoiGian"})
    public String thoiGian; // ISO 8601 format

    @SerializedName(value = "DaDoc", alternate = {"IsRead", "isRead", "daDoc"})
    public boolean daDoc;

    @SerializedName(value = "MessageType", alternate = {"messageType"})
    public String messageType;

    public ChatMessage() {}

    public ChatMessage(String toUser, String noiDung) {
        this.toUser = toUser;
        this.noiDung = noiDung;
        this.messageType = "text";
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "fromUser='" + fromUser + '\'' +
                ", toUser='" + toUser + '\'' +
                ", noiDung='" + noiDung + '\'' +
                ", thoiGian='" + thoiGian + '\'' +
                '}';
    }
}

