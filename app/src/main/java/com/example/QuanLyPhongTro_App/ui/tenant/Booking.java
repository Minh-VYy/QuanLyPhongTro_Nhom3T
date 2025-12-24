package com.example.QuanLyPhongTro_App.ui.tenant;

public class Booking {
    // Fields
    private String id;
    private String title;
    private String price;
    private String date;
    private String timeSlot;
    private String status;
    private String customerName;
    private String location;

    // Constructor
    public Booking(String id, String title, String price, String date,
                   String timeSlot, String status, String customerName, String location) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.date = date;
        this.timeSlot = timeSlot;
        this.status = status;
        this.customerName = customerName;
        this.location = location;
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getPrice() { return price; }
    public String getDate() { return date; }
    public String getTimeSlot() { return timeSlot; }
    public String getStatus() { return status; }
    public String getCustomerName() { return customerName; }
    public String getLocation() { return location; }

    // Setters
    public void setStatus(String status) {
        this.status = status;
    }
}

