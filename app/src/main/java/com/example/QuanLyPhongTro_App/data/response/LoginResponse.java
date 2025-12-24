package com.example.QuanLyPhongTro_App.data.response;

import com.google.gson.annotations.SerializedName;

/**
 * Login response - chứa JWT token
 * Response shape: { "Token": "eyJhbGciOi..." }
 */
public class LoginResponse {
    @SerializedName("Token")
    public String token;

    @SerializedName("token")
    public String tokenLowercase; // fallback if backend returns lowercase

    public String getToken() {
        return token != null ? token : tokenLowercase;
    }

    public LoginResponse() {}

    public LoginResponse(String token) {
        this.token = token;
    }
}

