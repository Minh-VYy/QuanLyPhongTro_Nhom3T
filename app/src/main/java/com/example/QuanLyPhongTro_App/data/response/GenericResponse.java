package com.example.QuanLyPhongTro_App.data.response;

import com.google.gson.annotations.SerializedName;

/**
 * Generic API response wrapper
 * { "success": true/false, "Data": {...}, "Message": "..." }
 */
public class GenericResponse<T> {
    @SerializedName("success")
    public boolean success;

    @SerializedName("Data")
    public T data;

    @SerializedName("Message")
    public String message;

    public GenericResponse() {}

    public GenericResponse(boolean success, T data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }
}

