package com.example.QuanLyPhongTro_App.data.response;

import com.google.gson.annotations.SerializedName;

/**
 * Login response - chứa JWT token và user info
 * Response shape: { "Token": "...", "VaiTroId": 3, "Email": "...", "HoTen": "..." }
 */
public class LoginResponse {
    @SerializedName("Token")
    public String token;

    @SerializedName("token")
    public String tokenLowercase; // fallback if backend returns lowercase

    @SerializedName("VaiTroId")
    public int vaiTroId;

    @SerializedName("Email")
    public String email;

    @SerializedName("HoTen")
    public String hoTen;

    @SerializedName("UserId")
    public String userId;

    public String getToken() {
        return token != null ? token : tokenLowercase;
    }

    /**
     * Convert VaiTroId to role string (1=Admin, 2=ChuTro, 3=NguoiThue)
     */
    public String getRoleString() {
        switch (vaiTroId) {
            case 1:
                return "admin";
            case 2:
                return "landlord";
            case 3:
                return "tenant";
            default:
                return "tenant";
        }
    }

    public LoginResponse() {}

    public LoginResponse(String token) {
        this.token = token;
    }
}



