package com.example.QuanLyPhongTro_App.ui.tenant;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.ArrayList;

@Entity(tableName = "rooms")
public class Room implements Serializable {
    // ========== THÔNG TIN CƠ BẢN PHÒNG ==========
    @PrimaryKey(autoGenerate = true)
    private int id;                    // ID phòng từ database

    @ColumnInfo(name = "title")
    private String title;              // Tiêu đề phòng

    @ColumnInfo(name = "price_value")
    private double priceValue;         // Giá phòng (số thực) - VD: 2500000

    @ColumnInfo(name = "location")
    private String location;           // Vị trí (quận/huyện)

    @ColumnInfo(name = "address")
    private String address;            // Địa chỉ đầy đủ

    @ColumnInfo(name = "area")
    private double area;               // Diện tích (m²) - VD: 20.5

    @ColumnInfo(name = "description")
    private String description;        // Mô tả chi tiết

    // ========== HÌNH ẢNH ==========
    @Ignore // Trường này chỉ dùng cho UI, không lưu vào DB
    private int imageResId;            // Resource ID cho ảnh local (tạm thời)

    @ColumnInfo(name = "image_url")
    private String imageUrl;           // URL ảnh từ server (khi có backend)

    // ========== ĐÁNH GIÁ PHÒNG ==========
    @ColumnInfo(name = "room_rating")
    private double roomRating;         // Đánh giá riêng của phòng (0-5) - VD: 4.5

    @ColumnInfo(name = "room_review_count")
    private int roomReviewCount;       // Số lượng đánh giá của phòng - VD: 8

    // ========== THÔNG TIN CHỦ TRỌ ==========
    @ColumnInfo(name = "landlord_id")
    private int landlordId;            // ID chủ trọ từ database

    @ColumnInfo(name = "landlord_name")
    private String landlordName;       // Tên chủ trọ

    @ColumnInfo(name = "landlord_rating")
    private double landlordRating;     // Tổng đánh giá của chủ trọ (0-5) - VD: 4.8

    @ColumnInfo(name = "landlord_review_count")
    private int landlordReviewCount;   // Số lượng đánh giá của chủ trọ - VD: 25

    @ColumnInfo(name = "landlord_phone")
    private String landlordPhone;      // Số điện thoại chủ trọ

    // ========== TRẠNG THÁI ==========
    @ColumnInfo(name = "is_new")
    private boolean isNew;             // Phòng mới đăng (< 7 ngày)

    @ColumnInfo(name = "is_promo")
    private boolean isPromo;           // Có khuyến mãi

    @ColumnInfo(name = "is_saved")
    private boolean isSaved;           // Đã lưu vào danh sách yêu thích

    @ColumnInfo(name = "created_date")
    private String createdDate;        // Ngày đăng tin - VD: "2024-01-15"
    
    // ========== THÔNG TIN BỔ SUNG CHO LỌC ==========
    private String roomType;           // Loại phòng: "Nguyên căn", "Phòng riêng", "Ở ghép"
    private ArrayList<String> amenities; // Danh sách tiện nghi

    // ========== CONSTRUCTORS ==========

    // Constructor trống mà Room yêu cầu
    public Room() {}

    @Ignore
    public Room(int id, String title, double priceValue, String location, String address,
                double area, String description, String imageUrl,
                double roomRating, int roomReviewCount,
                int landlordId, String landlordName, double landlordRating,
                int landlordReviewCount, String landlordPhone,
                boolean isNew, boolean isPromo, String createdDate) {
        this.id = id;
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
        this.isSaved = false; // Mặc định chưa lưu
    }

    @Ignore
    public Room(String title, String priceText, String location, int imageResId) {
        this.title = title;
        this.priceValue = parsePriceFromText(priceText);
        this.location = location;
        this.imageResId = imageResId;
        this.roomRating = 0;
        this.roomReviewCount = 0;
        this.landlordRating = 0;
        this.landlordReviewCount = 0;
    }

    @Ignore
    public Room(String title, String priceText, String address, String rating, boolean isSaved) {
        this.title = title;
        this.priceValue = parsePriceFromText(priceText);
        this.address = address;
        this.location = address;
        this.isSaved = isSaved;
        this.imageResId = 0;
        if (rating != null && !rating.isEmpty()) {
            try {
                this.roomRating = Double.parseDouble(rating);
            } catch (NumberFormatException e) {
                this.roomRating = 0;
            }
        }
    }

