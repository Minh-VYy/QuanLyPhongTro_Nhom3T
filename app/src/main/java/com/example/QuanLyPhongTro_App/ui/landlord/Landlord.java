package com.example.QuanLyPhongTro_App.ui.landlord;

public class Landlord {
    private String nguoiDungId; // Added to store the User ID
    private String email;
    private String passwordHash;
    private String hoTen;
    private String dienThoai;

    public Landlord(String nguoiDungId, String email, String passwordHash, String hoTen, String dienThoai) {
        this.nguoiDungId = nguoiDungId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.hoTen = hoTen;
        this.dienThoai = dienThoai;
    }

    // Getters and Setters
    public String getNguoiDungId() {
        return nguoiDungId;
    }

    public void setNguoiDungId(String nguoiDungId) {
        this.nguoiDungId = nguoiDungId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getDienThoai() {
        return dienThoai;
    }

    public void setDienThoai(String dienThoai) {
        this.dienThoai = dienThoai;
    }
}
