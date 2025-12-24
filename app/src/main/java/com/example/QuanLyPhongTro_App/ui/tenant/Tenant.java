package com.example.QuanLyPhongTro_App.ui.tenant;

import java.io.Serializable;
import java.util.Date;

public class Tenant implements Serializable {
    private String nguoiDungId;
    private String email;
    private String dienThoai;
    private boolean isEmailXacThuc;
    private String hoTen;
    private Date ngaySinh;
    private String loaiGiayTo;
    private String ghiChu;

    // Constructors
    public Tenant() {}

    public Tenant(String nguoiDungId, String email, String hoTen) {
        this.nguoiDungId = nguoiDungId;
        this.email = email;
        this.hoTen = hoTen;
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

    public String getDienThoai() {
        return dienThoai;
    }

    public void setDienThoai(String dienThoai) {
        this.dienThoai = dienThoai;
    }

    public boolean isEmailXacThuc() {
        return isEmailXacThuc;
    }

    public void setIsEmailXacThuc(boolean isEmailXacThuc) {
        this.isEmailXacThuc = isEmailXacThuc;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public Date getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(Date ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getLoaiGiayTo() {
        return loaiGiayTo;
    }

    public void setLoaiGiayTo(String loaiGiayTo) {
        this.loaiGiayTo = loaiGiayTo;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    @Override
    public String toString() {
        return "Tenant{" +
                "nguoiDungId='" + nguoiDungId + '\'' +
                ", email='" + email + '\'' +
                ", hoTen='" + hoTen + '\'' +
                ", isEmailXacThuc=" + isEmailXacThuc +
                '}';
    }
}