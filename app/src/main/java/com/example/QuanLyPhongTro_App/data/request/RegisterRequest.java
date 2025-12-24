package com.example.QuanLyPhongTro_App.data.request;

import com.google.gson.annotations.SerializedName;

/**
 * Register request model
 * - VaiTroId: 2 = ChuTro (landlord), 3 = NguoiThue (tenant)
 */
public class RegisterRequest {
    @SerializedName("Email")
    public String email;

    @SerializedName("Password")
    public String password;

    @SerializedName("DienThoai")
    public String dienThoai;

    @SerializedName("HoTen")
    public String hoTen;

    @SerializedName("VaiTroId")
    public int vaiTroId; // 2 = landlord, 3 = tenant

    public RegisterRequest(String email, String password, String dienThoai, String hoTen, int vaiTroId) {
        this.email = email;
        this.password = password;
        this.dienThoai = dienThoai;
        this.hoTen = hoTen;
        this.vaiTroId = vaiTroId;
    }
}