    // ========== HELPER METHODS ==========

    @Ignore
    private double parsePriceFromText(String priceText) {
        if (priceText == null || priceText.isEmpty()) return 0;

        try {
            String cleaned = priceText.replaceAll("[^0-9.]", "");
            double value = Double.parseDouble(cleaned);
            if (priceText.toLowerCase().contains("triệu")) {
                value = value * 1000000;
            }
            return value;
        } catch (Exception e) {
            return 0;
        }
    }

    @Ignore
    public String getFormattedPrice() {
        if (priceValue >= 1000000) {
            double millions = priceValue / 1000000;
            return String.format("%.1f triệu/tháng", millions);
        } else if (priceValue >= 1000) {
            double thousands = priceValue / 1000;
            return String.format("%.0f nghìn/tháng", thousands);
        } else {
            return String.format("%.0f đ/tháng", priceValue);
        }
    }

    @Ignore
    public String getFormattedArea() {
        if (area == (int) area) {
            return String.format("%dm²", (int) area);
        } else {
            return String.format("%.1fm²", area);
        }
    }

    @Ignore
    public String getFormattedRoomRating() {
        if (roomReviewCount > 0) {
            return String.format("%.1f (%d)", roomRating, roomReviewCount);
        } else {
            return "Chưa có đánh giá";
        }
    }

    @Ignore
    public String getFormattedLandlordRating() {
        if (landlordReviewCount > 0) {
            return String.format("%.1f (%d đánh giá)", landlordRating, landlordReviewCount);
        } else {
            return "Chưa có đánh giá";
        }
    }

    @Ignore
    public boolean hasRoomRating() {
        return roomReviewCount > 0 && roomRating > 0;
    }

    @Ignore
    public boolean hasLandlordRating() {
        return landlordReviewCount > 0 && landlordRating > 0;
    }

    // ========== GETTERS ==========
    public int getId() { return id; }
    public String getTitle() { return title; }
    public double getPriceValue() { return priceValue; }
    public String getLocation() { return location; }
    public String getAddress() { return address; }
    public double getArea() { return area; }
    public String getDescription() { return description; }
    public int getImageResId() { return imageResId; }
    public String getImageUrl() { return imageUrl; }
    public double getRoomRating() { return roomRating; }
    public int getRoomReviewCount() { return roomReviewCount; }
    public int getLandlordId() { return landlordId; }
    public String getLandlordName() { return landlordName; }
    public double getLandlordRating() { return landlordRating; }
    public int getLandlordReviewCount() { return landlordReviewCount; }
    public String getLandlordPhone() { return landlordPhone; }
    public boolean isNew() { return isNew; }
    public boolean isPromo() { return isPromo; }
    public boolean isSaved() { return isSaved; }
    public String getCreatedDate() { return createdDate; }
    public String getRoomType() { return roomType; }
    public ArrayList<String> getAmenities() { return amenities; }

    // ========== SETTERS ==========
    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setPriceValue(double priceValue) { this.priceValue = priceValue; }
    public void setLocation(String location) { this.location = location; }
    public void setAddress(String address) { this.address = address; }
    public void setArea(double area) { this.area = area; }
    public void setDescription(String description) { this.description = description; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setRoomRating(double roomRating) { this.roomRating = roomRating; }
    public void setRoomReviewCount(int roomReviewCount) { this.roomReviewCount = roomReviewCount; }
    public void setLandlordId(int landlordId) { this.landlordId = landlordId; }
    public void setLandlordName(String landlordName) { this.landlordName = landlordName; }
    public void setLandlordRating(double landlordRating) { this.landlordRating = landlordRating; }
    public void setLandlordReviewCount(int landlordReviewCount) { this.landlordReviewCount = landlordReviewCount; }
    public void setLandlordPhone(String landlordPhone) { this.landlordPhone = landlordPhone; }
    public void setNew(boolean isNew) { this.isNew = isNew; }
    public void setPromo(boolean isPromo) { this.isPromo = isPromo; }
    public void setSaved(boolean saved) { isSaved = saved; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
    public void setAmenities(ArrayList<String> amenities) { this.amenities = amenities; }

    @Deprecated
    @Ignore
    public String getPrice() {
        return getFormattedPrice();
    }

    @Deprecated
    @Ignore
    public String getRating() {
        return getFormattedRoomRating();
    }
}
