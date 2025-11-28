package com.example.QuanLyPhongTro_App.ui.tenant;

import java.io.Serializable;

public class Message implements Serializable {

    private long id;
    private String requesterName;
    private String sentTime;
    private String preview;
    private String fullContent;
    private String roomName;


    public Message(long id, String requesterName, String sentTime, String preview, String fullContent, String roomName) {
        this.id = id;
        this.requesterName = requesterName;
        this.sentTime = sentTime;
        this.preview = preview;
        this.fullContent = fullContent;
        this.roomName = roomName;
    }

    // Getters
    public long getId() {
        return id;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public String getSentTime() {
        return sentTime;
    }

    public String getPreview() {
        return preview;
    }

    public String getFullContent() {
        return fullContent;
    }

    public String getRoomName() {
        return roomName;
    }


}