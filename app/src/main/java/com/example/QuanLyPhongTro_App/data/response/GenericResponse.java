package com.example.QuanLyPhongTro_App.data.response;

import com.google.gson.annotations.SerializedName;

/**
 * Generic API response wrapper
 * Matches C# backend format with PascalCase fields
 * Example: { "success": true, "Data": {...}, "Message": "...", "TotalCount": 10, ... }
 */
public class GenericResponse<T> {
    @SerializedName(value = "success", alternate = {"Success", "Succeeded"})
    public Boolean success;

    // C# returns "Data" (PascalCase)
    @SerializedName(value = "Data", alternate = {"data"})
    public T data;

    @SerializedName(value = "Message", alternate = {"message", "MESSAGE"})
    public String message;

    // Pagination fields from C# backend
    @SerializedName("TotalCount")
    public Integer totalCount;

    @SerializedName("Page")
    public Integer page;

    @SerializedName("PageSize")
    public Integer pageSize;

    @SerializedName("TotalPages")
    public Integer totalPages;

    public GenericResponse() {}

    public GenericResponse(boolean success, T data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }
}
