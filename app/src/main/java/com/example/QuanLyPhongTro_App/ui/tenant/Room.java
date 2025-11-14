package com.example.QuanLyPhongTro_App.ui.tenant;

import java.io.Serializable;

public class Room implements Serializable {
    private String title;
    private String price;
    private String location;
    private int imageResId;
    private String description;
    private String area;
    private String address;
    private boolean isNew;
    private boolean isPromo;
    private String rating;
    private boolean isSaved;

    public Room(String title, String price, String location, int imageResId) {
        this.title = title;
        this.price = price;
        this.location = location;
        this.imageResId = imageResId;
    }

    // Constructor for saved rooms
    public Room(String title, String price, String address, String rating, boolean isSaved) {
        this.title = title;
        this.price = price;
        this.address = address;
        this.location = address;
        this.rating = rating;
        this.isSaved = isSaved;
        this.imageResId = 0; // Will be set later or use default
    }

    // Getter methods
    public String getTitle() { return title; }
    public String getPrice() { return price; }
    public String getLocation() { return location; }
    public int getImageResId() { return imageResId; }
    public String getDescription() { return description; }
    public String getArea() { return area; }
    public String getAddress() { return address; }
    public boolean isNew() { return isNew; }
    public boolean isPromo() { return isPromo; }
    public String getRating() { return rating; }
    public boolean isSaved() { return isSaved; }

    // Setter methods
    public void setDescription(String description) { this.description = description; }
    public void setArea(String area) { this.area = area; }
    public void setAddress(String address) { this.address = address; }
    public void setNew(boolean isNew) { this.isNew = isNew; }
    public void setPromo(boolean isPromo) { this.isPromo = isPromo; }
    public void setRating(String rating) { this.rating = rating; }
    public void setSaved(boolean saved) { isSaved = saved; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
}