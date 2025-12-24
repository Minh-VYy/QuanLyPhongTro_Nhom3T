package com.example.QuanLyPhongTro_App.ui.tenant;

public class Notification {
    private String title;
    private String message;
    private String time;
    private String iconType;
    private boolean isUnread;

    public Notification(String title, String message, String time, String iconType, boolean isUnread) {
        this.title = title;
        this.message = message;
        this.time = time;
        this.iconType = iconType;
        this.isUnread = isUnread;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }

    public String getIconType() {
        return iconType;
    }

    public boolean isUnread() {
        return isUnread;
    }

    public void setUnread(boolean unread) {
        isUnread = unread;
    }
}

