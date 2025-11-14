package com.example.QuanLyPhongTro_App.ui.tenant;

public class Booking {
    private String roomName;
    private String price;
    private String dateTime;
    private String address;
    private String status;

    public Booking(String roomName, String price, String dateTime, String address, String status) {
        this.roomName = roomName;
        this.price = price;
        this.dateTime = dateTime;
        this.address = address;
        this.status = status;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getPrice() {
        return price;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getAddress() {
        return address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

