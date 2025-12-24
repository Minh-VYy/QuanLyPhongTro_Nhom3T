package com.example.QuanLyPhongTro_App.data.request;

import com.google.gson.annotations.SerializedName;

/**
 * Login request model
 */
public class LoginRequest {
    @SerializedName("Email")
    public String email;

    @SerializedName("Password")
    public String password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}


