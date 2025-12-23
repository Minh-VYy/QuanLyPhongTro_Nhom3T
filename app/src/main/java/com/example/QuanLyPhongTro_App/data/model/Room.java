package com.example.QuanLyPhongTro_App.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "rooms")
public class Room {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "price_value")
    private double priceValue;

    @ColumnInfo(name = "location")
    private String location;

    @ColumnInfo(name = "address")
    private String address;

    @ColumnInfo(name = "area")
    private double area;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "image_url")
    private String imageUrl;

    @ColumnInfo(name = "room_rating")
    private double roomRating;

    @ColumnInfo(name = "room_review_count")
    private int roomReviewCount;

    @ColumnInfo(name = "landlord_id")
    private int landlordId;

    @ColumnInfo(name = "landlord_name")
    private String landlordName;

    @ColumnInfo(name = "landlord_rating")
    private double landlordRating;

    @ColumnInfo(name = "landlord_review_count")
    private int landlordReviewCount;

    @ColumnInfo(name = "landlord_phone")
    private String landlordPhone;

    @ColumnInfo(name = "is_new")
    private boolean isNew;

    @ColumnInfo(name = "is_promo")
    private boolean isPromo;

    @ColumnInfo(name = "created_date")
    private String createdDate;

    public Room(String title, double priceValue, String location, String address,
                double area, String description, String imageUrl,
                double roomRating, int roomReviewCount,
                int landlordId, String landlordName, double landlordRating,
                int landlordReviewCount, String landlordPhone,
                boolean isNew, boolean isPromo, String createdDate) {
        this.title = title;
        this.priceValue = priceValue;
        this.location = location;
        this.address = address;
        this.area = area;
        this.description = description;
        this.imageUrl = imageUrl;
        this.roomRating = roomRating;
        this.roomReviewCount = roomReviewCount;
        this.landlordId = landlordId;
        this.landlordName = landlordName;
        this.landlordRating = landlordRating;
        this.landlordReviewCount = landlordReviewCount;
        this.landlordPhone = landlordPhone;
        this.isNew = isNew;
        this.isPromo = isPromo;
        this.createdDate = createdDate;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public double getPriceValue() { return priceValue; }
    public void setPriceValue(double priceValue) { this.priceValue = priceValue; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public double getArea() { return area; }
    public void setArea(double area) { this.area = area; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public double getRoomRating() { return roomRating; }
    public void setRoomRating(double roomRating) { this.roomRating = roomRating; }
    public int getRoomReviewCount() { return roomReviewCount; }
    public void setRoomReviewCount(int roomReviewCount) { this.roomReviewCount = roomReviewCount; }
    public int getLandlordId() { return landlordId; }
    public void setLandlordId(int landlordId) { this.landlordId = landlordId; }
    public String getLandlordName() { return landlordName; }
    public void setLandlordName(String landlordName) { this.landlordName = landlordName; }
    public double getLandlordRating() { return landlordRating; }
    public void setLandlordRating(double landlordRating) { this.landlordRating = landlordRating; }
    public int getLandlordReviewCount() { return landlordReviewCount; }
    public void setLandlordReviewCount(int landlordReviewCount) { this.landlordReviewCount = landlordReviewCount; }
    public String getLandlordPhone() { return landlordPhone; }
    public void setLandlordPhone(String landlordPhone) { this.landlordPhone = landlordPhone; }
    public boolean isNew() { return isNew; }
    public void setNew(boolean aNew) { isNew = aNew; }
    public boolean isPromo() { return isPromo; }
    public void setPromo(boolean promo) { isPromo = promo; }
    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }
}